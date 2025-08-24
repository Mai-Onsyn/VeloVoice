import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.TOCReference;
import nl.siegmann.epublib.epub.EpubReader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class Test2 {
    public static void main(String[] args) throws IOException {

        EpubReader epubReader = new EpubReader();

        Book book = epubReader.readEpub(new FileInputStream("D:\\Users\\Desktop\\我们不可能成为恋人！绝对不行2\\epub1-6卷+联动短篇\\1.epub"));

        List<TOCReference> tocReferences = book.getTableOfContents().getTocReferences();
        printToc(tocReferences);
    }


    private static void printToc(List<TOCReference> references) {
        for (TOCReference reference : references) {

            System.out.printf("title: %s%n", reference.getTitle());

            if (reference.getChildren() != null && !reference.getChildren().isEmpty()) {
                printToc(reference.getChildren());
            }
        }
    }
}
