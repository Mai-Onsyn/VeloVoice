package mai_onsyn.VeloVoice2.NetWork.TTS;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.*;

public class EdgeTTSVoice {

    private static final JSONArray jsonArray;

    static {
        StringBuilder sb = new StringBuilder();
        try (InputStream inputStream = EdgeTTSVoice.class.getClassLoader().getResourceAsStream("edgeTTS.json");
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"))
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        jsonArray = JSONArray.parseArray(sb.toString().trim());
    }

    public static JSONObject getVoice(String name) {
        for (Object obj : jsonArray) {
            if (((JSONObject) obj).get("ShortName").equals(name)) {
                return (JSONObject) obj;
            }
        }
        return null;
    }
    public static JSONArray getVoices() {
        return jsonArray;
    }
}
