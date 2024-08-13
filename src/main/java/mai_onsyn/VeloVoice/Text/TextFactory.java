package mai_onsyn.VeloVoice.Text;

import mai_onsyn.VeloVoice.App.AppConfig;
import mai_onsyn.VeloVoice.Utils.Chapter;
import mai_onsyn.VeloVoice.Utils.Volume;

import java.util.*;

public class TextFactory {

    public static Volume parseFromVolume(String s) {
        String[] sentence = s.split("\n");
        List<Chapter> chapters = new ArrayList<>();
        List<String> chapter = new ArrayList<>();
        for (String line : sentence) {
            if (line.startsWith("  ") && line.charAt(2) != ' ' && line.length() > 3) {
                if (!chapter.isEmpty()) chapters.add(Chapter.parseChapter(chapter));
                chapter = new ArrayList<>();

                String title = line.trim();
                chapter.add(title);
            }
            if (line.startsWith("    ")) collect(chapter, deleteSpace(line));

        }

        if (!chapter.isEmpty()) chapters.add(Chapter.parseChapter(chapter));

        List<String> titles = new ArrayList<>(chapters.size());
        for (Chapter c : chapters) {
            titles.add(c.getTitle());
        }

        Volume volume = new Volume(extractVolumeNames(titles).getFirst());

        chapters.forEach(volume::addChapter);
        return volume;
    }

    public static List<Volume> parseFromSeries(String s) {
        List<Volume> volumes = new ArrayList<>();
        List<String> lines = List.of(s.split("\n"));

        List<String> chapterTitles = new ArrayList<>();
        List<Integer> chapterIndexes = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (!line.startsWith(" ") && line.length() > 2) {
                chapterTitles.add(line);
                chapterIndexes.add(i);
            }
        }

        List<String> volumeNames = extractVolumeNames(chapterTitles);

        for (String volumeName : volumeNames) {
            Volume volume = new Volume(volumeName);
            int volumeStartIndex = -1;
            int volumeEndIndex = -1;

            for (int i = 0; i < chapterIndexes.size(); i++) {
                if (lines.get(chapterIndexes.get(i)).contains(volumeName)) {
                    if (volumeStartIndex == -1) {
                        volumeStartIndex = i;
                    }
                    volumeEndIndex = i;
                }
            }

            for (int i = volumeStartIndex; i <= volumeEndIndex; i++) {
                int startIdx = chapterIndexes.get(i);
                int endIdx = chapterIndexes.get(i + 1);
                List<String> chapterLines = lines.subList(startIdx, endIdx);

                List<String> chapterText = new ArrayList<>();
                for (String line : chapterLines) {
                    if (line.length() > 1) {
                        chapterText.add(line.trim());
                    }
                }
                volume.addChapter(Chapter.parseChapter(chapterText));
            }
            volumes.add(volume);
        }

        return volumes;
    }

    public static Chapter parseFromFormattedChapter(String s) {
        if (s == null || s.isEmpty()) return Chapter.parseChapter(new ArrayList<>(List.of("")));

        String[] sentences = s.split("\n");
        List<String> lines = new ArrayList<>();

        for (String sentence : sentences) {
            String trimmed = sentence.trim();
            if (trimmed.length() > 1) {
                //lines.add(trimmed);
                collect(lines, trimmed);
            }
        }

        return Chapter.parseChapter(lines);
    }

    private static String[] dichotomy(String s) {
        int halfLength = s.length() / 2;

        for (char c : AppConfig.textSplitSymbols) {
            for (int i = 0; i < halfLength - 2; i++) {
                if (s.charAt(halfLength + i) == c)
                    return new String[] {
                            s.substring(0, halfLength + 1 + i),
                            s.substring(halfLength + 1 + i)
                    };
                if (s.charAt(halfLength - i) == c)
                    return new String[] {
                            s.substring(0, halfLength + 1 - i),
                            s.substring(halfLength + 1 - i)
                    };
            }
        }
        return new String[] {
                s.substring(0, halfLength + 1),
                s.substring(halfLength + 1)
        };
    }

    private static void collect(List<String> parent, String s) {
        if (s.isEmpty()) return;
        if (s.length() < AppConfig.textPieceSize) {
            parent.add(s);
            return;
        }

        String[] ab = dichotomy(s);
        collect(parent, ab[0]);
        collect(parent, ab[1]);
    }

    private static List<String> extractVolumeNames(List<String> chapterHeads) {
        Set<String> names = new LinkedHashSet<>();

        List<Set<String>> sets = new ArrayList<>();
        for (String chapterHead : chapterHeads) {
            sets.add(new LinkedHashSet<>(List.of(chapterHead.split("\\s+"))));
        }
        for (int i = 0; i < sets.size() - 1; i++) {
            sets.get(i).retainAll(sets.get(i + 1));
            if (!sets.get(i).isEmpty()) {
                StringBuilder builder = new StringBuilder();
                for (String s : sets.get(i)) {
                    builder.append(s);
                    builder.append(" ");
                }
                names.add(builder.toString().trim());
            }
        }

        return names.stream().toList();
    }

    private static String deleteSpace(String s) {
        return s.replace(" ", "");
    }

    /*
    private static String extractVolumeName(List<String> chapterHeads) {
        if (chapterHeads == null || chapterHeads.isEmpty()) {
            return ""; // 如果没有输入字符串，返回空字符串
        }

        // 将第一个字符串按空格分割成组
        String[] firstParts = chapterHeads.getFirst().split("\\s+");
        Set<String> commonParts = new LinkedHashSet<>(List.of(firstParts)); // 使用LinkedHashSet保持顺序

        // 遍历后续字符串，找出它们的交集
        for (int i = 1; i < chapterHeads.size(); i++) {
            String[] parts = chapterHeads.get(i).split("\\s+");
            List<String> currentParts = List.of(parts);
            commonParts.retainAll(currentParts); // 取交集
        }

        // 将交集转换为字符串返回
        return String.join(" ", commonParts);
    }
     */
}
