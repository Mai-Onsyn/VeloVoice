package mai_onsyn.VeloVoice.NetWork.Crawler.Websites;

import mai_onsyn.VeloVoice.App.AppConfig;
import mai_onsyn.VeloVoice.NetWork.Crawler.HttpFactory;
import mai_onsyn.VeloVoice.NetWork.Crawler.NovelSite;
import mai_onsyn.VeloVoice.Utils.Structure;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.util.*;

public class WenKu8 implements NovelSite {

    private final String url;

    private static final String HOME = "https://www.wenku8.net";

    public WenKu8(String url) {
        if (url.contains("novel")) {
            this.url = url;
        }
        else if (url.contains("book")) {
            this.url = parseCatalogUrl(url);
        }
        else {
            throw new RuntimeException("Unknown URL format!");
        }
    }

    @Override
    public Structure<List<String>> getContents() {
        Document catalog = HttpFactory.getBody(this.url);
        String novelTitle = catalog.getElementById("title").text();

        Structure<List<String>> contentTree = new Structure<>(novelTitle);

        Elements tableRows = catalog.select("body table tbody").first().select("tr");
        Structure<String> volumeURLList = parseVolumeURLs(tableRows);

        for (Structure<String> volumeURLStructure : volumeURLList.getChildren()) {
            Structure<List<String>> volumeStructure = new Structure<>(volumeURLStructure.getName());

            for (Structure<String> chapterURLStructure : volumeURLStructure.getChildren()) {
                String chapterTitle = chapterURLStructure.getName();
                String chapterURL = chapterURLStructure.getData();
                List<String> chapterContents = parseChapterContent(chapterURL);
                chapterContents.addFirst(AppConfig.isAppendVolumeName ? volumeURLStructure.getName() + " " + chapterTitle : chapterTitle);
                volumeStructure.add(new Structure<>(AppConfig.isAppendVolumeName ? volumeURLStructure.getName() + " " + chapterTitle : chapterTitle, chapterContents));
            }

            contentTree.add(volumeStructure);
        }

        return contentTree;
    }


    private List<String> parseChapterContent(String url) {
        Element contentBody = HttpFactory.getBody(url).getElementById("content");

        List<String> lines = new ArrayList<>();
        for (Node node : contentBody.childNodes()) {
            if (node instanceof TextNode) {
                String text = ((TextNode) node).text().trim();
                if (!text.isEmpty()) {
                    lines.add(text);
                }
            }
        }
        return lines;
    }

    private Structure<String> parseVolumeURLs(Elements tableBody) {
        Structure<String> parsedTableBody = new Structure<>("TableBody");

        for (Element row : tableBody) {
            Elements cells = row.select("td");
            if (cells.size() == 1) {
                String volumeName = cells.first().text();
                Structure<String> volumeStructure = new Structure<>(volumeName);

                for (Element chapterRow : tableBody.subList(tableBody.indexOf(row) + 1, tableBody.size())) {
                    Elements chapterCells = chapterRow.select("td");
                    if (chapterCells.size() == 4) {
                        for (Element chapterCell : chapterCells) {
                            Elements links = chapterCell.select("a");
                            if (links.size() == 1) {
                                Element link = links.first();
                                volumeStructure.add(new Structure<>(link.text(), this.url.replace("index.htm", "") + link.attr("href")));
                            }
                        }
                    } else break;
                }

                parsedTableBody.add(volumeStructure);
            }
        }

        return parsedTableBody;
    }

    private String parseCatalogUrl(String url) {
        Elements href = HttpFactory.getBody(url).getElementById("content").select("a[href]");

        for (Element element : href) {
            if (element.hasText()) {
                if (element.toString().contains("小说目录")) {
                    return HOME + element.attr("href");
                }
            }
        }
        return "";
    }
}
