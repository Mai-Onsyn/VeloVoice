package mai_onsyn.VeloVoice.NetWork.TTS;

import java.util.List;
import java.util.UUID;

public interface TTSClient {
    void connect();

    boolean isOpen();

    void close();

    List<byte[]> sendText(UUID uuid, String text) throws InterruptedException;
}
