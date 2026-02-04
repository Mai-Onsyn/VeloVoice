package mai_onsyn.AnimeFX;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public class I18N {

    private static final Map<String, Map<String, String>> languageMap = new HashMap<>();
    private static final List<Localizable> registeredComponents = new ArrayList<>();
    private static final JSONObject langInfos;
    public static final List<Runnable> onChangedActions = new ArrayList<>();
    private static String currentLanguage = "en_us";

    private static final Logger log = LogManager.getLogger(I18N.class);


    private I18N() {}

    static {
        langInfos = JSONObject.parseObject(readJsonFile("lang/lang_info.json"));
        readLanguages();
    }

    public static void setLanguage(String language) {
        currentLanguage = language;
        Map<String, String> keyMap = languageMap.get(language);
        if (keyMap == null) {
            for (Localizable component : registeredComponents) {
                component.localize(component.getI18NKey());
            }
        }
        else {
            for (Localizable component : registeredComponents) {
                String defaultName = languageMap.get("en_us").getOrDefault(component.getI18NKey(), component.getI18NKey());
                component.localize(keyMap.getOrDefault(component.getI18NKey(), defaultName));
            }
        }

        onChangedActions.forEach(e -> {
            try {
                e.run();
            } catch (Throwable _) {}
        });
    }

    public static void registerComponent(Localizable component) {
        registeredComponents.add(component);
        registeredComponents.addAll(component.getChildrenLocalizable());
    }

    public static void registerComponents(Localizable... components) {
        for (Localizable component : components) {
            registerComponent(component);
        }
    }

    public static void addOnChangedAction(Runnable runnable) {
        onChangedActions.add(runnable);
    }

    public static JSONObject getLanguageInfos() {
        return langInfos;
    }

    public static String getCurrentValue(String key) {
        Map<String, String> keyMap = languageMap.get(currentLanguage);
        if (keyMap == null) {
            return key;
        }
        else {
            String defaultName = languageMap.get("en_us").getOrDefault(key, key);
            return keyMap.getOrDefault(key, defaultName);
        }
    }

    private static void readLanguages() {
        log.debug("Lang Infos: \n{}", JSONObject.toJSONString(I18N.langInfos, JSONWriter.Feature.PrettyFormat));

        for (String key : I18N.langInfos.keySet()) {
            try {
                JSONObject json = JSONObject.parseObject(readJsonFile("lang/" + key + ".json"));
                Map<String, String> flatMap = new HashMap<>();
                flattenJson("", json, flatMap);
                languageMap.put(key, flatMap);
            } catch (NullPointerException e) {
                log.warn("Language file not found: {}", key);
            }
        }
    }

    /**
     * 递归展开 JSON 为扁平化的 key
     * @param prefix 当前 key 前缀
     * @param json   当前层的 JSON
     * @param flatMap 扁平化结果存储
     */
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


    private static String readJsonFile(String resourceDir) {
        try (
                InputStream stream = I18N.class.getModule().getResourceAsStream(resourceDir);
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream))
        ) {
            StringBuilder sb = new StringBuilder();
            while (reader.ready()) {
                sb.append(reader.readLine());
            }
            return sb.toString();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
