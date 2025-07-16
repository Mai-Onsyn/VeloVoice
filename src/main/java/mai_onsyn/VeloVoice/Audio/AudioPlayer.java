package mai_onsyn.VeloVoice.Audio;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import mai_onsyn.AnimeFX.I18N;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class AudioPlayer {
    private static final Logger log = LogManager.getLogger(AudioPlayer.class);

    public static void play(byte[] mergedAudio) {
        try {
            try (ByteArrayInputStream stream = new ByteArrayInputStream(mergedAudio)) {
                Player player = new Player(stream);
                log.info(I18N.getCurrentValue("log.audio_player.info.playing"));
                player.play();
            }
        } catch (JavaLayerException | IOException e) {
            log.error(I18N.getCurrentValue("log.audio_player.error.failed"), e);
            throw new RuntimeException(e);
        }
    }
}
