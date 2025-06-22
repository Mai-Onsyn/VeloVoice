package mai_onsyn.AnimeFX2;

import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public class I18N {

    private static final Map<String, Map<String, String>> languageMap = new HashMap<>();
    private static final List<Localizable> registeredComponents = new ArrayList<>();

    private I18N() {}

    static {
        readLanguages(JSONObject.parseObject(readJsonFile("lang/lang_info.json")));
    }

    public static void setLanguage(String language) {
        Map<String, String> keyMap = languageMap.get(language);
        if (keyMap == null) {
            for (Localizable component : registeredComponents) {
                component.localize(component.getI18NKey());
            }
        }
        else {
            for (Localizable component : registeredComponents) {
                component.localize(keyMap.getOrDefault(component.getI18NKey(), component.getI18NKey()));
            }
        }

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

    private static void readLanguages(JSONObject lang_infos) {
        for (String key : lang_infos.keySet()) {
            try {
                languageMap.put(key, JSONObject.parseObject(readJsonFile("lang/" + key + ".json")).getInnerMap().entrySet()
                        .stream()
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                e -> (String) e.getValue()
                        )));
            } catch (NullPointerException e) {
                System.err.println("Language file not found: " + key);
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
