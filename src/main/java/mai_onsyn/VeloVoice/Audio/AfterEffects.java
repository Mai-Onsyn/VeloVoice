package mai_onsyn.VeloVoice.Audio;

import mai_onsyn.AnimeFX.I18N;
import mai_onsyn.VeloVoice.Text.Sentence;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

import static mai_onsyn.VeloVoice.App.Runtime.voiceConfig;

public class AfterEffects {

    private static final Logger log = LogManager.getLogger(AfterEffects.class);

    public static List<Sentence> process(List<Sentence> sentences) {
        if (voiceConfig.getBoolean("EnableVoiceShift")) return adjustAudio(sentences);
        else {
            log.debug("Skip audio adjust");
            return sentences;
        }
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

}
