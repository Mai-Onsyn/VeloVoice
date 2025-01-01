package mai_onsyn.VeloVoice2.NetWork.TTS;

import com.alibaba.fastjson.JSONObject;
import mai_onsyn.VeloVoice2.App.Constants;
import mai_onsyn.VeloVoice2.Text.Sentence;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static mai_onsyn.VeloVoice2.App.Constants.*;
import static mai_onsyn.VeloVoice2.App.Runtime.config;

public class FixedEdgeTTSClient implements TTSClient {

    public static void setVoiceVolume(double v) {
        EdgeTTSClient.setVoiceVolume(v);
    }
    public static void setVoiceRate(double v) {
        EdgeTTSClient.setVoiceRate(v);
    }
    public static void setVoicePitch(double v) {
        EdgeTTSClient.setVoicePitch(v);
    }
    public static void setVoice(String name) {
        EdgeTTSClient.setVoice(name);
    }



    private EdgeTTSClient client;

    public FixedEdgeTTSClient() {
        client = new EdgeTTSClient();
    }

    @Override
    public void connect() throws Exception {
        int retry = -1;
        while (retry++ < config.getInteger("MaxRetries")) {
            try {
                client.connectWithRetries();
                return;
            } catch (Exception _) {}
        }
        throw new EdgeTTSException("Out of retries");
    }

    @Override
    public void close() {
        client.close();
    }

    @Override
    public Sentence process(String s) throws Exception {
        int retry = -1;
        while (retry++ < config.getInteger("MaxRetries")) {
            try {
                return client.process(s);
            } catch (Exception _) {}
        }
        throw new EdgeTTSException("Out of retries");
    }
}

class EdgeTTSClient extends WebSocketClient {

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

    public static void setVoiceVolume(double v) {
        voiceVolume = v;
    }
    public static void setVoiceRate(double v) {
        voiceRate = v;
    }
    public static void setVoicePitch(double v) {
        voicePitch = v;
    }
    public static void setVoice(String name) {
        voice = EdgeTTSVoice.getVoice(name);
    }

    public EdgeTTSClient() {
        super(URI.create(Constants.getEdgeTTSURL()), HEADERS);
    }

    private CompletableFuture<String> locker;
    private final List<byte[]> bytesTemp = new ArrayList<>();

    public void connectWithRetries() throws Exception {
        super.connect();
        locker = new CompletableFuture<>();
        String res = locker.get(config.getInteger("TimeoutMillis"), TimeUnit.MILLISECONDS);
        if (res.equals("error")) {
            throw new EdgeTTSException("Server connect error");
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
                "audio-24khz-48kbitrate-mono-mp3"
        );
        super.send(message);
        locker.complete("opened");
    }

    @Override
    public void onMessage(String s) {
        if (s.contains("Path:turn.end")) locker.complete("finished");
        //System.out.println(s);
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
            this.bytesTemp.add(audioData);
        }
    }

    @Override
    public void onClose(int i, String s, boolean b) {

    }

    @Override
    public void onError(Exception e) {
        locker.complete("error");
    }

    public Sentence process(String s) throws Exception {
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
                UUID.randomUUID().toString().replace("-", ""),
                dateStr,
                voice.get("Locale"),
                voice.get("Name"),
                voicePitch >= 1.0 ? "+" : "",
                (voicePitch - 1) * 100,
                voiceRate >= 1.0 ? "+" : "",
                (voiceRate - 1) * 100,
                voiceVolume >= 1.0 ? "+" : "",
                (voiceVolume - 1) * 100,
                s.replaceAll("&", "&amp;").replaceAll("<", "&lt;")
        );
        this.send(message);

        locker = new CompletableFuture<>();
        String res = locker.get(config.getInteger("TimeoutMillis"), TimeUnit.MILLISECONDS);
        if (res.equals("error")) throw new EdgeTTSException("Server not response");
        Sentence sentence = new Sentence(s, List.copyOf(bytesTemp));
        bytesTemp.clear();

        return sentence;
    }
}

class EdgeTTSException extends Exception {
    public EdgeTTSException(String message) {
        super(message);
    }
}