package mai_onsyn.AnimeFX;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
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
        langInfos = JSONObject.parseObject(readJsonFile("lang/lang_info.json"), Feature.OrderedField);
        readLanguages(langInfos);
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
        if (!languageMap.containsKey(currentLanguage)) return key;
        return languageMap.get(currentLanguage).getOrDefault(key, key);
    }

    private static void readLanguages(JSONObject lang_infos) {
        log.debug("Lang Infos: \n" + JSONObject.toJSONString(lang_infos, true));

        for (String key : lang_infos.keySet()) {
            try {
                languageMap.put(key, JSONObject.parseObject(readJsonFile("lang/" + key + ".json")).getInnerMap().entrySet()
                        .stream()
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                e -> (String) e.getValue()
                        )));
            } catch (NullPointerException e) {
                log.warn("Language file not found: {}", key);
            }
        }
    }

    private static String readJsonFile(String resourceDir) {
        try (
                InputStream stream = I18N.class.getClassLoader().getResourceAsStream(resourceDir);
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        ) {
            StringBuilder sb = new StringBuilder();
            while (reader.ready()) {
                sb.append(reader.readLine());
            }
            return sb.toString();

        } catch (Exception e) {
            return null;
        }
    }
}
