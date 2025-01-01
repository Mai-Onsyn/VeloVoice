package mai_onsyn.VeloVoice2.NetWork;

import mai_onsyn.VeloVoice2.Audio.AfterEffects;
import mai_onsyn.VeloVoice2.Audio.AudioSaver;
import mai_onsyn.VeloVoice2.NetWork.TTS.FixedEdgeTTSClient;
import mai_onsyn.VeloVoice2.NetWork.TTS.TTSClient;
import mai_onsyn.VeloVoice2.Text.Sentence;

import java.io.File;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static mai_onsyn.VeloVoice2.App.Runtime.config;

//多线程TTS处理
public class TTSPool {
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

    public void connect() {
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
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        for (TTSClient client : clients) {
            client.close();
        }
    }

    public void execute(List<String> input, File outputFolder, String fileName) throws InterruptedException {
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
                        text = input.get(thisIndex);
                    }

                    try {
                        Sentence sentence = client.process(text);
                        resultPool.put(thisIndex, sentence);
                        countDownLatch.countDown();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }

        countDownLatch.await();

        List<Sentence> result = new ArrayList<>(resultPool.size());
        for (Map.Entry<Integer, Sentence> entry : resultPool.entrySet()) {
            result.add(entry.getKey(), entry.getValue());
        }
        result.forEach(System.out::println);

        AudioSaver.save(AfterEffects.process(result), outputFolder, fileName);
    }
}

class ClientFactory {

    private final TTSPool.ClientType type;

    ClientFactory(TTSPool.ClientType type) {
        this.type = type;
    }

    public TTSClient createClient() {
        return switch (type) {
            case EDGE -> new FixedEdgeTTSClient();
        };
    }


}