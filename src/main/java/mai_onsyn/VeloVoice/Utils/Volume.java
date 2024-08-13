package mai_onsyn.VeloVoice.Utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Volume {

    private final String volumeName;

    private final List<Chapter> chapters = new ArrayList<>();

    public Volume(String volumeName) {
        this.volumeName = volumeName;
    }

    public void addChapter(Chapter... chapter) {
        this.chapters.addAll(List.of(chapter));
    }

    public List<Chapter> getChapters() {
        return chapters;
    }

    public String getVolumeName() {
        return volumeName;
    }

    public void writeToFile(File parent) {
        for (int i = 0; i < chapters.size(); i++) {
            File output = new File(parent, String.format("%s/%d. %s.txt", volumeName, i + 1, chapters.get(i).getTitle()));
            output.getParentFile().mkdirs();
            try (FileWriter fw = new FileWriter(output)) {
                List<String> contents = chapters.get(i).getContents();
                fw.write(contents.getFirst());
                fw.write("\n\n");
                for (int j = 1; j < contents.size(); j++) {
                    fw.write("   " + contents.get(j) + "\n\n");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
