package mai_onsyn.VeloVoice.Utils;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import static mai_onsyn.VeloVoice.App.Runtime.logger;

public class AudioPlayer {

    public static void play(List<byte[]> byteArrayList) {
        try {
            byte[] mergedAudio = mergeByteArrays(byteArrayList);

            try (ByteArrayInputStream stream = new ByteArrayInputStream(mergedAudio)) {
                Player player = new Player(stream);
                player.play();
            }
        } catch (JavaLayerException | IOException e) {
            logger.error("播放声音时出错：" + e);
            e.printStackTrace();
        }
    }

    private static byte[] mergeByteArrays(List<byte[]> byteArrayList) {
        int totalLength = 0;
        for (byte[] byteArray : byteArrayList) {
            totalLength += byteArray.length;
        }

        byte[] result = new byte[totalLength];
        int currentIndex = 0;
        for (byte[] byteArray : byteArrayList) {
            System.arraycopy(byteArray, 0, result, currentIndex, byteArray.length);
            currentIndex += byteArray.length;
        }
        return result;
    }
}
