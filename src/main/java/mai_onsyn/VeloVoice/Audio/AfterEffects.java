package mai_onsyn.VeloVoice.Audio;

import mai_onsyn.VeloVoice.Text.Sentence;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static mai_onsyn.VeloVoice.App.Runtime.voiceConfig;

public class AfterEffects {

    private static final Logger log = LogManager.getLogger(AfterEffects.class);

    public static List<Sentence> process(List<Sentence> sentences) throws IOException {
        List<Sentence> result;

        if (voiceConfig.getBoolean("EnableVoiceShift")) result = adjustAudio(sentences);
        else {
            log.debug("Skip audio adjust");
            result = sentences;
        }

        if (voiceConfig.getBoolean("ClipAudio")) {
            result = clipAudio(result, voiceConfig.getInteger("StartClipMillis"), CutType.START);
            result = clipAudio(result, voiceConfig.getInteger("EndClipMillis"), CutType.END);
        }

        return result;
    }


    /**
     * 音频变速与变调
     */
    private static List<Sentence> adjustAudio(List<Sentence> source) {

        List<Sentence> result = new ArrayList<>(source.size());
        double voiceRate = voiceConfig.getDouble("VoiceRate");
        double voicePitch = voiceConfig.getDouble("VoicePitch");

        source.forEach(sentence -> {
            byte[] sourceData = sentence.audioByteArray();
            int sourceLength = sourceData.length;

            byte[] shiftedPCM = AudioEncodeUtils.speed_rateShift(sourceData, voiceRate / voicePitch, 1 / voicePitch);
            int shiftedLength = shiftedPCM.length;

            double ratio = (double) shiftedLength / sourceLength;

            List<Sentence.Word> newWords = new ArrayList<>();
            sentence.wordList().forEach(word -> {
                newWords.add(new Sentence.Word(word.word(), (int) (word.start() * ratio), (int) (word.end() * ratio)));
            });

            result.add(new Sentence(sentence.text(), shiftedPCM, newWords, AudioEncodeUtils.AudioFormat.WAV_24KHZ_16BIT));
        });

        return result;
    }

    private enum CutType {
        START, END
    }

    /**
     * 切片删减
     */
    private static List<Sentence> clipAudio(List<Sentence> source, int millis, CutType cutType) throws IOException {
        List<Sentence> result = new ArrayList<>(source.size());

        for (Sentence sentence : source) {
            byte[] pcm = switch (sentence.audioFormat()) {
                case MP3_24KHZ_16BIT -> AudioEncodeUtils.decodeMp3ToPcm(sentence.audioByteArray());
                case WAV_22KHZ_16BIT -> AudioEncodeUtils.wavRedecode(new AudioEncodeUtils.WavInfo(22000, 16, 1, sentence.audioByteArray()), 24000, 16, 1);
                case WAV_24KHZ_16BIT -> sentence.audioByteArray();
            };

            int cutLength = AudioSaveUtil.WAV_24KHZ_16BIT_UNIT_DURATION * millis;
            int decodedLength = pcm.length > cutLength ? pcm.length - cutLength : 0;
            byte[] decodedPCM = new byte[decodedLength];

            switch (cutType) {
                case START -> System.arraycopy(pcm, pcm.length > cutLength ? cutLength : 0, decodedPCM, 0, decodedLength);
                case END -> System.arraycopy(pcm, 0, decodedPCM, 0, decodedLength);
            }

            result.add(new Sentence(sentence.text(), decodedPCM, List.of(new Sentence.Word(sentence.text(), 0, decodedLength)), AudioEncodeUtils.AudioFormat.WAV_24KHZ_16BIT));
        }

        return result;
    }
}
