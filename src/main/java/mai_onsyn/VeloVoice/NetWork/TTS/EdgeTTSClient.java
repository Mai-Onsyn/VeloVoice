package mai_onsyn.VeloVoice.NetWork.TTS;

import com.alibaba.fastjson.JSONObject;
import mai_onsyn.VeloVoice.App.AppConfig;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static mai_onsyn.VeloVoice.App.Constants.*;
import static mai_onsyn.VeloVoice.App.Runtime.logger;

public class EdgeTTSClient extends WebSocketClient implements TTSClient {


    private static final String mp3Format = "audio-24khz-48kbitrate-mono-mp3";
    private static final String webmFormat = "webm-24khz-16bit-mono-opus";


    private static final Map<String, String> HEADERS = new HashMap<>();
    static {
        HEADERS.put("Origin", ORIGIN);
        HEADERS.put("Pragma", "no-cache");
        HEADERS.put("Cache-Control", "no-cache");
        HEADERS.put("User-Agent", USER_AGENT);
    }


    private static double voicePitch = 1.0;
    private static double voiceRate = 1.0;
    private static double voiceVolume = 1.0;
    private static JSONObject voice;

    public static void setVoiceVolume(double voiceVolume) {
        EdgeTTSClient.voiceVolume = voiceVolume;
    }
    public static void setVoiceRate(double voiceRate) {
        EdgeTTSClient.voiceRate = voiceRate;
    }
    public static void setVoicePitch(double voicePitch) {
        EdgeTTSClient.voicePitch = voicePitch;
    }
    public static void setVoice(JSONObject json) {
        voice = json;
    }


    public EdgeTTSClient() {
        super(URI.create(URL), HEADERS);
        dataTemp = new ArrayList<>();
    }

    private List<byte[]> dataTemp;
    private volatile CompletableFuture<List<byte[]>> future;

    @Override
    public synchronized List<byte[]> sendText(UUID uuid, String text) throws InterruptedException {
        String dateStr = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss 'GMT'Z (zzzz)").format(new Date());
        String message = String.format(
                """
                X-RequestId:%s\r
                Content-Type:application/ssml+xml\r
                X-Timestamp:%sZ\r
                Path:ssml\r
                \r
                <speak version='1.0' xmlns='http://www.w3.org/2001/10/synthesis' xml:lang='%s'><voice name='%s'><prosody pitch='%s%.1fHz' rate='%s%.1f%%' volume='%s%.1f%%'>%s</prosody></voice></speak>
                """,
                uuid.toString().replace("-", ""),
                dateStr,
                voice.get("Locale"),
                voice.get("Name"),
                voicePitch >= 1.0 ? "+" : "",
                (voicePitch - 1) * 100,
                voiceRate >= 1.0 ? "+" : "",
                (voiceRate - 1) * 100,
                voiceVolume >= 1.0 ? "+" : "",
                (voiceVolume - 1) * 100,
                text.replaceAll("&", "&amp;").replaceAll("<", "&lt;")
        );
        future = new CompletableFuture<>();
        try {
            super.send(message);
            return future.get(AppConfig.timeoutSeconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            if (!(e instanceof InterruptedException)) {
                if (logger != null) logger.debug("Error message: " + text + " because: " + e);
                else System.out.println("Error message: " + text + " because: " + e);
            }
            else throw new InterruptedException();
            return null;
        }
    }

    CompletableFuture<?> openLock;
    @Override
    public void connect() {
        try {
            openLock = new CompletableFuture<>();
            super.connect();
            openLock.get(AppConfig.timeoutSeconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            super.close();
            //throw new RuntimeException(e);
        }
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        String dateStr = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss 'GMT'Z (zzzz)").format(new Date());
        String message = String.format(
                """
                X-Timestamp:%s\r
                Content-Type:application/json; charset=utf-8\r
                Path:speech.config\r
                \r
                {"context":{"synthesis":{"audio":{"metadataoptions":{"sentenceBoundaryEnabled":"false","wordBoundaryEnabled":"true"},"outputFormat":"%s"}}}}
                """,
                dateStr,
                mp3Format
        );
        super.send(message);
        openLock.complete(null);
    }

    @Override
    public void onMessage(String s) {
        //System.out.println(s);
        if (s.contains("Path:turn.start")) {
            dataTemp = new ArrayList<>();
        }
        if (s.contains("Path:turn.end")) {
            future.complete(List.copyOf(dataTemp));
        }
    }

    private static final byte[] HEADER = {0x50, 0x61, 0x74, 0x68, 0x3a, 0x61, 0x75, 0x64, 0x69, 0x6f, 0x0d, 0x0a};
    @Override
    public void onMessage(ByteBuffer byteBuffer) {
        byte[] originalData = byteBuffer.array();
        int headerLength = HEADER.length;
        int dataLength = originalData.length;

        int headerIndex = -1;
        for (int i = 0; i <= dataLength - headerLength; i++) {
            boolean isMatch = true;
            for (int j = 0; j < headerLength; j++) {
                if (originalData[i + j] != HEADER[j]) {
                    isMatch = false;
                    break;
                }
            }
            if (isMatch) {
                headerIndex = i;
                break;
            }
        }

        if (headerIndex != -1) {
            byte[] audioData = Arrays.copyOfRange(originalData, headerIndex + headerLength, dataLength);
            this.dataTemp.add(audioData);
        }
    }

    @Override
    public void onClose(int i, String s, boolean b) {
    }

    @Override
    public void onError(Exception e) {
        e.printStackTrace();
        //throw new RuntimeException(e);
    }

//    private void attemptReconnect() {
//        if (reconnectAttempts < maxReconnectAttempts) {
//            reconnectAttempts++;
//            try {
//                Thread.sleep(2000);  // 等待2秒后重连
//                System.out.println("Reconnecting attempt " + reconnectAttempts);
//                this.reconnect();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        } else {
//            System.out.println("Max reconnect attempts reached. Giving up.");
//        }
//    }
}
