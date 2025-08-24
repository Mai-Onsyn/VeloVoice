import com.alibaba.fastjson.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class JsonUnflatter {
    private static final Logger log = LogManager.getLogger(JsonUnflatter.class);

    public static void main(String[] args) throws Exception {

        String inputFile = "D:\\Users\\Desktop\\Files\\Projects\\Java\\VeloVoice\\src\\main\\resources\\lang\\ja_jp.json";
        String outputFile = inputFile;

        String jsonText = readFile(inputFile);

        // 用 LinkedHashMap 保持顺序
        LinkedHashMap<String, Object> flatMap = JSONObject.parseObject(jsonText, LinkedHashMap.class);

        LinkedHashMap<String, Object> nested = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : flatMap.entrySet()) {
            insertNested(nested, entry.getKey().split("\\."), entry.getValue());
        }

        // 输出为嵌套格式，缩进 2 格
        String prettyJson = JSONObject.toJSONString(nested, true);

        try (Writer writer = new OutputStreamWriter(new FileOutputStream(outputFile), "UTF-8")) {
            writer.write(prettyJson);
        }

        System.out.println("转换完成: " + outputFile);
    }

    private static void insertNested(LinkedHashMap<String, Object> root, String[] keys, Object value) {
        LinkedHashMap<String, Object> current = root;
        for (int i = 0; i < keys.length; i++) {
            String key = keys[i];
            if (i == keys.length - 1) {
                current.put(key, value);
            } else {
                if (!(current.get(key) instanceof Map)) {
                    current.put(key, new LinkedHashMap<String, Object>());
                }
                current = (LinkedHashMap<String, Object>) current.get(key);
            }
        }
    }

    private static String readFile(String path) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }
}