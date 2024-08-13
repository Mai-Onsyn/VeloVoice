package mai_onsyn.VeloVoice.NetWork.Crawler;

import mai_onsyn.VeloVoice.App.AppConfig;
import mai_onsyn.VeloVoice.NetWork.Crawler.Websites.WenKu8;
import mai_onsyn.VeloVoice.Utils.Structure;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.*;
import java.util.List;

public class HttpFactory {

//    public static void main(String[] args) throws Exception {
//
//        Structure<List<String>> structure = new WenKu8("https://www.wenku8.net/book/1538.htm").getContents();
//
//        System.out.println(structure.toString());
//
//        structure.getChildren().getFirst().getChildren().getFirst().getData().forEach(System.out::println);
//
//    }

    public static Document getBody(String url) {
        try {
            return Jsoup.parse(URI.create(url).toURL(), AppConfig.timeoutSeconds * 1000);
        } catch (IOException e) {
            throw new RuntimeException("Connect Failed: " + url, e);
        }
    }

}
