package mai_onsyn.AnimeFX2;

import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LanguageManager {

    private final Map<String, LanguageSwitchable> nodes;
    private final JSONObject languages;

    public LanguageManager(File langFolder) {
        this.nodes = new HashMap<>();

        languages = new JSONObject();

        try {
            File[] langs = langFolder.listFiles((dir, name) -> name.endsWith(".json"));
            if (langs == null || langs.length == 0) throw new IOException("No language file found!");
            for (File file : langs) {
                languages.put(file.getName().replace(".json", ""), JSONObject.parseObject(new String(Files.readAllBytes(file.toPath()))));
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
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
}
