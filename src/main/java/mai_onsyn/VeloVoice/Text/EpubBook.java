package mai_onsyn.VeloVoice.Text;

import javafx.beans.property.SimpleStringProperty;
import mai_onsyn.AnimeFX.Utls.AXDataTreeItem;
import mai_onsyn.AnimeFX.Utls.AXTreeItem;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.SpineReference;
import nl.siegmann.epublib.domain.TOCReference;
import nl.siegmann.epublib.epub.EpubWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class EpubBook extends Book {

    private static final Logger log = LogManager.getLogger(EpubBook.class);
    private AXTreeItem bookTree;

    public void setBookTree(AXTreeItem bookTree) {
        this.bookTree = bookTree;
    }

    public void write(String url) throws IOException {
        TOCReference root = new TOCReference();
        addFolder(root, bookTree, "Text");
        root.getChildren().forEach(child -> super.getTableOfContents().addTOCReference(child));

        EpubWriter epubWriter = new EpubWriter();
        epubWriter.write(this, new FileOutputStream(url));
    }



    private void addFolder(TOCReference tocParent, AXTreeItem treeParent, String path) {
        if (treeParent instanceof AXDataTreeItem<?> dataTreeItem) {
            if (dataTreeItem.getData() instanceof SimpleStringProperty content) {
                String fileName = dataTreeItem.getHeadName();
                String filePath = path + "/" + fileName + ".xhtml";

                Resource resource = new Resource(TextUtil.createHtmlChapter(fileName, TextUtil.splitWrap(content.get())).getBytes(StandardCharsets.UTF_8), filePath);

                tocParent.addChildSection(new TOCReference(dataTreeItem.getHeadName(), resource));
                super.addResource(resource);
                super.getSpine().addSpineReference(new SpineReference(resource));
            }
            return;
        }

        for (AXTreeItem child : treeParent.getChildrenAsItem()) {
            String fileName = child.getHeadName();
            if (child instanceof AXDataTreeItem<?> dataTreeItem) {
                if (dataTreeItem.getData() instanceof SimpleStringProperty content) {
                    String filePath = path + "/" + fileName + ".xhtml";

                    Resource resource = new Resource(TextUtil.createHtmlChapter(fileName, TextUtil.splitWrap(content.get())).getBytes(StandardCharsets.UTF_8), filePath);
                    tocParent.addChildSection(new TOCReference(dataTreeItem.getHeadName(), resource));
                    super.addResource(resource);
                    super.getSpine().addSpineReference(new SpineReference(resource));
                }
            } else {
                String folderPath = path + "/" + fileName;
                Resource folderResource = new Resource(TextUtil.createHtmlChapter(fileName, List.of()).getBytes(), "static_folder_resources/" + folderPath + ".xhtml");
                addFolder(tocParent.addChildSection(new TOCReference(fileName, folderResource)), child, folderPath);
                super.addResource(folderResource);
                super.getSpine().addSpineReference(new SpineReference(folderResource));
            }
        }
    }

}
