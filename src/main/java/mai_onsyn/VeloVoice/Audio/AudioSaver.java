package mai_onsyn.VeloVoice.Audio;

import com.jonathanedgecombe.srt.InvalidTimestampFormatException;
import com.jonathanedgecombe.srt.Subtitle;
import com.jonathanedgecombe.srt.SubtitleFile;
import com.jonathanedgecombe.srt.Timestamp;
import mai_onsyn.AnimeFX.I18N;
import mai_onsyn.VeloVoice.NetWork.TTS.ResumableTTSClient;
import mai_onsyn.VeloVoice.Text.Sentence;
import mai_onsyn.VeloVoice.Text.TextUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static mai_onsyn.VeloVoice.App.Runtime.textConfig;
import static mai_onsyn.VeloVoice.App.Runtime.voiceConfig;

public class AudioSaver {

    public static final int MP3_24KHZ_16BIT_UNIT_DURATION = 6;   //mp3 byte length of 1 millisecond
    public static final int WAV_24KHZ_16BIT_UNIT_DURATION = 48;
    public static final int WAV_22KHZ_16BIT_UNIT_DURATION = 44;
    private static final String illegalChars = "[\\\\/:*?\"<>|]";

    private static final Logger log = LogManager.getLogger(AudioSaver.class);

    public static void save(List<Sentence> sentences, File folder, String filename) throws IOException {
        AudioEncodeUtils.AudioFormat audioFormat = sentences.getFirst().audioFormat();

        String name = filename.replaceAll(illegalChars, "_");

        final int unitDuration = getUnitDuration(audioFormat);
        final int sectionByteLength = voiceConfig.getInteger("SectionLength") * 60000 * unitDuration;

        if (voiceConfig.getBoolean("SaveInSections") && countLength(sentences) > sectionByteLength) {
            log.debug("Save in sections");
            int byteLength = 0;
            int i = 1;
            final List<Sentence> saveTemp = new ArrayList<>();

            String sectionUnitNamePattern = String.format("%s%s.%s", name, voiceConfig.getString("SectionUnitPattern"), audioFormat.getFormat());
            for (Sentence sentence : sentences) {
                if (byteLength > sectionByteLength) {
                    byteLength = 0;
                    saveAudio(saveTemp, new File(folder, String.format(sectionUnitNamePattern, i)), audioFormat);
                    saveTemp.clear();

                    //if enabled, every section will read the first sentence of the file
                    if (voiceConfig.getBoolean("SectionReadHead")) {
                        try {
                            ResumableTTSClient fixedEdgeTTSClient = new ResumableTTSClient();
                            fixedEdgeTTSClient.establish();
                            Sentence head = fixedEdgeTTSClient.process(String.format(sentences.getFirst().text() + voiceConfig.getString("SectionUnitPattern"), ++i));
                            byteLength += head.audioByteArray().length;
                            saveTemp.add(head);
                            fixedEdgeTTSClient.terminate();
                        } catch (Exception e) {
                            log.warn("Can't read the first sentence because {}, In file {} will be ignored", e.getMessage(), name);
                        }
                    }
                }

                saveTemp.add(sentence);
                byteLength += sentence.audioByteArray().length;
            }
            saveAudio(saveTemp, new File(folder, String.format(sectionUnitNamePattern, i)), audioFormat);

        } else {
            File audioFile = new File(folder, name + "." + audioFormat.getFormat());
            saveAudio(sentences, audioFile, audioFormat);

            if (voiceConfig.getBoolean("SaveSRT")) saveSRT(sentences, folder, name);
        }
    }

    private static void saveAudio(List<Sentence> sentences, File audioFile, AudioEncodeUtils.AudioFormat audioFormat) {
        try {
            if (!audioFile.getParentFile().exists()) audioFile.getParentFile().mkdirs();
            if (!audioFile.exists()) audioFile.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (FileOutputStream fos = new FileOutputStream(audioFile)) {

            if (audioFormat == AudioEncodeUtils.AudioFormat.WAV_24KHZ_16BIT) fos.write(AudioEncodeUtils.genWavHeader(countLength(sentences), 24000, 1));
            else if (audioFormat == AudioEncodeUtils.AudioFormat.WAV_22KHZ_16BIT) fos.write(AudioEncodeUtils.genWavHeader(countLength(sentences), 22000, 1));

            for (Sentence sentence : sentences) {
                fos.write(sentence.audioByteArray());
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void saveSRT(List<Sentence> sentences, File file, String name) {
        SubtitleFile srt = new SubtitleFile();
        int tick = 0;

        try {

            for (Sentence sentence : sentences) {
                final int unitDuration = getUnitDuration(sentence.audioFormat());
                Subtitle subtitle = new Subtitle(new Timestamp(formatDuration(tick / unitDuration)), new Timestamp(formatDuration((tick+=sentence.audioByteArray().length) / unitDuration)));

                if (voiceConfig.getBoolean("DeleteSRTEndSymbol")) subtitle.addLine(TextUtil.deleteEndSymbol(sentence.text()));
                else subtitle.addLine(sentence.text());

                srt.addSubtitle(subtitle);
            }

            File srtFile = new File(file, name + ".srt");
            log.debug("Saving SRT file to {}", srtFile.getAbsolutePath());
            srt.save(srtFile);

        } catch (InvalidTimestampFormatException | IOException e) {
            log.error(I18N.getCurrentValue("log.audio_saver.error.srt_save_failed"), e);
            throw new RuntimeException(e);
        }
    }

    private static int getUnitDuration(AudioEncodeUtils.AudioFormat sentence) {
        return switch (sentence) {
            case MP3_24KHZ_16BIT -> MP3_24KHZ_16BIT_UNIT_DURATION;
            case WAV_24KHZ_16BIT -> WAV_24KHZ_16BIT_UNIT_DURATION;
            case WAV_22KHZ_16BIT -> WAV_22KHZ_16BIT_UNIT_DURATION;
        };
    }

    private static String formatDuration(long milliseconds) {

        long h = milliseconds / 3600000;
        long m = (milliseconds % 3600000) / 60000;
        long s = ((milliseconds % 3600000) % 60000) / 1000;
        long S = milliseconds % 1000;

        return String.format("%02d:%02d:%02d,%03d", h, m, s, S);
    }

    static int countLength(List<Sentence> s) {
        return s.stream().mapToInt(sentence -> sentence.audioByteArray().length).sum();
    }

    @Deprecated
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
