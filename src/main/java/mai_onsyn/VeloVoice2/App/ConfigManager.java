package mai_onsyn.VeloVoice2.App;

import com.alibaba.fastjson.JSONObject;
import javafx.scene.paint.Color;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

public class ConfigManager {

    private final Config config;
    private final Thread thread;
    private final File path;

    private String lastString = "";

    public ConfigManager(Config config, File file, long waitTime) {
        this.config = config;
        this.path = file;

        try {
            load();
        } catch (Exception e) {
            save(config.toString());
            throw new RuntimeException(e);
        }

        thread = Thread.ofVirtual().name("Config-Listener-Thread").unstarted(() -> {
            try {
                while (true) {
                    String thisString = config.toString();
                    if (!Objects.equals(thisString, lastString)) {
                        save(thisString);
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

    private void load() throws Exception {
        String json = Files.readString(path.toPath());
        JSONObject jsonObject = JSONObject.parseObject(json);
        loadItem(config, jsonObject);
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
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
