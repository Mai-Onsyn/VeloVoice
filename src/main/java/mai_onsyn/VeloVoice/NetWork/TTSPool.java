package mai_onsyn.VeloVoice.NetWork;

import mai_onsyn.AnimeFX.I18N;
import mai_onsyn.VeloVoice.Audio.AfterEffects;
import mai_onsyn.VeloVoice.Audio.AudioSaver;
import mai_onsyn.VeloVoice.NetWork.TTS.FixedEdgeTTSClient;
import mai_onsyn.VeloVoice.NetWork.TTS.TTSClient;
import mai_onsyn.VeloVoice.Text.Sentence;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static mai_onsyn.VeloVoice.App.Runtime.*;

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
        log.info(I18N.getCurrentValue("log.tts_pool.info.connecting"), size);
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
            countDownLatch.await(config.getInteger("TimeoutSeconds"), TimeUnit.SECONDS);
            log.info(I18N.getCurrentValue("log.tts_pool.info.connected"), size);
        } catch (Exception e) {
            log.error(I18N.getCurrentValue("log.tts_pool.error.connect_failed"), e);
            throw new InterruptedException();
        }
    }

    public void close() {
        for (TTSClient client : clients) {
            client.close();
        }
        log.info(I18N.getCurrentValue("log.tts_pool.info.closed"));
    }

    public void execute(List<String> input, File outputFolder, String fileName) throws InterruptedException, IOException {

        log.debug("Received {} sentences", input.size());

        Map<Integer, Sentence> resultPool = new HashMap<>();

        AtomicInteger processIndex = new AtomicInteger();
        CountDownLatch countDownLatch = new CountDownLatch(input.size());

        AtomicInteger aliveThreads = new AtomicInteger(clients.length);
        AtomicBoolean isInterrupted = new AtomicBoolean(false);
        AtomicReference<IOException> exception = new AtomicReference<>();
        try {
            for (int i = 0; i < clients.length; i++) {
                TTSClient client = clients[i];
                Thread thread = Thread.ofVirtual().name("TaskThread-" + i).start(() -> {
                    while (processIndex.get() < input.size()) {
                        String text;
                        int thisIndex;
                        synchronized (this) {
                            thisIndex = processIndex.getAndIncrement();
                            if (thisIndex < input.size()) text = input.get(thisIndex);
                            else break;
                        }

                        if (isInterrupted.get()) {
                            log.debug("Thread {} received interrupt signal, shutting down...", Thread.currentThread().getName());
                            break;
                        }

                        try {
                            Sentence sentence = client.process(text);
                            resultPool.put(thisIndex, sentence);
                        } catch (Exception e) {
                            log.warn(I18N.getCurrentValue("log.tts_pool.warn.thread_failed"), text, e);

                            log.warn(I18N.getCurrentValue("log.tts_pool.warn.thread_shutdown"), e.getMessage(), e);

                            aliveThreads.getAndDecrement();
                            if (aliveThreads.get() == 0) {
                                log.error(I18N.getCurrentValue("log.tts_pool.error.all_failed"));
                                while (countDownLatch.getCount() > 0) {
                                    countDownLatch.countDown();
                                    exception.set(new IOException("All threads exited with exception"));
                                }
                            }

                            return;
                        } finally {
                            currentFinished.set(currentFinished.get() + 1);
                            totalFinished.set(totalFinished.get() + 1);
                            countDownLatch.countDown();
                        }
                    }
                });
            }
            countDownLatch.await();
            if (exception.get() != null) {
                throw exception.get();
            }
        } catch (InterruptedException | IOException e) {
            isInterrupted.set(true);
            throw e;
        }

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
