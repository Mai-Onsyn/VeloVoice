package mai_onsyn.VeloVoice2.Audio;

import mai_onsyn.VeloVoice2.Text.Sentence;

import java.util.ArrayList;
import java.util.List;

import static mai_onsyn.VeloVoice2.App.Runtime.voiceConfig;

public class AfterEffects {

    public static List<Sentence> process(List<Sentence> sentences) {

        List<Sentence> s1 = adjustAudio(sentences);

        return s1;

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

            result.add(new Sentence(sentence.text(), shiftedPCM, newWords, AudioEncodeUtils.AudioFormat.WAV));
        });

        return result;
    }

}
