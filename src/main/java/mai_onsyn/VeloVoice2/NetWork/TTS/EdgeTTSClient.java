package mai_onsyn.VeloVoice2.NetWork.TTS;

import mai_onsyn.VeloVoice2.Text.Sentence;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static mai_onsyn.VeloVoice2.App.Constants.URL;
import static mai_onsyn.VeloVoice2.App.Constants.ORIGIN;
import static mai_onsyn.VeloVoice2.App.Constants.USER_AGENT;

public class EdgeTTSClient extends WebSocketClient implements TTSClient {

    private static final Map<String, String> HEADERS = new HashMap<>();
    static {
        HEADERS.put("Origin", ORIGIN);
        HEADERS.put("Pragma", "no-cache");
        HEADERS.put("Cache-Control", "no-cache");
        HEADERS.put("User-Agent", USER_AGENT);
    }

    public static double voicePitch = 1.0;
    public static double voiceRate = 1.0;
    public static double voiceVolume = 1.0;


    public EdgeTTSClient() {
        super(URI.create(URL), HEADERS);
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
    }

    @Override
    public void onMessage(String s) {

    }

    @Override
    public void onClose(int i, String s, boolean b) {

    }

    @Override
    public void onError(Exception e) {

    }

    @Override
    public Sentence process(String s) {
        return null;
    }
}
