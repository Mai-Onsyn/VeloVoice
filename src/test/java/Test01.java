import mai_onsyn.VeloVoice2.App.Config;
import mai_onsyn.VeloVoice2.App.ConfigManager;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

public class Test01 {


    public static void main(String[] args) throws InterruptedException {
        File file = new File("D:/Users/Desktop/cfg.json");

        Config config = new Config();
        Config sub = new Config();

        config.registerBoolean("a", true);
        config.registerInteger("b", 1);
        config.registerMap("map", new LinkedHashMap<>(Map.of("key1", "value1", "key2", "value2")));

        sub.registerBoolean("a", true);
        sub.registerInteger("b", 1);
        config.registerConfig("sub", sub);


        ConfigManager configListener = new ConfigManager(config, file, 1000);
        configListener.start();

        Thread.sleep(1000);

        config.setInteger("b", 2);

        Thread.sleep(4000);
        System.out.println(config.getBoolean("a"));
        System.out.println(config.getInteger("b"));
    }
}