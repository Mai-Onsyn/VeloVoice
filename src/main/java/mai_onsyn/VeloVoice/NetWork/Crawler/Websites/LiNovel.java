package mai_onsyn.VeloVoice.NetWork.Crawler.Websites;

import mai_onsyn.VeloVoice.App.AppConfig;
import mai_onsyn.VeloVoice.App.Runtime;
import mai_onsyn.VeloVoice.Main;
import mai_onsyn.VeloVoice.NetWork.Crawler.HttpFactory;
import mai_onsyn.VeloVoice.NetWork.Crawler.NovelSite;
import mai_onsyn.VeloVoice.Utils.Structure;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import static mai_onsyn.VeloVoice.App.Runtime.logger;

public class LiNovel implements NovelSite {

    private final String url;

    private static final String HOME = "https://www.linovel.net";

    public LiNovel(String url) throws MalformedURLException {
        if (url.contains("book")) {
            if (url.endsWith("#catalog")) this.url = url;
            else this.url = url + "#catalog";
        }
        else throw new MalformedURLException("Unknown URL format: " + url);
    }

    private Structure<String> parseVolumeURLs(Elements sections) {
        Structure<String> urlStructure = new Structure<>("Body");

        for (int i = 0; i < sections.size(); i++) {
            String volumeName = sections.get(i).select("div.volume-info > h2 > a").text();
            if (AppConfig.isAppendExtraVolumeName) volumeName = String.format("第%d卷 %s", i + 1, volumeName);
            Structure<String> volumeStructure = new Structure<>(volumeName);

            Elements chapters = sections.get(i).select("div.chapter-list-wrp.text-expander > div.chapter-list.text-content-wrapper > div > div");


            int counter = 0;
            for (Element chapter : chapters) {
                Elements box = chapter.select("div.chapter");

                for (Element item : box) {
                    StringBuilder chapterName = new StringBuilder();
                    if (AppConfig.isAppendVolumeName) chapterName.append(volumeName).append(" ");
                    if (AppConfig.isAppendExtraChapterName) chapterName.append("第").append(++counter).append("章").append(" ");
                    chapterName.append(item.select("a").text());
                    String chapterURL = item.select("a").attr("href");
                    volumeStructure.getChildren().add(new Structure<>(chapterName.toString(), HOME + chapterURL));
                }
            }

            urlStructure.getChildren().add(volumeStructure);
        }

        return urlStructure;
    }

    @Override
    public Structure<List<String>> getContents() {
        Document catalog = HttpFactory.getDocument(url);
        Element detailHashTable = catalog.selectFirst("body > div.book-wrapper > div.detail-layout > div.detail.hash-tab");
        String novelTitle = detailHashTable.selectFirst("div.meta-info > h1").text();

        Structure<String> volumeURLs = parseVolumeURLs(detailHashTable.select("div.tab-page.book-index-tab[data-tab-id=catalog] > div.section-list > div.section"));
        Structure<List<String>> contentTree = new Structure<>(novelTitle);


        for (Structure<String> volumeURL : volumeURLs.getChildren()) {
            String volumeName = volumeURL.getName();

            Structure<List<String>> volumeStructure = new Structure<>(volumeName);
            for (Structure<String> chapterURL : volumeURL.getChildren()) {
                String chapterName = chapterURL.getName();
                List<String> chapterContents = parseChapterContents(chapterURL.getData());

                chapterContents.addFirst(chapterName);

                volumeStructure.getChildren().add(new Structure<>(chapterName, chapterContents));

                if (Runtime.consoleMode) System.out.println("已加载 - " + chapterName);
                else logger.prompt("已加载 - " + chapterName);
            }
            contentTree.getChildren().add(volumeStructure);
        }

        return contentTree;
    }

    private List<String> parseChapterContents(String url) {
        List<String> contents = new ArrayList<>();
        Elements lines = HttpFactory.getDocument(url).child(0).select("div.read-content > div > div.article-text > p");

        lines.forEach(e -> {
            String text = e.text();
            if (!text.isEmpty()) {
                contents.add(text);
            }
        });
        return contents;
    }

    public static void main(String[] args) {
        new Main();
        try {
            LiNovel ln = new LiNovel("https://www.linovel.net/book/117696.html");
            Structure<List<String>> structure = ln.getContents();
            System.out.println(structure);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}