package mai_onsyn.VeloVoice2.App;

import mai_onsyn.AnimeFX2.LanguageManager;
import mai_onsyn.AnimeFX2.ThemeManager;

import java.io.File;

public class Runtime {
    public static final Config config = new Config();

    static {
        config.registerInteger("TimeoutMillis", 10000);
        config.registerInteger("MaxRetries", 3);
        config.registerInteger("TTSThreadCount", 1);



        ConfigManager cfgManager = new ConfigManager(config, new File("D:/Users/Desktop/config.json"), 1000);
        cfgManager.start();
    }

    public static ThemeManager themeManager = new ThemeManager();
    public static LanguageManager languageManager = new LanguageManager("lang");

}
