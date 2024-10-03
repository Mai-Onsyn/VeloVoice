package mai_onsyn.VeloVoice.NetWork.Crawler.Websites;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import mai_onsyn.VeloVoice.App.AppConfig;
import mai_onsyn.VeloVoice.Main;
import mai_onsyn.VeloVoice.NetWork.Crawler.HttpFactory;
import mai_onsyn.VeloVoice.NetWork.Crawler.NovelSite;
import mai_onsyn.VeloVoice.Utils.Structure;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.math.BigInteger;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Kakuyomu implements NovelSite {

    private final String url;
    private static final String HOME = "https://kakuyomu.jp/";

    public Kakuyomu(String url) throws MalformedURLException {
        if (url.contains("works") && !url.contains("episodes") && !url.endsWith("/")) {
            this.url = url;
        }
        else {
            throw new MalformedURLException("Unknown URL format: " + url);
        }
    }

    private List<String> parseChapterURLs() {
        String scriptHead = "<script id=\"__NEXT_DATA__\" type=\"application/json\">";

        String jsonString = HttpFactory.getDocument(url)
                .selectFirst("body")
                .getElementById("__NEXT_DATA__")
                .toString();
        jsonString = jsonString.substring(jsonString.indexOf(scriptHead) + scriptHead.length(), jsonString.indexOf("</script>"));

        //所有杂七杂八的元素 包含章和话
        JSONObject contentJson = JSONObject.parseObject(jsonString)
                .getJSONObject("props")
                .getJSONObject("pageProps")
                .getJSONObject("__APOLLO_STATE__");

        List<String> episodes = new ArrayList<>();
        for (String key : contentJson.keySet()) {
            if (key.startsWith("TableOfContentsChapter")) {;
                JSONArray episodeArray = contentJson.getJSONObject(key).getJSONArray("episodeUnions");
                for (Object o : episodeArray) {
                    String episode = ((JSONObject) o).getString("__ref").substring(8);
                    episodes.add(episode);
                }
            }
        }
        episodes.sort(Comparator.comparing(BigInteger::new));

        List<String> result = new ArrayList<>();
        episodes.forEach(e -> result.add(url + "/episodes/" + e));

        //result.forEach(System.out::println);
        return result;
    }

    private List<String> parseContents(Element body) {

        List<String> result = new ArrayList<>();

        Elements episodeBody = body.selectFirst("#contentMain-inner > div > div > div").getAllElements().select("p");

        episodeBody.forEach(e -> {
            String line = e.text().trim();
            if (!line.isEmpty()) result.add(line);
        });

        return result;
    }

    @Override
    public Structure<List<String>> getContents() {
        Structure<List<String>> root = new Structure<>("");


        Structure<List<String>> chapter = new Structure<>("initial");
        List<String> chapterURLs = parseChapterURLs();
        String chapterName = "";
        int i = 0;
        for (String chapterURL : chapterURLs) {
            Document episodeDoc = HttpFactory.getDocument(chapterURL);

            Element mainTitle = episodeDoc.selectFirst("#contentMain-header-workTitle");
            if (mainTitle != null) root.setName(mainTitle.text());

            Element chapterTitle = episodeDoc.selectFirst("#contentMain-header > p.chapterTitle.level1.js-vertical-composition-item > span");
            if (chapterTitle != null) {
                chapterName = (AppConfig.isAppendExtraChapterName ? "第" + (++i) + "章 " : "") + chapterTitle.text();
                chapter = new Structure<>(chapterName);
                root.add(chapter);
                System.out.println(chapterName);
            }

            Element episodeTitle = episodeDoc.selectFirst("#contentMain-header > p.widget-episodeTitle.js-vertical-composition-item");
            String episodeName = (AppConfig.isAppendVolumeName ? chapterName + " " : "") + episodeTitle.text();
            List<String> lines = parseContents(episodeDoc);
            lines.addFirst(episodeName);
            chapter.add(new Structure<>(episodeName, lines));
            System.out.println(episodeTitle.text());
        }

        return root;
    }

    public static void main(String[] args) throws MalformedURLException {
        new Main();
        Kakuyomu kakuyomu = new Kakuyomu("https://kakuyomu.jp/works/1177354054894027232");

        kakuyomu.getContents();
    }
}
