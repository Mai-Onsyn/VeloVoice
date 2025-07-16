package mai_onsyn.VeloVoice.App;

import com.alibaba.fastjson.JSONObject;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import mai_onsyn.AnimeFX.I18N;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ConfigManager {

    private static final Logger log = LogManager.getLogger(ConfigManager.class);
    private final Config config;
    private final Thread thread;
    private final File path;
    private final List<Pair<String, Runnable>> onChangedListeners = new ArrayList<>();

    private String lastString = "";

    public ConfigManager(Config config, File file, long waitTime) {
        this.config = config;
        this.path = file;

        load();

        thread = Thread.ofVirtual().name("Config-Listener-Thread").unstarted(() -> {
            try {
                while (true) {
                    String thisString = config.toString();
                    if (!Objects.equals(thisString, lastString)) {
                        save(thisString);
                        onChanged(lastString, thisString);
                        lastString = thisString;
                    }
                    Thread.sleep(waitTime);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void start() {
        thread.start();
    }

    public void addOnChangedListener(String key, Runnable task) {
        onChangedListeners.add(new Pair<>(key, task));
    }

    private void load() {
        log.debug("Loading config from {}", path);
        if (!path.exists()) {
            log.warn(I18N.getCurrentValue("log.config_manager.warn.config_not_found"));
            Runtime.firstLaunch = true;
            save(config.toString());
        }
        try {
            String json = Files.readString(path.toPath());
            JSONObject jsonObject = JSONObject.parseObject(json);
            loadItem(config, jsonObject);
            log.debug("User config loaded");
        } catch (Exception e) {
            log.error(I18N.getCurrentValue("log.config_manager.error.load_failed"), e);
        }
    }

    private void loadItem(Config config, JSONObject jsonObject) throws IOException {
        for (String key : config.keySet()) {
            if (!jsonObject.containsKey(key)) throw new IOException("Key \"" + key + "\" not found in config file");
            switch (config.getType(key)) {
                case STRING -> config.put(key, jsonObject.getString(key));
                case INTEGER -> config.put(key, jsonObject.getInteger(key));
                case BOOLEAN -> config.put(key, jsonObject.getBoolean(key));
                case DOUBLE -> config.put(key, jsonObject.getDouble(key));
                case COLOR -> config.put(key, Color.web(jsonObject.getString(key)));
                case MAP -> config.put(key, Config.parseMap(jsonObject.getString(key)));
                case CONFIG -> {
                    JSONObject subJsonObject = jsonObject.getJSONObject(key);
                    loadItem(config.getConfig(key), subJsonObject);
                }
            }
        }
    }

    private void save(String s) {
        try (FileWriter fw = new FileWriter(path)) {
            fw.write(s);
            log.debug("Config file saved");
        } catch (IOException e) {
            log.error(I18N.getCurrentValue("log.config_manager.error.save_failed"), e);
        }
    }

    private void onChanged(String lastString, String thisString) {
        JSONObject last = JSONObject.parseObject(lastString);
        JSONObject current = JSONObject.parseObject(thisString);

        if (last == null || current == null) return;

        current.forEach((key, value) -> {
            if (value instanceof JSONObject currentChildObject) {
                if (last.get(key) instanceof JSONObject lastChildObject) {
                    currentChildObject.forEach((key2, value2) -> {
                        if (!Objects.equals(lastChildObject.get(key2).toString(), currentChildObject.get(key2).toString())) {
                            String wholeKey = key + "." + key2;
                            runListener(wholeKey);
                            log.debug("Config changed: {}", wholeKey);
                        }
                    });
                }
            } else {
                if (!Objects.equals(last.get(key).toString(), current.get(key).toString())) {
                    runListener(key);
                    log.debug("Config changed: {}", key);
                }
            }
        });
    }

    private void runListener(String key) {
        onChangedListeners.forEach(listener -> {
            if (listener.getKey().equals(key)) listener.getValue().run();
        });
    }
}
