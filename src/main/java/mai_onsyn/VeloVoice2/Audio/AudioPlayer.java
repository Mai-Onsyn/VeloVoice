package mai_onsyn.VeloVoice2.Audio;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static mai_onsyn.VeloVoice.App.Runtime.logger;

public class AudioPlayer {
    public static void play(byte[] mergedAudio) {
        try {
            try (ByteArrayInputStream stream = new ByteArrayInputStream(mergedAudio)) {
                Player player = new Player(stream);
                player.play();
            }
        } catch (JavaLayerException | IOException e) {
            logger.error("播放声音时出错：" + e);
            e.printStackTrace();
        }
    }
}
