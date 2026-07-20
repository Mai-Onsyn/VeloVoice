package mai_onsyn.VeloVoice.NetWork.LoadTarget;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;

import static mai_onsyn.VeloVoice.App.Runtime.config;

public class LoadUtil {

    private static final Logger log = LogManager.getLogger(LoadUtil.class);

    public static Document getDocument(String url) {
        int retries = 0;
        int maxRetries = config.getInteger("MaxRetries");

        while (retries < maxRetries && !Thread.currentThread().isInterrupted()) {
            try {
                return //Jsoup.parse(URI.create(url).toURL(), config.getInteger("TimeoutSeconds") * 1000);
                    Jsoup.connect(url)
                        .userAgent(config.getString("User-Agent"))
                        .timeout(config.getInteger("TimeoutSeconds") * 1000)
                        .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                        .header("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3")
                        .followRedirects(true)
                        .get();
            } catch (MalformedURLException e) {
                log.error("Invalid URL: {}", url);
                return null;
            } catch (IOException e) {
                retries++;
                if (Thread.currentThread().isInterrupted()) {
                    log.debug("Thread interrupted, aborting retry for: {}", url);
                    return null;
                }
                e.printStackTrace();
                if (retries < maxRetries) {
                    log.warn("Load Failed because {}, retrying... (Attempt {} of {}): {}", e.getMessage(), retries, maxRetries, url);
                    try {
                        Thread.sleep(config.getInteger("FetchFailWaitMillis"));
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        log.debug("Thread interrupted while waiting for retry");
                        return null;
                    }
                }
            }
        }

        if (retries >= maxRetries) {
            log.error("Connect Failed after {} retries: {}", maxRetries, url);
        }
        return null;
    }
}
