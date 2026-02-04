package mai_onsyn.VeloVoice.NetWork.TTS;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import mai_onsyn.AnimeFX.I18N;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

public class EdgeTTSVoice {

    private static final JSONArray jsonArray;
    private static final Logger log = LogManager.getLogger(EdgeTTSVoice.class);

    static {
        log.debug("Loading EdgeTTS voices...");
        StringBuilder sb = new StringBuilder();
        try (InputStream inputStream = EdgeTTSVoice.class.getClassLoader().getResourceAsStream("edgeTTS.json");
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"))
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            log.error(I18N.getCurrentValue("log.edge_tts_voice.error_load_json"), e);
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
