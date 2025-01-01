package mai_onsyn.AnimeFX2;

import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class LanguageManager {

    private final Map<String, LanguageSwitchable> nodes;
    private final JSONObject languages;

    public LanguageManager(String langURI) {
        this.nodes = new HashMap<>();

        languages = new JSONObject();

        try {
//            File[] langs = langFolder.listFiles((dir, name) -> name.endsWith(".json"));
//            if (langs == null || langs.length == 0) throw new IOException("No language file found!");
//            for (File file : langs) {
//                languages.put(file.getName().replace(".json", ""), JSONObject.parseObject(new String(Files.readAllBytes(file.toPath()))));
//            }

            Map<String, String> jsonFiles = readJsonFiles(langURI);

        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void register(String key, LanguageSwitchable node) {
        this.nodes.put(key, node);
        Map<String, LanguageSwitchable> children = node.getLanguageElements();
        if (children == null || children.isEmpty()) return;
        for (Map.Entry<String, LanguageSwitchable> entry : children.entrySet()) {
            this.register(key + "." + entry.getKey(), entry.getValue());
        }
    }

    public void switchLanguage(String lang) {
        for (Map.Entry<String, LanguageSwitchable> entry : nodes.entrySet()) {
            try {
                String text = languages.getJSONObject(lang).getString(entry.getKey());
                if (text == null || text.isEmpty()) throw new NullPointerException();
                else entry.getValue().switchLanguage(text);
            } catch (NullPointerException e) {
                entry.getValue().switchLanguage(entry.getKey());
            }
        }
    }



    /**
     * 读取 resources 目录下所有 JSON 文件，并返回文件名和内容的映射
     *
     * @param resourceDir 资源目录（相对于 classpath）
     * @return JSON 文件名到内容的映射
     * @throws IOException 如果读取资源失败
     */
    private static Map<String, String> readJsonFiles(String resourceDir) throws IOException, URISyntaxException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Map<String, String> jsonFiles = new HashMap<>();

        // 读取资源目录的 URL
        URL resource = classLoader.getResource(resourceDir);
        if (resource == null) {
            throw new IOException("Resource directory not found: " + resourceDir);
        }

        if (resource.getProtocol().equals("file")) {
            // 开发阶段：直接从文件系统读取
            Path resourcePath = Paths.get(resource.toURI());
            try (Stream<Path> paths = Files.walk(resourcePath)) {
                paths.filter(Files::isRegularFile)
                        .filter(f -> f.toString().endsWith(".json"))
                        .forEach(file -> {
                            String fileName = file.getFileName().toString();
                            try {
                                String content = Files.readString(file);
                                jsonFiles.put(fileName, content);
                            } catch (IOException e) {
                                throw new RuntimeException("Failed to read file: " + fileName, e);
                            }
                        });
            }
        } else if (resource.getProtocol().equals("jar")) {
            // 打包后：从 JAR 文件中读取
            String jarPath = resource.getPath().substring(5, resource.getPath().indexOf("!"));
            try (ZipInputStream zip = new ZipInputStream(Files.newInputStream(Paths.get(jarPath)))) {
                ZipEntry entry;
                while ((entry = zip.getNextEntry()) != null) {
                    String entryName = entry.getName();
                    if (entryName.startsWith(resourceDir) && entryName.endsWith(".json")) {
                        String fileName = entryName.substring(entryName.lastIndexOf("/") + 1);
                        try (InputStream inputStream = classLoader.getResourceAsStream(entryName)) {
                            if (inputStream == null) continue;
                            String content = new BufferedReader(new InputStreamReader(inputStream))
                                    .lines()
                                    .collect(Collectors.joining("\n"));
                            jsonFiles.put(fileName, content);
                        }
                    }
                }
            }
        }

        return jsonFiles;
    }
}
