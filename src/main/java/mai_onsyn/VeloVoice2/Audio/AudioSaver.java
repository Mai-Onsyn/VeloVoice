package mai_onsyn.VeloVoice2.Audio;

import com.jonathanedgecombe.srt.InvalidTimestampFormatException;
import com.jonathanedgecombe.srt.Subtitle;
import com.jonathanedgecombe.srt.SubtitleFile;
import com.jonathanedgecombe.srt.Timestamp;
import mai_onsyn.VeloVoice2.Text.Sentence;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class AudioSaver {

    private static final int UNIT_DURATION = 6;   // byte length of 1 millisecond

    public static void save(List<Sentence> sentences, File file, String name) {
        File mp3File = new File(file, name + ".mp3");

        try {
            if (!mp3File.getParentFile().exists()) mp3File.getParentFile().mkdirs();
            if (!mp3File.exists()) mp3File.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (FileOutputStream fos = new FileOutputStream(mp3File)) {
            for (Sentence sentence : sentences) {
                fos.write(sentence.getAudio());
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        saveSRT(sentences, file, name);
    }

    private static void saveSRT(List<Sentence> sentences, File file, String name) {
        SubtitleFile srt = new SubtitleFile();
        int tick = 0;

        try {

            for (Sentence sentence : sentences) {
                Subtitle subtitle = new Subtitle(new Timestamp(formatDuration(tick / UNIT_DURATION)), new Timestamp(formatDuration((tick+=sentence.countBytes()) / UNIT_DURATION)));

                subtitle.addLine(sentence.getText());

                srt.addSubtitle(subtitle);
            }

            srt.save(new File(file, name + ".srt"));

        } catch (InvalidTimestampFormatException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String formatDuration(long milliseconds) {

        long h = milliseconds / 3600000;
        long m = (milliseconds % 3600000) / 60000;
        long s = ((milliseconds % 3600000) % 60000) / 1000;
        long S = milliseconds % 1000;

        return String.format("%02d:%02d:%02d,%03d", h, m, s, S);
    }

}
