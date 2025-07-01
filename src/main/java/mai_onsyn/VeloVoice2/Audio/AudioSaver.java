package mai_onsyn.VeloVoice2.Audio;

import com.jonathanedgecombe.srt.InvalidTimestampFormatException;
import com.jonathanedgecombe.srt.Subtitle;
import com.jonathanedgecombe.srt.SubtitleFile;
import com.jonathanedgecombe.srt.Timestamp;
import mai_onsyn.VeloVoice2.Text.Sentence;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static mai_onsyn.VeloVoice2.App.Runtime.voiceConfig;

public class AudioSaver {

    private static final int MP3_UNIT_DURATION = 6;   //mp3 byte length of 1 millisecond
    private static final int WAV_UNIT_DURATION = 48;   //pcm byte length of 1 millisecond

    private static final Logger log = LogManager.getLogger(AudioSaver.class);

    public static void save(List<Sentence> sentences, File file, String name) {
        AudioEncodeUtils.AudioFormat audioFormat = sentences.getFirst().audioFormat();
        File audioFile = new File(file, name + "." + audioFormat.getFormat());

        try {
            if (!audioFile.getParentFile().exists()) audioFile.getParentFile().mkdirs();
            if (!audioFile.exists()) audioFile.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (FileOutputStream fos = new FileOutputStream(audioFile)) {

            if (audioFormat == AudioEncodeUtils.AudioFormat.WAV) fos.write(AudioEncodeUtils.genWavHeader(countLength(sentences), 24000, 1));

            for (Sentence sentence : sentences) {
                fos.write(sentence.audioByteArray());
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //saveWord(sentences.get(0), file);
        if (voiceConfig.getBoolean("SaveSRT")) saveSRT(sentences, file, name);
    }

    private static void saveSRT(List<Sentence> sentences, File file, String name) {
        SubtitleFile srt = new SubtitleFile();
        int tick = 0;

        try {

            for (Sentence sentence : sentences) {
                boolean isMP3 = sentence.audioFormat() == AudioEncodeUtils.AudioFormat.MP3;
                Subtitle subtitle = new Subtitle(new Timestamp(formatDuration(tick / (isMP3 ? MP3_UNIT_DURATION : WAV_UNIT_DURATION))), new Timestamp(formatDuration((tick+=sentence.audioByteArray().length) / (isMP3 ? MP3_UNIT_DURATION : WAV_UNIT_DURATION))));

                subtitle.addLine(sentence.text());

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

    private static int countLength(List<Sentence> s) {
        return s.stream().mapToInt(sentence -> sentence.audioByteArray().length).sum();
    }

    private static void saveWord(Sentence sentence, File folder) {
        sentence.wordList().forEach(word -> {
            File file = new File(folder, sentence.text() + "_" + word.word() + ".mp3");

            try (FileOutputStream fos = new FileOutputStream(file)) {

                fos.write(Arrays.copyOfRange(sentence.audioByteArray(), (int) word.start(), (int) word.end()));

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

}
