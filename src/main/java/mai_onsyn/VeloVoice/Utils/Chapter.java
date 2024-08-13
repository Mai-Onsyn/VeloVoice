package mai_onsyn.VeloVoice.Utils;

import java.util.List;

public class Chapter {

    private String title;

    private List<String> contents;

    public static Chapter parseChapter(List<String> texts) {

        Chapter chapter = new Chapter();
        chapter.contents = texts;
        chapter.title = texts.getFirst();

        return chapter;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getContents() {
        return contents;
    }

}