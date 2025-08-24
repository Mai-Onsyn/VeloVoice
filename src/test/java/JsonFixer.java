import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class JsonFixer {
    // 配置选项
    private static final String BASE_LANG = "en_us.json"; // 基准语言文件
    private static final boolean USE_TODO_MARK = false;    // 是否用 TODO 标记

    public static void main(String[] args) {
        // 配置参数
        String folderPath = "D:\\Users\\Desktop\\Files\\Projects\\Java\\VeloVoice\\src\\main\\resources\\lang";
        LinkedHashSet<String> excludeFiles = new LinkedHashSet<>(List.of("lang_info.json"));

        // 1. 解析所有 JSON 文件 -> 扁平化 Map
        LinkedHashMap<String, LinkedHashMap<String, String>> flatDataMap = parseJsonFiles(folderPath, excludeFiles);

        // 2. 取 en_us 的 key 作为基准
        LinkedHashMap<String, String> baseMap = flatDataMap.get(BASE_LANG);
        if (baseMap == null) {
            System.err.println("错误: 找不到基准语言文件 " + BASE_LANG);
            return;
        }

        // 3. 补全缺失 key
        for (Map.Entry<String, LinkedHashMap<String, String>> entry : flatDataMap.entrySet()) {
            String filename = entry.getKey();
            if (filename.equals(BASE_LANG)) continue;

            LinkedHashMap<String, String> fileMap = entry.getValue();
            boolean changed = false;

            for (Map.Entry<String, String> baseEntry : baseMap.entrySet()) {
                if (!fileMap.containsKey(baseEntry.getKey())) {
                    String value = USE_TODO_MARK
                            ? "TODO: " + baseEntry.getValue()
                            : baseEntry.getValue();

                    fileMap.put(baseEntry.getKey(), value);
                    System.out.println("文件 " + filename + " 缺失 " + baseEntry.getKey() + " → 已补全");
                    changed = true;
                }
            }

            if (changed) {
                // 转换回嵌套 JSON
                JSONObject nested = unflatten(fileMap);
                writeJsonToFile(folderPath + "\\" + filename, nested);
            }
        }

        System.out.println("✓ 检查并补全完成");
    }

    // 解析 JSON -> 扁平化 Map
    private static LinkedHashMap<String, LinkedHashMap<String, String>> parseJsonFiles(String folderPath, LinkedHashSet<String> excludeFiles) {
        LinkedHashMap<String, LinkedHashMap<String, String>> result = new LinkedHashMap<>();
        File folder = new File(folderPath);

        if (!folder.isDirectory()) {
            System.err.println("错误: 提供的路径不是文件夹");
            return result;
        }

        for (File file : Objects.requireNonNull(folder.listFiles())) {
            if (!file.isFile() || !file.getName().endsWith(".json")
                    || excludeFiles.contains(file.getName())) {
                continue;
            }

            try {
                String content = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
                JSONObject jsonObj = JSONObject.parseObject(content, Feature.OrderedField);

                LinkedHashMap<String, String> flatMap = new LinkedHashMap<>();
                flattenJson("", jsonObj, flatMap);

                result.put(file.getName(), flatMap);
                System.out.println("已解析: " + file.getName());
            } catch (IOException e) {
                System.err.println("读取文件失败: " + file.getName() + " - " + e.getMessage());
            } catch (Exception e) {
                System.err.println("JSON解析错误: " + file.getName() + " - " + e.getMessage());
            }
        }
        return result;
    }

    // 扁平化
    private static void flattenJson(String prefix, JSONObject json, LinkedHashMap<String, String> flatMap) {
        for (String key : json.keySet()) {
            Object value = json.get(key);
            String newKey = prefix.isEmpty() ? key : prefix + "." + key;

            if (value instanceof JSONObject obj) {
                flattenJson(newKey, obj, flatMap);
            } else {
                flatMap.put(newKey, String.valueOf(value));
            }
        }
    }

    // 反扁平化：把 "a.b.c" 转回嵌套 JSONObject
    private static JSONObject unflatten(LinkedHashMap<String, String> flatMap) {
        LinkedHashMap<String, Object> nested = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : flatMap.entrySet()) {
            insertNested(nested, entry.getKey().split("\\."), entry.getValue());
        }
        return new JSONObject(nested);
    }

    private static void insertNested(LinkedHashMap<String, Object> root, String[] keys, String value) {
        LinkedHashMap<String, Object> current = root;
        for (int i = 0; i < keys.length; i++) {
            String key = keys[i];
            if (i == keys.length - 1) {
                current.put(key, value);
            } else {
                if (!(current.get(key) instanceof LinkedHashMap)) {
                    current.put(key, new LinkedHashMap<String, Object>());
                }
                current = (LinkedHashMap<String, Object>) current.get(key);
            }
        }
    }

    // 写回 JSON 文件
    private static void writeJsonToFile(String filePath, JSONObject json) {
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(filePath), "UTF-8")) {
            writer.write(JSONObject.toJSONString(json, true)); // 美化输出
        } catch (IOException e) {
            System.err.println("写入文件失败: " + filePath + " - " + e.getMessage());
        }
    }
}
