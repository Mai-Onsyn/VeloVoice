import mai_onsyn.VeloVoice.Text.TextUtil;
import nl.siegmann.epublib.domain.*;
import nl.siegmann.epublib.epub.EpubWriter;

import java.io.*;
import java.util.List;

public class Test2 {

    public static void main(String[] args) throws IOException {
        //Test1.main(args);
        Book book = new Book();

        Metadata metadata = book.getMetadata();
        metadata.addTitle("Test Book");

        TableOfContents tableOfContents = book.getTableOfContents();
        Resource folderResource = new Resource(TextUtil.createHtmlChapter("Folder1", List.of("Test folder 1")).getBytes(), "static_folder_resource.xhtml");
        TOCReference folder1 = new TOCReference("Folder 1", folderResource);
        TOCReference tocFolder1 = tableOfContents.addTOCReference(folder1);

        Resource fileResource1 = new Resource(TextUtil.createHtmlChapter("File1", List.of("Test file content 1")).getBytes(), "text/file1.xhtml");
        TOCReference file1 = new TOCReference("File 1", fileResource1);
        TOCReference tocFile1 = tocFolder1.addChildSection(file1);

        TOCReference folder2 = new TOCReference("Folder 2", folderResource);
        TOCReference tocFolder2 = tableOfContents.addTOCReference(folder2);

        Resource fileResource2 = new Resource(TextUtil.createHtmlChapter("File2", List.of("Test file content 2")).getBytes(), "text/file2.xhtml");
        TOCReference file2 = new TOCReference("File 2", fileResource2);
        TOCReference tocFile2 = tocFolder2.addChildSection(file2);

        book.addResource(folderResource);
        book.addResource(fileResource1);
        book.addResource(fileResource2);

        book.getSpine().addSpineReference(new SpineReference(fileResource1));
        book.getSpine().addSpineReference(new SpineReference(fileResource2));

        EpubWriter epubWriter = new EpubWriter();
        epubWriter.write(book, new FileOutputStream("D:/Users/Desktop/test.epub"));
    }
}
