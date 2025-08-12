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

        // 1. 解析文件夹内所有JSON文件（排除指定文件）
        Map<String, JSONObject> jsonDataMap = parseJsonFiles(folderPath, excludeFiles);

        // 2. 收集所有第一层key并去重
        Set<String> globalKeys = collectGlobalKeys(jsonDataMap);
        System.out.println("全局Key集合 (" + globalKeys.size() + " 个):");
        printKeysVertically(globalKeys);

        // 3. 检查每个文件的key缺失情况
        checkMissingKeys(jsonDataMap, globalKeys);
    }

    // 解析JSON文件并排除指定文件
    private static Map<String, JSONObject> parseJsonFiles(String folderPath, Set<String> excludeFiles) {
        Map<String, JSONObject> result = new HashMap<>();
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
                result.put(file.getName(), jsonObj);
                System.out.println("已解析: " + file.getName());
            } catch (IOException e) {
                System.err.println("读取文件失败: " + file.getName() + " - " + e.getMessage());
            } catch (Exception e) {
                System.err.println("JSON解析错误: " + file.getName() + " - " + e.getMessage());
            }
        }
        return result;
    }

    // 收集所有JSON文件的第一层key（去重）
    private static Set<String> collectGlobalKeys(Map<String, JSONObject> jsonDataMap) {
        Set<String> keys = new TreeSet<>(String.CASE_INSENSITIVE_ORDER); // 不区分大小写排序
        for (JSONObject json : jsonDataMap.values()) {
            keys.addAll(json.keySet());
        }
        return keys;
    }

    // 纵向打印键名列表
    private static void printKeysVertically(Set<String> keys) {
        for (String key : keys) {
            System.out.println("  - " + key);
        }
        System.out.println(); // 空行分隔
    }

    // 检查每个文件中缺失的全局key（纵向输出）
    private static void checkMissingKeys(Map<String, JSONObject> jsonDataMap, Set<String> globalKeys) {
        System.out.println("缺失Key检查结果:");
        boolean foundMissing = false;

        for (Map.Entry<String, JSONObject> entry : jsonDataMap.entrySet()) {
            String filename = entry.getKey();
            JSONObject json = entry.getValue();
            Set<String> fileKeys = json.keySet();
            Set<String> missingKeys = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);

            // 找出缺失的key
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
