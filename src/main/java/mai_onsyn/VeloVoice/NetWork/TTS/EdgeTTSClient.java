package mai_onsyn.VeloVoice.NetWork.TTS;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import mai_onsyn.VeloVoice.App.Constants;
import mai_onsyn.VeloVoice.Audio.AudioEncodeUtils;
import mai_onsyn.VeloVoice.Text.Sentence;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static mai_onsyn.VeloVoice.App.Constants.*;
import static mai_onsyn.VeloVoice.App.Runtime.config;

public class EdgeTTSClient extends WebSocketClient implements TTSClient {

    private static final Map<String, String> HEADERS = new HashMap<>();
    private static final Logger log = LogManager.getLogger(EdgeTTSClient.class);

    static {
        HEADERS.put("Origin", ORIGIN);
        HEADERS.put("Pragma", "no-cache");
        HEADERS.put("Cache-Control", "no-cache");
        HEADERS.put("User-Agent", config.getString("User-Agent"));
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
    private final List<byte[]> audioBytesTemp = new ArrayList<>();
    private final List<Sentence.Word> wordsTemp = new ArrayList<>();

    @Override
    public void establish() throws TTSException, ExecutionException, InterruptedException, TimeoutException {
        super.connect();
        locker = new CompletableFuture<>();
        String res = locker.get(config.getInteger("TimeoutSeconds"), TimeUnit.SECONDS);
        if (res.equals("error")) {
            throw new TTSException("Server connect error");
        }
    }

    @Override
    public void terminate() {
        super.close();
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
        else if (s.contains("Path:audio.metadata")) {
            String jsonStr = s.substring(s.indexOf("{"));
            JSONArray metadata = JSONObject.parseObject(jsonStr).getJSONArray("Metadata");
            JSONObject data = metadata.getJSONObject(0).getJSONObject("Data");
            JSONObject text = data.getJSONObject("text");

            long start = data.getLong("Offset") / 1667;
            Sentence.Word word = new Sentence.Word(text.getString("Text"), (int) start, (int) (start + data.getLong("Duration") / 1667));
            wordsTemp.add(word);
        }
        log.trace(s);
    }

    private static final byte[] HEADER = {0x50, 0x61, 0x74, 0x68, 0x3a, 0x61, 0x75, 0x64, 0x69, 0x6f, 0x0d, 0x0a};
    @Override
    public void onMessage(ByteBuffer byteBuffer) {
        byte[] originalData = byteBuffer.array();
        int headerLength = HEADER.length;
        int dataLength = originalData.length;
        log.trace("Data length: " + dataLength);

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
            this.audioBytesTemp.add(audioData);
        }
    }

    @Override
    public void onClose(int i, String s, boolean b) {

    }

    @Override
    public void onError(Exception e) {
        locker.complete("error");
    }

    @Override
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
        String res = locker.get(config.getInteger("TimeoutSeconds"), TimeUnit.SECONDS);
        if (res.equals("error")) throw new TTSException("Server not response");
        Sentence sentence = new Sentence(s, Sentence.mergeAudio(audioBytesTemp), List.copyOf(wordsTemp), AudioEncodeUtils.AudioFormat.MP3_24KHZ_16BIT);
        //log.debug(sentence.toString());

        audioBytesTemp.clear();
        wordsTemp.clear();

        return sentence;
    }

    @Override
    public boolean isActive() {
        return super.isOpen();
    }
}