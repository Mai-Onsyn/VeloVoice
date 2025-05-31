package mai_onsyn.VeloVoice2.App;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import mai_onsyn.AnimeFX2.LanguageManager;
import mai_onsyn.AnimeFX2.ThemeManager;
import mai_onsyn.VeloVoice2.NetWork.Item.LocalTXT;
import mai_onsyn.VeloVoice2.NetWork.Item.Source;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Runtime {

    public static final SimpleBooleanProperty isRunning = new SimpleBooleanProperty(false);
    public static final Map<String, Source> sources = new LinkedHashMap<>();
    static {    //在这里添加自定义的加载源
        sources.put("LocalTXT", new LocalTXT());
    }

    public static final Config config = new Config();
    public static final Config edgeTTSConfig = new Config();
    public static final Config textConfig = new Config();

    static {
        config.registerInteger("TimeoutMillis", 10000);
        config.registerInteger("MaxRetries", 3);
        config.registerString("AudioSaveFolder", "");
        config.registerString("PreviewText", "Hello, World!");

        config.registerConfig("EdgeTTS", edgeTTSConfig);
        {
            edgeTTSConfig.registerString("SelectedLanguage", "中文");
            edgeTTSConfig.registerString("SelectedModel", "zh-CN-XiaoxiaoNeural");
            edgeTTSConfig.registerDouble("VoiceRate", 1.0);
            edgeTTSConfig.registerDouble("VoiceVolume", 1.0);
            edgeTTSConfig.registerDouble("VoicePitch", 1.0);
            edgeTTSConfig.registerInteger("ThreadCount", 2);
        }
        
        config.registerConfig("Text", textConfig);
        {
            textConfig.registerInteger("SplitLength", 512);
            textConfig.registerString("ForceSplitChars", "\n");
            textConfig.registerString("SplitChars", "。？！…， ");
            textConfig.registerString("LoadSource", "LocalTXT");
            textConfig.registerString("LoadUri", "");
        }

        for (Map.Entry<String, Source> entry : sources.entrySet()) {
            config.registerConfig(entry.getKey(), entry.getValue().getConfig());
        }


        ConfigManager cfgManager = new ConfigManager(config, new File(System.getProperty("user.dir") + "\\config.json"), 1000);
        cfgManager.start();
    }

    public static final ThemeManager themeManager = new ThemeManager();
    public static final LanguageManager languageManager = new LanguageManager("lang");

    public static Thread EDGE_TTS_THREAD;

    public static int currentTotalCount = 0;
    public static int totalCount = 0;
    public static final SimpleIntegerProperty totalFinished = new SimpleIntegerProperty(0);
    public static final SimpleIntegerProperty currentFinished = new SimpleIntegerProperty(0);


    public static final List<Runnable> cycleTasks = new ArrayList<>();
    static {
        Thread.ofVirtual().start(() -> {
            try {
                while (true) {
                    for (Runnable task : cycleTasks) {
                        task.run();
                    }
                    Thread.sleep(1000);
                }
            } catch (InterruptedException _) {
            }
        });
    }

}
