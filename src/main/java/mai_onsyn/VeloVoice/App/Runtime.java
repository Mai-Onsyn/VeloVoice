package mai_onsyn.VeloVoice.App;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.paint.Color;
import mai_onsyn.AnimeFX.I18N;
import mai_onsyn.AnimeFX.ThemeManager;
import mai_onsyn.AnimeFX.Utls.Toolkit;
import mai_onsyn.VeloVoice.FrameFactory.FrameThemes;
import mai_onsyn.VeloVoice.NetWork.LoadTarget.Source;
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

    public static final SimpleBooleanProperty isRunning = new SimpleBooleanProperty(false);
    public static boolean firstLaunch = false;

    public static final  ConfigManager configManager;

    public static final Config config = new Config();
    public static final Config edgeTTSConfig = new Config();
    public static final Config textConfig = new Config();
    public static final Config voiceConfig = new Config();
    public static final Config windowConfig = new Config();
    private static final Logger log = LogManager.getLogger(Runtime.class);

    static {
        config.registerString("LogLevel", "INFO");
        config.registerInteger("TimeoutSeconds", 30);
        config.registerInteger("MaxRetries", 5);
        config.registerString("AudioSaveFolder", "C:/Users/Administrator/Desktop/");
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
            textConfig.registerInteger("SplitThresholds", 512);
            textConfig.registerString("ForceSplitChars", "\\n");
            textConfig.registerString("SplitChars", "。？！…， ");
            textConfig.registerString("LoadSource", "LocalTXT");
            textConfig.registerString("LoadUri", "C:/Users/Administrator/Desktop/");
            textConfig.registerString("SaveMethod", "TXTTree");
            textConfig.registerString("SaveUri", "C:/Users/Administrator/Desktop/");
            textConfig.registerString("TXTSaveEncoding", "UTF-8");
            textConfig.registerString("OrdinalFormat", "%02d. %s");
            textConfig.registerBoolean("OrdinalStartByZero", false);
        }

        config.registerConfig("Voice", voiceConfig);
        {
            voiceConfig.registerBoolean("SaveSRT", false);
            voiceConfig.registerBoolean("EnableVoiceShift", false);
            voiceConfig.registerDouble("VoiceRate", 1.0);
            voiceConfig.registerDouble("VoicePitch", 1.0);
            voiceConfig.registerBoolean("SaveInSections", false);
            voiceConfig.registerInteger("SectionLength", 20);
            voiceConfig.registerBoolean("SectionReadHead", true);
            voiceConfig.registerString("SectionUnitPattern", "_p%02d");
        }
        config.registerConfig("Window", windowConfig);
        {
            windowConfig.registerBoolean("DarkMode", true);
            windowConfig.registerString("ThemeColor", "#F08080");
            windowConfig.registerString("BackgroundImage", "textures/bg.png");
            windowConfig.registerDouble("BackgroundOpacity", 1.0);
            windowConfig.registerDouble("BackgroundBlur", 0);
            windowConfig.registerDouble("BackgroundScale", 1.01);
            windowConfig.registerString("Language", "en_us");
        }

        for (Map.Entry<String, Source> entry : sources.entrySet()) {
            config.registerConfig(entry.getKey(), entry.getValue().getConfig());
        }


        configManager = new ConfigManager(config, new File(System.getProperty("user.dir") + "\\config.json"), 500);
        configManager.start();

        {
            configManager.addOnChangedListener("Window.DarkMode", () -> {
                Boolean darkMode = windowConfig.getBoolean("DarkMode");
                WindowManager.setBackgroundShadowColor(Color.gray(darkMode ? FrameThemes.DARK_GRAY : FrameThemes.LIGHT_GRAY, windowConfig.getDouble("BackgroundOpacity")));
                FrameThemes.setDarkMode(darkMode);
            });
            configManager.addOnChangedListener("Window.ThemeColor", () -> {
                String themeColor = windowConfig.getString("ThemeColor");
                try {
                    FrameThemes.setThemeColor(Color.valueOf(themeColor));
                } catch (IllegalArgumentException | NullPointerException e) {
                    log.warn(I18N.getCurrentValue("log.runtime.warn.color_parse_failed"), themeColor);
                }
            });
            configManager.addOnChangedListener("Window.BackgroundImage", () -> {
                String imgUri = windowConfig.getString("BackgroundImage");
                try {
                    WindowManager.setBackgroundImage(Toolkit.loadImage(imgUri));
                } catch (IOException e) {
                    log.trace("Failed to load background image: " + e.getMessage());
                }
            });
            configManager.addOnChangedListener("Window.BackgroundOpacity", () -> {
                WindowManager.setBackgroundShadowColor(Color.gray(windowConfig.getBoolean("DarkMode") ? FrameThemes.DARK_GRAY : FrameThemes.LIGHT_GRAY, windowConfig.getDouble("BackgroundOpacity")));
            });
            configManager.addOnChangedListener("Window.BackgroundScale", () -> {
                WindowManager.setBackgroundScale(windowConfig.getDouble("BackgroundScale"));
            });
            configManager.addOnChangedListener("Window.BackgroundBlur", () -> {
                WindowManager.setBackgroundBlur(windowConfig.getDouble("BackgroundBlur"));
            });

            configManager.addOnChangedListener("LogLevel", () -> {
                Configurator.setRootLevel(Level.valueOf(config.getString("LogLevel")));
            });
        }
    }

    public static final ThemeManager themeManager = new ThemeManager();

    public static Thread EDGE_TTS_THREAD;

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

    public static String stackTraceToString(Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

}
