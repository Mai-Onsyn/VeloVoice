package mai_onsyn.VeloVoice.NetWork.TTS;

import mai_onsyn.VeloVoice.App.AppConfig;
import mai_onsyn.VeloVoice.App.Constants;

import java.util.List;
import java.util.UUID;

import static mai_onsyn.VeloVoice.App.Runtime.logger;

public class FixedEdgeTTSClient implements TTSClient {

    private EdgeTTSClient client;

    public FixedEdgeTTSClient() {
    }

    @Override
    public void connect() {
        int tryCount = -1;
        while (tryCount < AppConfig.retryCount) {
            tryCount++;
            client = new EdgeTTSClient();

            client.connect();

            if (!client.isOpen()) {
                client.close();
                Constants.UpdateSec_MS_GEC();
                if (logger != null) logger.warn(String.format("线程%s连接错误，尝试第%d次重连", Thread.currentThread().getName(), tryCount + 1));
                else System.out.println(String.format("线程%s连接错误，尝试第%d次重连", Thread.currentThread().getName(), tryCount + 1));
                //client.connect();
                continue;
            }

            break;
        }
    }

    @Override
    public boolean isOpen() {
        return client.isOpen();
    }

    @Override
    public void close() {
        client.close();
    }

    @Override
    public List<byte[]> sendText(UUID uuid, String text) throws InterruptedException {
        List<byte[]> bytes = client.sendText(uuid, text);

        if (!checkBytes(bytes)) {
            reconnect();
            bytes = client.sendText(uuid, text);
        }

        return bytes;
    }

    private void reconnect() {
        close();
        connect();
        System.out.println("Reconnected");
    }

    private boolean checkBytes(List<byte[]> bytes) {
        if (bytes == null || bytes.isEmpty()) return false;

        boolean hasNull = false;
        for (byte[] b : bytes) {
            if (b == null) {
                hasNull = true;
                break;
            }
        }

        return !hasNull;
    }

}
