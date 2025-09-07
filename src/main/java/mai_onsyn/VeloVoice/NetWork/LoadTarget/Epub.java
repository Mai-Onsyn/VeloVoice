package mai_onsyn.VeloVoice.NetWork.LoadTarget;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.image.Image;
import mai_onsyn.AnimeFX.I18N;
import mai_onsyn.AnimeFX.Module.AXTreeView;
import mai_onsyn.AnimeFX.Utls.AXDataTreeItem;
import mai_onsyn.AnimeFX.Utls.AXTreeItem;
import mai_onsyn.VeloVoice.App.Config;
import mai_onsyn.VeloVoice.App.ResourceManager;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.Resources;
import nl.siegmann.epublib.domain.TOCReference;
import nl.siegmann.epublib.epub.EpubReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

import static mai_onsyn.VeloVoice.App.Constants.*;

public class Epub extends Source {
    private static final Logger log = LogManager.getLogger(Epub.class);

    @Override
    protected String getHomePage() {
        return "";
    }

    @Override
    protected String getDetails() {
        return "Local";
    }

    @Override
    public String getNameSpace() {
        return "source.epub.name";
    }

    @Override
    protected Image getIcon() {
        return ResourceManager.pc;
    }


    public Epub() {
        super.config.registerString("ParseMethod", "OPF-NCX");
        super.config.registerBoolean("MarkTitle", true);
    }

    @Override
    public Config.ConfigBox mkConfigFrame() {
        Config.ConfigBox configBox = new Config.ConfigBox(UI_SPACING, UI_HEIGHT);

        Config.ConfigItem parseMethod = config.genChooseStringItem("ParseMethod", List.of("OPF-NCX", "OPF-NAV(Linear)"));
        Config.ConfigItem markTitle = config.genSwitchItem("MarkTitle");
        parseMethod.setI18NKey("source.epub.label.parse_method");
        markTitle.setI18NKey("source.epub.label.mark_title");
        I18N.registerComponents(parseMethod, markTitle);

        configBox.addConfigItem(parseMethod, markTitle);

        return configBox;
    }

    @Override
    public void process(String uri, AXTreeItem root) throws Exception {

        File inputFile = new File(uri);

        if (inputFile.isFile() && inputFile.getName().endsWith(".epub")) {
            processFile(uri, root);
        } else if (inputFile.isDirectory()) {
            File[] listFiles = inputFile.listFiles();

            if (listFiles != null) {
                Arrays.sort(listFiles, Comparator.comparing(File::getName));

                for (File file : listFiles) {
                    if (file.isFile() && file.getName().endsWith(".epub")) processFile(file.getAbsolutePath(), root);
                    else log.info(I18N.getCurrentValue("log.epub.info.not_a_epub_file"), file.getName());
                }
            } else throw new IOException("Failed to list files in directory: " + inputFile.getAbsolutePath());
        } else log.info(I18N.getCurrentValue("log.epub.info.not_a_epub_file"), inputFile.getName());
    }

    private void processFile(String uri, AXTreeItem root) throws IOException {
        Book book = new EpubReader().readEpub(new FileInputStream(uri));
        String title = parseTitle(book, uri);

        AXTreeView<?> attribution = root.getAttribution();

        AXTreeItem parent = attribution.createFolderItem(title);
        List<TOCReference> tocReferences = book.getTableOfContents().getTocReferences();

        Platform.runLater(() -> root.add(parent));

        switch (config.getString("ParseMethod")) {
            case "OPF-NAV(Linear)" -> {
                Resource navResource = getNav(book);
                if (navResource != null) {
                    parseLinearNav(navResource, book.getResources(), parent, attribution);

                } else for (TOCReference tocReference : tocReferences) {
                    addToTree(parent, tocReference, attribution);
                }
            }
            case "OPF-NCX" -> {
                for (TOCReference tocReference : tocReferences) {
                    addToTree(parent, tocReference, attribution);
                }
            }
        }
    }

    private void parseLinearNav(Resource navResource, Resources resources, AXTreeItem parent, AXTreeView<?> attribution) throws IOException, NullPointerException {
        Elements lis = Jsoup.parse(new String(navResource.getData(), navResource.getInputEncoding())).select("body > nav > ol > li");

        AXTreeItem currentFolder = attribution.createFolderItem("Default");
        for (Element li : lis) {
            if (!li.select("span").isEmpty()) {
                //log.debug("Title: {}", li.text().trim());
                currentFolder = attribution.createFolderItem(li.text().trim());

                AXTreeItem finalCurrentFolder = currentFolder;
                Platform.runLater(() -> parent.add(finalCurrentFolder));
            }
            else {
                String id = Objects.requireNonNull(li.selectFirst("a")).attr("href");
                //log.debug("ID: {}", id);
                String content = formatLines(parseContent(resources.getById(id)));

                AXDataTreeItem<?> fileItem = attribution.createFileItem(li.text().trim(), new SimpleStringProperty(content));
                AXTreeItem finalCurrentFolder = currentFolder;
                Platform.runLater(() -> finalCurrentFolder.add(fileItem));
            }
        }
    }

    private Resource getNav(Book book) {
        for (Resource resource : book.getResources().getAll()) {
            if (resource.getHref().toLowerCase().endsWith("nav.xhtml")) {
                return resource;
            }
        }
        return null;
    }

    private void addToTree(AXTreeItem parent, TOCReference tocReference, AXTreeView<?> attribution) throws IOException {
        String title = tocReference.getTitle();
        //log.info("Title: {}", title);

        Resource resource = tocReference.getResource();
        if (!resource.getHref().startsWith("static_folder_resources/")) {
            List<String> lines = parseContent(resource);
            SimpleStringProperty data = (SimpleStringProperty) attribution.getDataCreator().create();

            data.set(formatLines(lines));

            Platform.runLater(() -> parent.add(attribution.createFileItem(title, data)));
        }

        List<TOCReference> children = tocReference.getChildren();
        if (children != null && !children.isEmpty()) {
            AXTreeItem folderItem = attribution.createFolderItem(title);
            Platform.runLater(() -> parent.add(folderItem));

            for (TOCReference child : children) {
                addToTree(folderItem, child, attribution);
            }
        }
    }

    private List<String> parseContent(Resource resource) throws IOException {
        String htmlContent = new String(resource.getData(), resource.getInputEncoding());
        List<String> lines = new ArrayList<>();
        Document document = Jsoup.parse(htmlContent);

        Elements elements;
        if (config.getBoolean("MarkTitle")) elements = document.select("p, h1, h2, h3, h4, h5, h6");
        else elements = document.select("p");
        for (Element element : elements) {
            String trimmed = element.text().trim();
            if (!trimmed.isEmpty()) lines.add(trimmed);
        }

        return lines;
    }

    private String formatLines(List<String> lines) {
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            sb.append("  ").append(line).append("\n\n");
        }
        return sb.toString();
    }

    private String parseTitle(Book book, String uri) {
        String title;

        title = book.getTitle();
        if (title == null || title.isEmpty()) {
            title = book.getMetadata().getFirstTitle();
        }

        if (title == null || title.isEmpty()) {
            File file = new File(uri);
            String fileName= file.getName();
            title = fileName.substring(0, fileName.lastIndexOf('.'));
        }

        if (title.isEmpty()) {
            title = "Unknown Title";
        }

        return title;
    }
}
