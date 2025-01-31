package mai_onsyn.AnimeFX2;

import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class LanguageManager {

    private final Map<LanguageSwitchable, String> nodes;
    private final JSONObject languages;

    public LanguageManager(String langURI) {
        this.nodes = new LinkedHashMap<>();

        Map<String, Object> map = new LinkedHashMap<>();
        Map<String, String> jsonFiles = readJsonFiles(langURI);
        jsonFiles.forEach((key, value) -> map.put(key, JSONObject.parseObject(value)));
        languages = new JSONObject(map);
    }

    public void register(LanguageSwitchable node, String key) {
        this.nodes.put(node, key);
        Map<LanguageSwitchable, String> children = node.getLanguageElements();
        if (children == null || children.isEmpty()) return;
        for (Map.Entry<LanguageSwitchable, String> entry : children.entrySet()) {
            this.register(entry.getKey(), key + "." + entry.getValue());
        }
    }

    public void register(Object... obj) {
        if (obj.length % 2 != 0) throw new IllegalArgumentException("arguments count must be even");
        for (int i = 0; i < obj.length; i += 2) {
            this.register((LanguageSwitchable) obj[i], obj[i + 1].toString());
        }
    }

    public void switchLanguage(String lang) {
        //Set<String> namespaceSet = new LinkedHashSet<>();
        for (Map.Entry<LanguageSwitchable, String> entry : nodes.entrySet()) {
            //namespaceSet.add(entry.getValue());
            try {
                String text = languages.getJSONObject(lang).getString(entry.getValue());
                if (text == null || text.isEmpty()) throw new NullPointerException();
                else entry.getKey().switchLanguage(text);
            } catch (NullPointerException e) {
                entry.getKey().switchLanguage(entry.getValue());
            }
        }
        //namespaceSet.forEach(e -> System.out.printf("\"%s\": \"\",\n", e));
    }


    public static Map<String, String> readJsonFiles(String resourceDir) {
        Map<String, String> resultMap = new HashMap<>();
        try {
            // 使用 ClassLoader 获取资源
            ClassLoader classLoader = LanguageManager.class.getClassLoader();
            Enumeration<java.net.URL> resources = classLoader.getResources(resourceDir);

            while (resources.hasMoreElements()) {
                java.net.URL resourceURL = resources.nextElement();
                if (resourceURL.getProtocol().equals("jar")) {
                    // 处理 JAR 包内的资源
                    loadFromJar(resourceURL, resourceDir, resultMap);
                } else {
                    // 处理开发环境文件夹中的资源
                    loadFromDirectory(resourceURL, resultMap);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to read JSON files from directory: " + resourceDir, e);
        }
        return resultMap;
    }

    private static void loadFromJar(java.net.URL resourceURL, String resourceDir, Map<String, String> resultMap) throws Exception {
        String jarPath = resourceURL.getPath().substring(5, resourceURL.getPath().indexOf("!")); // 获取 JAR 文件路径
        try (JarFile jarFile = new JarFile(jarPath)) {
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.getName().startsWith(resourceDir) && entry.getName().endsWith(".json")) {
                    String fileName = entry.getName().replace(resourceDir + "/", "").replace(".json", ""); // 去掉目录和后缀
                    try (InputStream inputStream = jarFile.getInputStream(entry)) {
                        String content = readStream(inputStream);
                        resultMap.put(fileName, content);
                    }
                }
            }
        }
    }

    private static void loadFromDirectory(java.net.URL resourceURL, Map<String, String> resultMap) throws Exception {
        java.io.File directory = new java.io.File(resourceURL.toURI());
        java.io.File[] files = directory.listFiles((dir, name) -> name.endsWith(".json"));
        if (files != null) {
            for (java.io.File file : files) {
                String fileName = file.getName().replace(".json", ""); // 去掉后缀
                try (InputStream inputStream = new java.io.FileInputStream(file)) {
                    String content = readStream(inputStream);
                    resultMap.put(fileName, content);
                }
            }
        }
    }

    private static String readStream(InputStream inputStream) throws Exception {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString().trim(); // 去掉最后的换行
    }
}