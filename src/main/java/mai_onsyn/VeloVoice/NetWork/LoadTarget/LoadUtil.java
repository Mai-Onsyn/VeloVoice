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
                return Jsoup.parse(URI.create(url).toURL(), config.getInteger("TimeoutSeconds") * 1000);
            } catch (MalformedURLException e) {
                log.error("Invalid URL: {}", url);
                return null;
            } catch (IOException e) {
                retries++;
                if (Thread.currentThread().isInterrupted()) {
                    log.debug("Thread interrupted, aborting retry for: {}", url);
                    return null;
                }

                if (retries < maxRetries) {
                    log.warn("Load Failed, retrying... (Attempt {} of {}): {}", retries, maxRetries, url);
                    try {
                        Thread.sleep(500);
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
