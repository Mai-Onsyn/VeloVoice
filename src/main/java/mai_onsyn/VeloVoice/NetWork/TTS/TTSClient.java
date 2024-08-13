package mai_onsyn.VeloVoice.NetWork.TTS;

import org.java_websocket.client.WebSocketClient;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class TTSClient extends WebSocketClient {
    public enum Status {
        FREE,
        RECEIVING_DATA,
        DATA_AVAILABLE,
        CLOSING,
        ERROR
    }
    public TTSClient(URI serverUri, Map<String, String> httpHeaders) {
        super(serverUri, httpHeaders);
    }

    public abstract List<byte[]> sendText(UUID uuid, String text) throws InterruptedException;

    public abstract List<byte[]> readData();

    public abstract Status getStatus();

    //@Override
    public void connect() {
        super.connect();
    }
}
