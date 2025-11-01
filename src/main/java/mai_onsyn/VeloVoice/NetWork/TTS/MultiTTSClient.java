package mai_onsyn.VeloVoice.NetWork.TTS;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import mai_onsyn.AnimeFX.I18N;
import mai_onsyn.VeloVoice.Audio.AudioEncodeUtils;
import mai_onsyn.VeloVoice.Text.Sentence;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;

import static mai_onsyn.VeloVoice.App.Runtime.config;
import static mai_onsyn.VeloVoice.App.Runtime.multiTTSConfig;

public class MultiTTSClient implements TTSClient {

    private static final Logger log = LogManager.getLogger(MultiTTSClient.class);
    private static String voiceModel = "Default";
    private static int voiceRate = 50;
    private static int voicePitch = 50;
    private static int voiceVolume = 50;

    public static void setVoiceModel(String voiceModel) {
        MultiTTSClient.voiceModel = voiceModel;
    }

    public static void setVoiceRate(int voiceRate) {
        MultiTTSClient.voiceRate = voiceRate;
    }

    public static void setVoicePitch(int voicePitch) {
        MultiTTSClient.voicePitch = voicePitch;
    }

    public static void setVoiceVolume(int voiceVolume) {
        MultiTTSClient.voiceVolume = voiceVolume;
    }

    @Override
    public void establish() throws Exception {
    }

    @Override
    public void terminate() {
    }

    @Override
    public Sentence process(String s) throws Exception {
        String url = multiTTSConfig.getString("Url");
        if (!url.startsWith("http")) {
            url = "http://" + url;
        }

        StringBuilder sb = new StringBuilder(url);
        if (!url.endsWith("/")) sb.append("/");
        sb.append("forward?");
        sb.append("text=").append(URLEncoder.encode(s, StandardCharsets.UTF_8));
        sb.append("&speed=").append(voiceRate);
        sb.append("&volume=").append(voiceVolume);
        sb.append("&pitch=").append(voicePitch);
        if (!Objects.equals(voiceModel, "Default")) {
            sb.append("&voice=").append(URLEncoder.encode(voiceModel, StandardCharsets.UTF_8));
        }

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(config.getInteger("TimeoutSeconds")))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(sb.toString()))
                .GET()
                .build();

        try {
            HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
            byte[] bytes = response.body();
            AudioEncodeUtils.WavInfo wavInfo = AudioEncodeUtils.parseWav(bytes);
            return new Sentence(
                    s,
                    wavInfo.audioData,
                    List.of(new Sentence.Word(s, 0, wavInfo.audioData.length)),
                    AudioEncodeUtils.AudioFormat.WAV_24KHZ_16BIT
            );
        } catch (Exception e) {
            log.error("Multi TTS request failed: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public boolean isActive() {
        return false;
    }

    public static List<String> getVoiceIDs() {
        List<String> result = new ArrayList<>();
        String url = multiTTSConfig.getString("Url");
        if (!url.startsWith("http")) {
            url = "http://" + url;
        }

        String fullUrl = url + (url.endsWith("/") ? "" : "/") + "voices";

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(1))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(fullUrl))
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject responseJson = JSONObject.parseObject(response.body(), Feature.OrderedField);
            if (responseJson.getBoolean("success")) {
                extractIds(responseJson, result);
            }
        } catch (Exception e) {
            log.warn(I18N.getCurrentValue("log.multi_tts_client.warn.get_voice_list_failed"), e.getMessage());
        }

        result.sort(Comparator.naturalOrder());
        return result;
    }

    private static void extractIds(Object obj, List<String> idList) {
        if (obj instanceof JSONObject jsonObject) {
            if (jsonObject.containsKey("id")) {
                Object idValue = jsonObject.get("id");
                if (idValue instanceof String) {
                    idList.add((String) idValue);
                }
            }
            for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
                extractIds(entry.getValue(), idList);
            }
        } else if (obj instanceof JSONArray jsonArray) {
            for (Object item : jsonArray) {
                extractIds(item, idList);
            }
        }
    }
}