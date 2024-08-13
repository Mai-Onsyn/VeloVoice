package mai_onsyn.VeloVoice.NetWork;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import static mai_onsyn.VeloVoice.App.Constants.*;

public class Voice {

    private static final List<JSONObject> voiceList = new ArrayList<>();
    static {
        flush();
    }

    private static void listVoice() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(VOICES_LIST_URL))
                .header("Authority", "speech.platform.bing.com")
                .header("Sec-CH-UA", "\" Not;A Brand\";v=\"99\", \"Microsoft Edge\";v=\"91\", \"Chromium\";v=\"91\"")
                .header("Sec-CH-UA-Mobile", "?0")
                .header("Cache-Control", "no-cache")
                .header("User-Agent", USER_AGENT)
                .header("Accept", "*/*")
                .header("Sec-Fetch-Site", "none")
                .header("Sec-Fetch-Mode", "cors")
                .header("Sec-Fetch-Dest", "empty")
                .header("Accept-Encoding", "gzip, deflate, br")
                .header("Accept-Language", "en-US,en;q=0.9")
                .version(HttpClient.Version.HTTP_2).GET().build();

        HttpClient httpClient = HttpClient.newBuilder().build();
        HttpResponse<String> response;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.body() != null) {
                JSONArray jsonArray = JSON.parseArray(response.body());
                voiceList.clear();
                for (Object o : jsonArray) {
                    voiceList.add(com.alibaba.fastjson.JSON.parseObject(o.toString()));
                }
                voiceList.sort((o1, o2) -> {
                    String shortName1 = o1.getString("ShortName");
                    String shortName2 = o2.getString("ShortName");

                    return -shortName1.compareTo(shortName2);
                });
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static JSONObject get(String shortName) {
        for (JSONObject voice : voiceList) {
            if (voice.getString("ShortName").equals(shortName)) {
                return voice;
            }
        }
        return null;
    }

    public static List<JSONObject> getVoiceList() {
        return voiceList;
    }

    public static void flush() {
        listVoice();
    }
}
