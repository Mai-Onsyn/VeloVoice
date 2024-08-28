package mai_onsyn.VeloVoice.NetWork.Crawler.Websites;

import mai_onsyn.VeloVoice.App.AppConfig;
import mai_onsyn.VeloVoice.Main;
import mai_onsyn.VeloVoice.NetWork.Crawler.HttpFactory;
import mai_onsyn.VeloVoice.NetWork.Crawler.NovelSite;
import mai_onsyn.VeloVoice.Utils.Structure;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.net.MalformedURLException;
import java.util.*;

public class WenKu8 implements NovelSite {

    private final String url;

    private static final String HOME = "https://www.wenku8.net";

    public WenKu8(String url) throws MalformedURLException {
        if (url.contains("novel")) {
            this.url = url;
        }
        else if (url.contains("book")) {
            this.url = parseCatalogUrl(url);
        }
        else {
            throw new MalformedURLException("Unknown URL format: " + url);
        }
    }

    @Override
    public Structure<List<String>> getContents() {
        Document catalog = HttpFactory.getDocument(this.url);
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
                String title = AppConfig.isAppendVolumeName ? volumeURLStructure.getName() + " " + chapterTitle : chapterTitle;
                chapterContents.addFirst(title);
                volumeStructure.add(new Structure<>(title, chapterContents));
                //System.out.println(title);
            }

            contentTree.add(volumeStructure);
        }

        return contentTree;
    }


    private List<String> parseChapterContent(String url) {
        Element contentBody = HttpFactory.getDocument(url).getElementById("content");

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

    // 解析表格体中的卷URL
    private Structure<String> parseVolumeURLs(Elements tableBody) {
        // 创建一个名为TableBody的结构体
        Structure<String> parsedTableBody = new Structure<>("TableBody");

        // 遍历表格体中的每一行
        for (Element row : tableBody) {
            // 获取当前行的单元格
            Elements cells = row.select("td");
            // 如果当前行只有一个单元格
            if (cells.size() == 1) {
                // 获取卷名
                String volumeName = cells.first().text();
                // 创建一个名为卷名的结构体
                Structure<String> volumeStructure = new Structure<>(volumeName);

                // 遍历表格体中当前行之后的每一行
                for (Element chapterRow : tableBody.subList(tableBody.indexOf(row) + 1, tableBody.size())) {
                    // 获取当前行的单元格
                    Elements chapterCells = chapterRow.select("td");
                    // 如果当前行有四个单元格
                    if (chapterCells.size() == 4) {
                        // 遍历当前行的每一个单元格
                        for (Element chapterCell : chapterCells) {
                            // 获取当前单元格中的链接
                            Elements links = chapterCell.select("a");
                            // 如果当前单元格中有一个链接
                            if (links.size() == 1) {
                                // 获取链接
                                Element link = links.first();
                                // 将链接的文本和URL添加到卷结构体中
                                volumeStructure.add(new Structure<>(link.text(), this.url.replace("index.htm", "") + link.attr("href")));
                            }
                        }
                    } else break;
                }

                // 将卷结构体添加到表格体结构体中
                parsedTableBody.add(volumeStructure);
            }
        }

        // 返回解析后的表格体结构体
        return parsedTableBody;
    }

    private String parseCatalogUrl(String url) {
        Elements href = HttpFactory.getDocument(url).getElementById("content").select("a[href]");

        for (Element element : href) {
            if (element.hasText()) {
                if (element.toString().contains("小说目录")) {
                    return HOME + element.attr("href");
                }
            }
        }
        return "";
    }

    public static void main(String[] args) {
        try {
            new Main();
            WenKu8 wenKu8 = new WenKu8("https://www.wenku8.net/novel/3/3057/index.htm");
            Structure<List<String>> structure = wenKu8.getContents();
            System.out.println(structure);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
