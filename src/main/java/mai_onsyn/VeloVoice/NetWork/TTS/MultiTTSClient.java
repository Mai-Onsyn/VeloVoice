package mai_onsyn.VeloVoice.NetWork.TTS;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import mai_onsyn.AnimeFX.I18N;
import mai_onsyn.VeloVoice.Audio.AudioEncodeUtils;
import mai_onsyn.VeloVoice.Text.Sentence;
import okhttp3.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.util.*;

import static mai_onsyn.VeloVoice.App.Runtime.config;
import static mai_onsyn.VeloVoice.App.Runtime.multiTTSConfig;

public class MultiTTSClient implements TTSClient{

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
        HttpUrl.Builder urlBuilder;
        String url = multiTTSConfig.getString("Url");
        HttpUrl httpUrl = HttpUrl.parse(url.startsWith("http") ? url : "http://" + url);
        if (httpUrl == null) {
            log.error(I18N.getCurrentValue("log.multi_tts_client.error.url_error"), url);
            return null;
        }
        urlBuilder = httpUrl.newBuilder();
        urlBuilder.addPathSegment("forward");
        urlBuilder.addQueryParameter("text", s);
        urlBuilder.addQueryParameter("speed", String.valueOf(voiceRate));
        urlBuilder.addQueryParameter("volume", String.valueOf(voiceVolume));
        urlBuilder.addQueryParameter("pitch", String.valueOf(voicePitch));
        if (!Objects.equals(voiceModel, "Default")) urlBuilder.addQueryParameter("voice", voiceModel);


        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(Duration.ofSeconds(config.getInteger("TimeoutSeconds")))
                .build();
        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .build();

        Call call = client.newCall(request);

        try (Response response = call.execute()) {
            byte[] bytes = response.body().bytes();
            AudioEncodeUtils.WavInfo wavInfo = AudioEncodeUtils.parseWav(bytes);
            return new Sentence(s, wavInfo.audioData, List.of(new Sentence.Word(s, 0, wavInfo.audioData.length)), AudioEncodeUtils.AudioFormat.WAV_24KHZ_16BIT);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean isActive() {
        return false;
    }


    public static List<String> getVoiceIDs() {
        List<String> result = new ArrayList<>();
        HttpUrl.Builder urlBuilder;
        String url = multiTTSConfig.getString("Url");
        HttpUrl httpUrl = HttpUrl.parse(url.startsWith("http") ? url : "http://" + url);
        if (httpUrl == null) {
            log.error(I18N.getCurrentValue("log.multi_tts_client.error.url_error"), url);
            return result;
        }
        urlBuilder = httpUrl.newBuilder();
        urlBuilder.addPathSegment("voices");

        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(Duration.ofSeconds(config.getInteger("TimeoutSeconds")))
                .build();
        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .build();

        Call call = client.newCall(request);
        try (Response response = call.execute()) {
            ResponseBody body = response.body();
            JSONObject responseJson = JSONObject.parseObject(body.string(), Feature.OrderedField);
            if (responseJson.getBoolean("success")) extractIds(responseJson, result);
        } catch (Exception e) {
            log.warn(I18N.getCurrentValue("log.multi_tts_client.warn.get_voice_list_failed"), e.getMessage());
        }
        result.sort(Comparator.naturalOrder());
        //result.forEach(System.out::println);
        return result;
    }

    private static void extractIds(Object obj, List<String> idList) {
        if (obj instanceof JSONObject jsonObject) {

            // 检查当前对象是否有 "id" 字段
            if (jsonObject.containsKey("id")) {
                Object idValue = jsonObject.get("id");
                if (idValue instanceof String) {
                    idList.add((String) idValue);
                }
            }

            // 递归遍历所有值
            for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
                extractIds(entry.getValue(), idList);
            }

        } else if (obj instanceof JSONArray jsonArray) {

            // 递归遍历数组中的每个元素
            for (Object item : jsonArray) {
                extractIds(item, idList);
            }
        }
    }
}
