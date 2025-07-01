package mai_onsyn.VeloVoice2.NetWork;

import mai_onsyn.VeloVoice2.Audio.AfterEffects;
import mai_onsyn.VeloVoice2.Audio.AudioSaver;
import mai_onsyn.VeloVoice2.NetWork.TTS.FixedEdgeTTSClient;
import mai_onsyn.VeloVoice2.NetWork.TTS.TTSClient;
import mai_onsyn.VeloVoice2.Text.Sentence;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static mai_onsyn.VeloVoice2.App.Runtime.*;
import static mai_onsyn.VeloVoice2.FrameFactory.LogFactory.logger;

//多线程TTS处理
public class TTSPool {
    private static final Logger log = LogManager.getLogger(TTSPool.class);

    public enum ClientType {
        EDGE
    }

    private final int size;
    private final ClientFactory clientFactory;
    private final TTSClient[] clients;

    public TTSPool(ClientType type, int size) {
        clientFactory = new ClientFactory(type);
        this.size = size;
        clients = new TTSClient[size];
        for (int i = 0; i < size; i++) {
            clients[i] = clientFactory.createClient();
        }
    }

    public void connect() throws InterruptedException {
        log.info(String.format("Connecting to TTS server by %d threads...", size));
        CountDownLatch countDownLatch = new CountDownLatch(size);
        for (TTSClient client : clients) {
            Thread.ofVirtual().start(() -> {
                try {
                    client.connect();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                countDownLatch.countDown();
            });
        }
        try {
            countDownLatch.await(config.getInteger("TimeoutMillis"), TimeUnit.MILLISECONDS);
            log.info(String.format("%d threads connected to TTS server.", size));
        } catch (Exception e) {
            log.warn("Failed to connect because: %s" + e.getMessage());
            throw new InterruptedException();
        }
    }

    public void close() {
        for (TTSClient client : clients) {
            client.close();
        }
        log.info("All threads have been shut down");
    }

    public void execute(List<String> input, File outputFolder, String fileName) throws Exception {
        Map<Integer, Sentence> resultPool = new HashMap<>();

        AtomicInteger processIndex = new AtomicInteger();
        CountDownLatch countDownLatch = new CountDownLatch(input.size());

        for (int i = 0; i < clients.length; i++) {
            TTSClient client = clients[i];
            Thread.ofVirtual().name("TaskThread-" + i).start(() -> {
                while (processIndex.get() < input.size()) {
                    String text;
                    int thisIndex;
                    synchronized (this) {
                        thisIndex = processIndex.getAndIncrement();
                        if (thisIndex < input.size()) text = input.get(thisIndex);
                        else break;
                    }

                    try {
                        Sentence sentence = client.process(text);
                        resultPool.put(thisIndex, sentence);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    } finally {
                        currentFinished.set(currentFinished.get() + 1);
                        totalFinished.set(totalFinished.get() + 1);
                        countDownLatch.countDown();
                    }
                }
            });
        }

        countDownLatch.await();

        List<Sentence> result = new ArrayList<>(resultPool.size());
        for (Map.Entry<Integer, Sentence> entry : resultPool.entrySet()) {
            result.add(entry.getKey(), entry.getValue());
        }

        //after处理，保存
        AudioSaver.save(AfterEffects.process(result), outputFolder, fileName);
    }

    private record ClientFactory(ClientType type) {

        public TTSClient createClient() {
            return switch (type) {
                case EDGE -> new FixedEdgeTTSClient();
            };
        }
    }
}
