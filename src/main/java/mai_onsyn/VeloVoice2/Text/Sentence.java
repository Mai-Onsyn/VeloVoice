package mai_onsyn.VeloVoice2.Text;

import java.util.List;

public record Sentence(String text, List<byte[]> audio) {
    public byte[] getAudio() {
        int length = 0;
        for (byte[] audio : audio) {
            length += audio.length;
        }
        byte[] result = new byte[length];
        int offset = 0;
        for (byte[] audio : audio) {
            System.arraycopy(audio, 0, result, offset, audio.length);
            offset += audio.length;
        }
        return result;
    }

    public String getText() {
        return text;
    }

    public int countBytes() {
        int count = 0;
        for (byte[] audio : audio) {
            count += audio.length;
        }
        return count;
    }

    public List<byte[]> getAudioList() {
        return audio;
    }
}
