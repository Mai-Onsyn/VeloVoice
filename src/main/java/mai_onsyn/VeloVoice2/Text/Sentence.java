package mai_onsyn.VeloVoice2.Text;

import mai_onsyn.VeloVoice2.Audio.AudioEncodeUtils;

import java.util.List;

public record Sentence(String text, byte[] audioByteArray, List<Word> wordList, AudioEncodeUtils.AudioFormat audioFormat) {
    public static byte[] mergeAudio(List<byte[]> audioList) {
        int length = 0;
        for (byte[] audio : audioList) {
            length += audio.length;
        }
        byte[] result = new byte[length];
        int offset = 0;
        for (byte[] audio : audioList) {
            System.arraycopy(audio, 0, result, offset, audio.length);
            offset += audio.length;
        }
        return result;
    }

    public record Word(String word, int start, int end) {}
}
