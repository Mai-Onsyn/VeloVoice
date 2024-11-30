package mai_onsyn.VeloVoice.NetWork.TTS;

import java.net.URI;
import java.net.http.HttpRequest;
import java.util.List;
import java.util.UUID;

public class ChatTTSClient implements TTSClient{
    @Override
    public void connect() {

    }

    @Override
    public boolean isOpen() {
        return false;
    }

    @Override
    public void close() {

    }

    @Override
    public List<byte[]> sendText(UUID uuid, String text) throws InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:9966/"))
                .GET()
                .build();
        return null;
    }
}
