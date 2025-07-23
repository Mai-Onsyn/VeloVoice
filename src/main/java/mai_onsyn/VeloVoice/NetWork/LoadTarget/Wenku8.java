package mai_onsyn.VeloVoice.NetWork.LoadTarget;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.image.Image;
import javafx.util.Pair;
import mai_onsyn.AnimeFX.I18N;
import mai_onsyn.AnimeFX.Module.AXTreeView;
import mai_onsyn.AnimeFX.Utls.AXDataTreeItem;
import mai_onsyn.AnimeFX.Utls.AXTreeItem;
import mai_onsyn.VeloVoice.App.Config;
import mai_onsyn.VeloVoice.App.Resource;
import mai_onsyn.VeloVoice.App.Runtime;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static mai_onsyn.VeloVoice.App.Constants.*;

public class Wenku8 extends Source {
    private static final Logger log = LogManager.getLogger(Wenku8.class);
    //private static final LocalTXT parser = new LocalTXT();
    //private static final List<Pair<String, String>> identifiers = List.of(new Pair<>("\n  [^ ]", "\n"));

    public Wenku8() {
        super();
        super.config.registerBoolean("MarkChapter", true);
        super.config.registerBoolean("MarkVolume", true);
    }

    @Override
    protected String getHomePage() {
        return "https://www.wenku8.net/index.php";
    }

    @Override
    protected String getDetails() {
        return "wenku8.net";
    }

    @Override
    public String getNameSpace() {
        return "source.wenku8.name";
    }

    @Override
    protected Image getIcon() {
        return Resource.icon_wenku8;
    }

    @Override
    public Config.ConfigBox mkConfigFrame() {
        Config.ConfigBox configBox = new Config.ConfigBox(UI_SPACING, UI_HEIGHT);

        Config.ConfigItem markChapter = super.config.genSwitchItem("MarkChapter");
        Config.ConfigItem markVolume = super.config.genSwitchItem("MarkVolume");
        markChapter.setI18NKey("source.wenku8.label.mark_chapter");
        markVolume.setI18NKey("source.wenku8.label.mark_volume");
        I18N.registerComponents(markChapter, markVolume);

        Runtime.configManager.addOnChangedListener("Wenku8.MarkChapter", () -> {
            markVolume.setDisable(!super.config.getBoolean("MarkChapter"));
        });

        configBox.addConfigItem(markChapter, markVolume);

        return configBox;
    }

    @Override
    public void process(String uri, AXTreeItem root) throws InterruptedException {
        log.info("Start loading: {}, it may takes a long time to load!", uri);
        AXTreeView<?> attribution = root.getAttribution();

        Document mainPageDocument = LoadUtil.getDocument(uri);
        if (mainPageDocument == null) {
            log.error("Failed to load page: {}", uri);
            return;
        }
        Map<String, List<Pair<String, String>>> chapterURLs = getChapterURLs(mainPageDocument);
        String novelName = getNovelName(mainPageDocument);
        AXTreeItem novel = attribution.createFolderItem(novelName);
        Platform.runLater(() -> root.add(novel));

        if (Thread.currentThread().isInterrupted()) throw new InterruptedException();

        for (Map.Entry<String, List<Pair<String, String>>> entry : chapterURLs.entrySet()) {
            String volumeName = entry.getKey();
            AXTreeItem volume = attribution.createFolderItem(volumeName);
            Platform.runLater(() -> novel.add(volume));
            for (Pair<String, String> chapter_url : entry.getValue()) {
                if (Thread.currentThread().isInterrupted()) throw new InterruptedException();

                List<String> contentLines = getContent(chapter_url.getValue());

                StringBuilder sb = new StringBuilder();

                String chapterName = super.config.getBoolean("MarkVolume") ? volumeName + " " + chapter_url.getKey() : chapter_url.getKey();
                if (super.config.getBoolean("MarkChapter")) sb.append(chapterName).append("\n\n");
                for (String line : contentLines) {
                    sb.append("  ").append(line).append("\n\n");
                }

                AXDataTreeItem<?> chapter = attribution.createFileItem(chapterName, new SimpleStringProperty(sb.toString()));
                Platform.runLater(() -> volume.add(chapter));
            }
        }

        log.info("Finished loading from: {}", uri);
    }

    private List<String> getContent(String url) {
        Document page = LoadUtil.getDocument(url);
        if (page == null) return List.of(url);
        Element contentTable = page.selectFirst("#content");
        if (contentTable == null) return List.of(url);

        List<String> lines = new ArrayList<>();
        for (Node node : contentTable.childNodes()) {
            if (node instanceof TextNode) {
                String text = ((TextNode) node).text().trim();
                if (!text.isEmpty()) {
                    lines.add(text);
                }
            }
        }
        return lines;
    }

    private Map<String, List<Pair<String, String>>> getChapterURLs(Document mainPageDocument) {
        Map<String, List<Pair<String, String>>> chapterURLs = new LinkedHashMap<>();
        if (mainPageDocument == null) return chapterURLs;

        String contentPageURL = "https://www.wenku8.net" + mainPageDocument.select("#content > div:nth-child(1) > div:nth-child(6) > div > span:nth-child(1) > fieldset > div > a").attr("href");
        Document contentPageDocument = LoadUtil.getDocument(contentPageURL);
        if (contentPageDocument == null) return chapterURLs;

        Element table = contentPageDocument.select("body > table > tbody").first();

        if (table != null) {
            Elements rows = table.select("tr");
            for (Element row : rows) {
                Elements cells = row.select("td");

                if (cells.size() == 1) {
                    String volumeName = cells.first().text();
                    List<Pair<String, String>> chapterList = new ArrayList<>();
                    chapterURLs.put(volumeName, chapterList);

                    for (Element chapterRow : rows.subList(rows.indexOf(row) + 1, rows.size())) {
                        Elements chapterCells = chapterRow.select("td");
                        if (chapterCells.size() == 4) {
                            for (Element chapterCell : chapterCells) {
                                Elements links = chapterCell.select("a");
                                if (links.size() == 1) {
                                    Element link = links.first();
                                    chapterList.add(new Pair<>(link.text(), contentPageURL.replace("index.htm", "") + link.attr("href")));
                                }
                            }
                        } else break;
                    }
                }
            }
        }

        return chapterURLs;
    }

    private String getNovelName(Document mainPageDocument) {
        return mainPageDocument.select("#content > div:nth-child(1) > table:nth-child(1) > tbody > tr:nth-child(1) > td > table > tbody > tr > td:nth-child(1) > span > b").text();
    }
}
