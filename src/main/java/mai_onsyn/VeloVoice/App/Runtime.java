package mai_onsyn.VeloVoice.App;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.paint.Color;
import mai_onsyn.AnimeFX.I18N;
import mai_onsyn.AnimeFX.ThemeManager;
import mai_onsyn.AnimeFX.Utls.Toolkit;
import mai_onsyn.VeloVoice.FrameFactory.FrameThemes;
import mai_onsyn.VeloVoice.NetWork.LoadTarget.Source;
import mai_onsyn.VeloVoice.NetWork.TTS.ResumableTTSClient;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Map;

import static mai_onsyn.VeloVoice.App.Constants.*;

public class Runtime {
    private static final Logger log = LogManager.getLogger(Runtime.class);

    public static final SimpleBooleanProperty isTTSRunning = new SimpleBooleanProperty(false);
    public static final SimpleBooleanProperty isTextLoadRunning = new SimpleBooleanProperty(false);
    public static boolean firstLaunch = false;

    public static final  ConfigManager configManager;

    public static final Config config = new Config();
    public static final Config edgeTTSConfig = new Config();
    public static final Config naturalTTSConfig = new Config();
    public static final Config multiTTSConfig = new Config();
    public static final Config gptSoVITSConfig = new Config();
    public static final Config textConfig = new Config();
    public static final Config voiceConfig = new Config();
    public static final Config windowConfig = new Config();

    private static final File CONFIG_FILE = new File(System.getProperty("user.dir") + "\\config.json");
    static {
        ConfigInitializer.initializeConfig();
        configManager = new ConfigManager(config, CONFIG_FILE, 500);
        configManager.start();
        ConfigInitializer.initializeListener();
    }

    public static final ThemeManager themeManager = new ThemeManager();

    public static Thread TTS_THREAD;
    public static ResumableTTSClient.ClientType CLIENT_TYPE;

    public static int currentTotalCount = 0;
    public static int totalCount = 0;
    public static String currentFileName = I18N.getCurrentValue("log.progress.initializing");
    public static long totalStartTime = 0;
    public static long currentStartTime = 0;
    public static final SimpleIntegerProperty totalFinished = new SimpleIntegerProperty(0);
    public static final SimpleIntegerProperty currentFinished = new SimpleIntegerProperty(0);

    public static final List<Runnable> cycleTasks = new ArrayList<>();
    static {
        Thread.ofVirtual().start(() -> {
            while (true) {
                try {
                    for (Runnable task : cycleTasks) {
                        task.run();
                    }
                    Thread.sleep(1000);
                } catch (InterruptedException | ConcurrentModificationException _) {}
            }
        });
    }

    //switched:
    public static boolean disableNaturalTTS = false;

    public static String stackTraceToString(Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

}
