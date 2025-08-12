package mai_onsyn.VeloVoice.Audio;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import mai_onsyn.AnimeFX.I18N;
import mai_onsyn.VeloVoice.Text.Sentence;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class AudioPlayer {
    private static final Logger log = LogManager.getLogger(AudioPlayer.class);

    public static void play(Sentence sentence) {
        try {

            if (sentence.audioFormat().name().contains("MP3")) {
                try (ByteArrayInputStream stream = new ByteArrayInputStream(sentence.audioByteArray())) {
                    Player player = new Player(stream);
                    log.info(I18N.getCurrentValue("log.audio_player.info.playing"));
                    player.play();
                }
            } else playPcmDirectly(sentence);
        } catch (JavaLayerException | IOException e) {
            log.error(I18N.getCurrentValue("log.audio_player.error.failed"), e);
            throw new RuntimeException(e);
        }
    }

    private static void playPcmDirectly(Sentence sentence) {
        byte[] pcmData = sentence.audioByteArray();
        AudioFormat format = switch (sentence.audioFormat()) {
            case WAV_24KHZ_16BIT -> new AudioFormat(24000, 16, 1, true, false);
            case WAV_22KHZ_16BIT -> new AudioFormat(22000, 16, 1, true, false);
            case null, default -> null;
        };
        if (format == null) return;

        try (SourceDataLine line = AudioSystem.getSourceDataLine(format)) {
            line.open(format);
            line.start();
            line.write(pcmData, 0, pcmData.length);
            line.drain();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
