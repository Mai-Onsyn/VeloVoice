package mai_onsyn.VeloVoice.App;

import javafx.scene.paint.Color;
import mai_onsyn.AnimeFX.I18N;
import mai_onsyn.AnimeFX.Utls.Toolkit;
import mai_onsyn.VeloVoice.FrameFactory.FrameThemes;
import mai_onsyn.VeloVoice.NetWork.LoadTarget.Source;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import java.io.IOException;
import java.util.Map;

import static mai_onsyn.VeloVoice.App.Constants.sources;
import static mai_onsyn.VeloVoice.App.Runtime.*;

public class ConfigInitializer {

    private static final Logger log = LogManager.getLogger(ConfigInitializer.class);

    static void initializeConfig() {
        config.registerString("LogLevel", "INFO");
        config.registerInteger("TimeoutSeconds", 30);
        config.registerInteger("MaxRetries", 5);
        config.registerString("AudioSaveFolder", "C:/Users/Administrator/Desktop/");
        config.registerString("PreviewText", "In the distance, the sound of waves crashing against the shore brought a sense of calm.");
        config.registerString("TTSEngine", "Edge TTS");

        config.registerConfig("EdgeTTS", edgeTTSConfig);
        {
            edgeTTSConfig.registerString("SelectedLanguage", "中文");
            edgeTTSConfig.registerString("SelectedModel", "zh-CN-XiaoxiaoNeural");
            edgeTTSConfig.registerDouble("VoiceRate", 1.0);
            edgeTTSConfig.registerDouble("VoiceVolume", 1.0);
            edgeTTSConfig.registerDouble("VoicePitch", 1.0);
            edgeTTSConfig.registerInteger("ThreadCount", 2);
        }

        config.registerConfig("NaturalTTS", naturalTTSConfig);
        {
            naturalTTSConfig.registerString("SelectedModel", "Microsoft Huihui Desktop");
            naturalTTSConfig.registerInteger("VoiceRate", 0);
            naturalTTSConfig.registerDouble("VoiceVolume", 1.0);
            naturalTTSConfig.registerInteger("ThreadCount", 16);
        }

        config.registerConfig("MultiTTS", multiTTSConfig);
        {
            multiTTSConfig.registerString("Url", "192.168.0.13:8774");
            multiTTSConfig.registerString("VoiceModel", "Default");
            multiTTSConfig.registerInteger("VoiceRate", 50);
            multiTTSConfig.registerInteger("VoiceVolume", 50);
            multiTTSConfig.registerInteger("VoicePitch", 50);
        }

        config.registerConfig("GPT-SoVITS", gptSoVITSConfig);
        {
            gptSoVITSConfig.registerString("Url", "localhost:9880");
            gptSoVITSConfig.registerString("TextLang", "zh");
            gptSoVITSConfig.registerString("RefAudioPath", "");
            gptSoVITSConfig.registerString("PromptLang", "zh");
            gptSoVITSConfig.registerString("PromptText", "");
            gptSoVITSConfig.registerInteger("TopK", 5);
            gptSoVITSConfig.registerDouble("TopP", 1.0);
            gptSoVITSConfig.registerDouble("Temperature", 1.0);
            gptSoVITSConfig.registerString("TextSplitMethod", "cut0");
            gptSoVITSConfig.registerInteger("BatchSize", 1);
            gptSoVITSConfig.registerDouble("BatchThreshold", 0.75);
            gptSoVITSConfig.registerBoolean("SplitBucket", true);
            gptSoVITSConfig.registerDouble("SpeedFactor", 1.0);
            gptSoVITSConfig.registerDouble("FragmentInterval", 0.3);
            gptSoVITSConfig.registerInteger("Seed", -1);
            gptSoVITSConfig.registerString("MediaType", "wav");
//            gptSoVITSConfig.registerBoolean("StreamingMode", false);
//            gptSoVITSConfig.registerBoolean("ParallelInfer", true);
            gptSoVITSConfig.registerDouble("RepetitionPenalty", 1.35);
            gptSoVITSConfig.registerInteger("SampleSteps", 32);
            gptSoVITSConfig.registerBoolean("SuperSampling", false);
        }


        config.registerConfig("Text", textConfig);
        {
            textConfig.registerInteger("SplitThresholds", 512);
            textConfig.registerString("ForceSplitChars", "\\n");
            textConfig.registerString("SplitChars", "。？！…，.?!|/\\~) ");
            textConfig.registerString("LoadSource", "LocalTXT");
            textConfig.registerString("LoadUri", "C:/Users/Administrator/Desktop/");
            textConfig.registerString("SaveMethod", "TXTTree");
            textConfig.registerBoolean("SaveSelected", false);
            textConfig.registerString("EpubTitle", "An EPUB Book");
            textConfig.registerString("EpubAuthor", "VeloVoice EPUB Writer");
            textConfig.registerString("EpubCover", "");
            textConfig.registerString("TXTSaveEncoding", "UTF-8");
            textConfig.registerString("OrdinalFormat", "%02d. %s");
            textConfig.registerBoolean("OrdinalStartByZero", false);
        }

        config.registerConfig("Voice", voiceConfig);
        {
            voiceConfig.registerBoolean("SaveSRT", false);
            voiceConfig.registerBoolean("DeleteSRTEndSymbol", false);
            voiceConfig.registerBoolean("EnableVoiceShift", false);
            voiceConfig.registerDouble("VoiceRate", 1.0);
            voiceConfig.registerDouble("VoicePitch", 1.0);
            voiceConfig.registerBoolean("SaveInSections", false);
            voiceConfig.registerInteger("SectionLength", 20);
            voiceConfig.registerBoolean("SectionReadHead", true);
            voiceConfig.registerString("SectionUnitPattern", "_p%02d");
            voiceConfig.registerBoolean("ClipAudio", false);
            voiceConfig.registerInteger("StartClipMillis", 0);
            voiceConfig.registerInteger("EndClipMillis", 0);
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
    }

    static void initializeListener() {
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
