package mai_onsyn.VeloVoice.NetWork.Crawler;

import mai_onsyn.VeloVoice.App.AppConfig;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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

    @Deprecated
    public static Document getDocument2(String url) {
        try {
            return Jsoup.parse(URI.create(url).toURL(), AppConfig.timeoutSeconds * 1000);
        } catch (IOException e) {
            throw new RuntimeException("Connect Failed: " + url, e);
        }
    }

    public static Document getDocument(String url) {
        int retries = 0;
        while (retries < AppConfig.retryCount) {
            try {
                return Jsoup.parse(URI.create(url).toURL(), AppConfig.timeoutSeconds * 1000);
            } catch (IOException e) {
                retries++;
                System.out.println("retry: " + retries);
                if (retries >= AppConfig.retryCount) {
                    throw new RuntimeException("Connect Failed after " + AppConfig.retryCount + " retries: " + url, e);
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Thread interrupted while waiting for retry", ie);
                }
            }
        }
        throw new RuntimeException("Unreachable code");
    }

    public static List<Document> getDocuments(List<String> urls) {
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

        List<Future<Document>> futures = new ArrayList<>();

        for (String url : urls) {
            futures.add(executor.submit(() -> getDocument(url)));
        }

        List<Document> documents = new ArrayList<>();

        for (Future<Document> future : futures) {
            try {
                documents.add(future.get());
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException("Failed to get document", e);
            }
        }

        return documents;
    }


}
