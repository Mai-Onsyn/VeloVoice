import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class JsonAnalyzer {
    public static void main(String[] args) {
        // 配置参数
        String folderPath = "D:\\Users\\Desktop\\Files\\Projects\\Java\\VeloVoice\\src\\main\\resources\\lang";
        Set<String> excludeFiles = new HashSet<>(List.of("lang_info.json"));

        // 1. 解析文件夹内所有JSON文件（排除指定文件，并展开为扁平 key）
        Map<String, Map<String, String>> flatDataMap = parseJsonFiles(folderPath, excludeFiles);

        // 2. 收集所有 key 并去重
        Set<String> globalKeys = collectGlobalKeys(flatDataMap);
        System.out.println("全局Key集合 (" + globalKeys.size() + " 个):");
        printKeysVertically(globalKeys);

        // 3. 检查每个文件的key缺失情况
        checkMissingKeys(flatDataMap, globalKeys);
    }

    // 解析JSON文件 -> 扁平化 Map
    private static Map<String, Map<String, String>> parseJsonFiles(String folderPath, Set<String> excludeFiles) {
        Map<String, Map<String, String>> result = new HashMap<>();
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
                JSONObject jsonObj = JSONObject.parseObject(content);

                // 扁平化
                Map<String, String> flatMap = new LinkedHashMap<>();
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

    // 递归展开 JSON 为扁平化 key
    private static void flattenJson(String prefix, JSONObject json, Map<String, String> flatMap) {
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

    // 收集所有文件的全局 key
    private static Set<String> collectGlobalKeys(Map<String, Map<String, String>> dataMap) {
        Set<String> keys = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        for (Map<String, String> flatMap : dataMap.values()) {
            keys.addAll(flatMap.keySet());
        }
        return keys;
    }

    // 纵向打印键名列表
    private static void printKeysVertically(Set<String> keys) {
        for (String key : keys) {
            System.out.println("  - " + key);
        }
        System.out.println();
    }

    // 检查每个文件中缺失的 key
    private static void checkMissingKeys(Map<String, Map<String, String>> dataMap, Set<String> globalKeys) {
        System.out.println("缺失Key检查结果:");
        boolean foundMissing = false;

        for (Map.Entry<String, Map<String, String>> entry : dataMap.entrySet()) {
            String filename = entry.getKey();
            Set<String> fileKeys = entry.getValue().keySet();
            Set<String> missingKeys = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);

            for (String globalKey : globalKeys) {
                if (!fileKeys.contains(globalKey)) {
                    missingKeys.add(globalKey);
                }
            }

            if (!missingKeys.isEmpty()) {
                foundMissing = true;
                System.out.println("文件: " + filename);
                System.out.println("缺失Key (" + missingKeys.size() + " 个):");
                printKeysVertically(missingKeys);
            }
        }

        if (!foundMissing) {
            System.out.println("✓ 所有文件均包含完整的全局Key集合");
        }
    }
}
