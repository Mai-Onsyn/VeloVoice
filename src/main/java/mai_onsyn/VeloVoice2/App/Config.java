package mai_onsyn.VeloVoice2.App;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import javafx.scene.paint.Color;
import mai_onsyn.AnimeFX2.Utls.Toolkit;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Config extends JSONObject {

    private final Map<String, ConfigType> types = new HashMap<>();
    private final Map<String, Config> subConfig = new HashMap<>();

    public enum ConfigType {
        STRING, INTEGER, DOUBLE, BOOLEAN, COLOR, MAP, CONFIG
    }

    public Config() {
        super(new LinkedHashMap<>());
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this, true);
    }

    public ConfigType getType(String key) {
        return types.get(key);
    }

    public void registerString(String key, String value) {
        types.put(key, ConfigType.STRING);
        put(key, value);
    }
    public void registerInteger(String key, int value) {
        types.put(key, ConfigType.INTEGER);
        put(key, value);
    }
    public void registerDouble(String key, double value) {
        types.put(key, ConfigType.DOUBLE);
        put(key, value);
    }
    public void registerBoolean(String key, boolean value) {
        types.put(key, ConfigType.BOOLEAN);
        put(key, value);
    }
    public void registerColor(String key, Color value) {
        types.put(key, ConfigType.COLOR);
        put(key, Toolkit.colorToString(value));
    }
    public void registerMap(String key, Map<String, String> value) {
        types.put(key, ConfigType.MAP);
        put(key, value);
    }
    public void registerConfig(String key, Config value) {
        types.put(key, ConfigType.CONFIG);
        subConfig.put(key, value);
        put(key, value);
    }

    public void setString(String key, String value) {
        if (!types.containsKey(key)) throw new IllegalArgumentException("Key " + key + " is not registered");
        put(key, value);
    }
    public void setInteger(String key, int value) {
        if (!types.containsKey(key)) throw new IllegalArgumentException("Key " + key + " is not registered");
        put(key, value);
    }
    public void setDouble(String key, double value) {
        if (!types.containsKey(key)) throw new IllegalArgumentException("Key " + key + " is not registered");
        put(key, value);
    }
    public void setBoolean(String key, boolean value) {
        if (!types.containsKey(key)) throw new IllegalArgumentException("Key " + key + " is not registered");
        put(key, value);
    }
    public void setColor(String key, Color value) {
        if (!types.containsKey(key)) throw new IllegalArgumentException("Key " + key + " is not registered");
        put(key, Toolkit.colorToString(value));
    }
    public void setMap(String key, Map<String, String> value) {
        if (!types.containsKey(key)) throw new IllegalArgumentException("Key " + key + " is not registered");
        put(key, value);
    }

    public String getString(String key) {
        if (!types.containsKey(key)) throw new IllegalArgumentException("Key " + key + " is not registered");
        return super.getString(key);
    }
    public Double getDouble(String key) {
        if (!types.containsKey(key)) throw new IllegalArgumentException("Key " + key + " is not registered");
        return super.getDouble(key);
    }
    public Integer getInteger(String key) {
        if (!types.containsKey(key)) throw new IllegalArgumentException("Key " + key + " is not registered");
        return super.getInteger(key);
    }
    public Boolean getBoolean(String key) {
        if (!types.containsKey(key)) throw new IllegalArgumentException("Key " + key + " is not registered");
        return super.getBoolean(key);
    }
    public Color getColor(String key) {
        if (!types.containsKey(key)) throw new IllegalArgumentException("Key " + key + " is not registered");
        return Color.web(super.getString(key));
    }
    public Map<String, String> getMap(String key) {
        if (!types.containsKey(key)) throw new IllegalArgumentException("Key " + key + " is not registered");
        return parseMap(super.getString(key));
    }
    public Config getConfig(String key) {
        if (!types.containsKey(key)) throw new IllegalArgumentException("Key " + key + " is not registered");
        return subConfig.get(key);
    }

    public static LinkedHashMap<String, String> parseMap(String s) {
        Map<String, Object> innerMap = JSONObject.parseObject(s, Feature.OrderedField).getInnerMap();

        LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<>();
        for (Entry<String, Object> entry : innerMap.entrySet()) {
            linkedHashMap.put(entry.getKey(), (String) entry.getValue());
        }
        return linkedHashMap;
    }


    private String formatMap(Map<String, String> map) {
        return JSONObject.toJSONString(map, true);
    }
}
