package mai_onsyn.VeloVoice.FrameFactory;

import com.alibaba.fastjson.JSONObject;
import javafx.scene.Scene;
import javafx.stage.Stage;
import mai_onsyn.AnimeFX.I18N;
import mai_onsyn.AnimeFX.Module.AXButton;
import mai_onsyn.AnimeFX.Module.AXChoiceBox;
import mai_onsyn.AnimeFX.Utls.AXButtonGroup;
import mai_onsyn.VeloVoice.App.WindowManager;
import mai_onsyn.AnimeFX.layout.AXScrollPane;
import mai_onsyn.AnimeFX.layout.AutoPane;
import mai_onsyn.VeloVoice.App.Config;
import mai_onsyn.VeloVoice.App.ResourceManager;

import java.util.*;

import static mai_onsyn.VeloVoice.App.Constants.*;
import static mai_onsyn.VeloVoice.App.Runtime.*;
import static mai_onsyn.VeloVoice.FrameFactory.MainFactory.mkTitleBar;

public class SettingsFactory {

    public static final Stage settingsStage = new Stage();

    static {
        settingsStage.setTitle("Settings");
        settingsStage.getIcons().add(ResourceManager.icon);
        WindowManager.register(settingsStage);
        I18N.addOnChangedAction(() -> settingsStage.setTitle(I18N.getCurrentValue("stage.settings.title")));

        AutoPane root = mkkSettingsFrame();
        settingsStage.setScene(new Scene(root, 640, 480));
        settingsStage.setMinWidth(540);
        settingsStage.setMinHeight(300);

        settingsStage.setOnShown(event -> root.requestFocus());
    }

    private static AutoPane mkkSettingsFrame() {
        AutoPane root = new AutoPane();

        AXScrollPane scrollPane = new AXScrollPane();
        Config.ConfigBox scrollRoot = new Config.ConfigBox(UI_SPACING, UI_HEIGHT);
        scrollPane.setContent(scrollRoot);
        scrollPane.setFitToWidth(true);

        scrollRoot.getChildren().addAll(getFrameBox(), getNetworkBox(), getMiscBox());

        root.getChildren().add(scrollPane);
        root.setPosition(scrollPane, false, 20, 20, 20, 20);
        return root;
    }

    private static Config.ConfigBox getFrameBox() {
        Config.ConfigBox frameBox = new Config.ConfigBox(UI_SPACING, UI_HEIGHT);
        frameBox.getChildren().add(mkTitleBar("settings.window.title"));

        Config.ConfigItem darkMode = windowConfig.genSwitchItem("DarkMode");
        Config.ConfigItem themeColor = windowConfig.genInputStringItem("ThemeColor", "settings.window.input.theme_color");
        Config.ConfigItem backgroundImage = windowConfig.genInputStringItem("BackgroundImage", "settings.window.input.background_image");
        Config.ConfigItem backgroundOpacity = windowConfig.genFloatSlidItem("BackgroundOpacity", 0, 1.0, 0.05);
        Config.ConfigItem backgroundBlur = windowConfig.genFloatSlidItem("BackgroundBlur", 0, 60.0, 1.0);
        Config.ConfigItem backgroundScale = windowConfig.genFloatSlidItem("BackgroundScale", 1.0, 1.2, 0.01);
        darkMode.setI18NKey("settings.window.label.dark_mode");
        themeColor.setI18NKey("settings.window.label.theme_color");
        backgroundImage.setI18NKey("settings.window.label.background_image");
        backgroundOpacity.setI18NKey("settings.window.label.background_opacity");
        backgroundBlur.setI18NKey("settings.window.label.background_blur");
        backgroundScale.setI18NKey("settings.window.label.background_scale");
        themeColor.setChildrenI18NKeys(I18nKeyMaps.CONTEXT);
        backgroundImage.setChildrenI18NKeys(I18nKeyMaps.CONTEXT);

        Config.ConfigItem language; {
            List<String> langDisplayNames = new ArrayList<>();
            Map<String, String> dp_labelMapping = new HashMap<>();
            I18N.getLanguageInfos().forEach((k, v) -> {
                if (v instanceof JSONObject o) {
                    langDisplayNames.add(o.getString("local"));
                    dp_labelMapping.put(o.getString("local"), k);
                }
            });

            AXChoiceBox choiceBox = new AXChoiceBox();
            choiceBox.setTheme(FrameThemes.CHOICE_BOX);
            themeManager.register(choiceBox);
            I18N.registerComponent(choiceBox);
            for (String option : langDisplayNames) {
                AXButton item = choiceBox.createItem();
                item.setUserData(dp_labelMapping.get(option));
                item.setText(option);
                item.setOnMouseClicked(event -> {
                    windowConfig.setString("Language", item.getUserData().toString());
                    choiceBox.getTextLabel().setText(item.getText());
                });
            }

            //choose default
            AXButtonGroup group = choiceBox.getButtonGroup();
            group.getButtonList().forEach(button -> {
                if (Objects.equals(button.getUserData(), windowConfig.getString("Language"))) {
                    group.selectButton(button);
                }
            });
            choiceBox.setText(I18N.getLanguageInfos().getJSONObject(windowConfig.getString("Language")).getString("local"));

            group.addOnSelectChangedListener((o, ov, nv) -> {
                if (nv == null) {
                    windowConfig.setString("Language", "null");
                } else {
                    windowConfig.setString("Language", nv.getUserData().toString());
                    I18N.setLanguage(windowConfig.getString("Language"));
                }
            });

            language = new Config.ConfigItem("Language", choiceBox, 0.4);
        }
        language.setI18NKey("settings.window.label.lang");

        I18N.registerComponents(darkMode, themeColor, backgroundImage, backgroundOpacity, backgroundBlur, backgroundScale, language);
        frameBox.addConfigItem(darkMode, themeColor, backgroundImage, backgroundOpacity, backgroundBlur, backgroundScale, language);
        return frameBox;
    }

    private static Config.ConfigBox getNetworkBox() {
        Config.ConfigBox networkBox = new Config.ConfigBox(UI_SPACING, UI_HEIGHT);
        networkBox.getChildren().add(mkTitleBar("settings.net.title"));

        Config.ConfigItem timeoutSeconds = config.genIntegerSlidItem("TimeoutSeconds", 1, 120, 1);
        Config.ConfigItem maxRetries = config.genIntegerSlidItem("MaxRetries", 1, 15, 1);

        timeoutSeconds.setI18NKey("settings.net.label.timeout");
        maxRetries.setI18NKey("settings.net.label.max_retries");
        I18N.registerComponents(timeoutSeconds, maxRetries);

        networkBox.addConfigItem(timeoutSeconds, maxRetries);

        return networkBox;
    }

    private static Config.ConfigBox getMiscBox() {
        Config.ConfigBox miscBox = new Config.ConfigBox(UI_SPACING, UI_HEIGHT);
        miscBox.getChildren().add(mkTitleBar("settings.misc.title"));

        Config.ConfigItem logLevel = config.genChooseStringItem("LogLevel", List.of("TRACE", "DEBUG", "INFO", "WARN", "ERROR", "FATAL"));
        Config.ConfigItem previewText = config.genInputStringItem("PreviewText", "settings.misc.input.preview_text");
        Config.ConfigItem splitThresholds = textConfig.genIntegerSlidItem("SplitThresholds", 16, 4096, 16);
        Config.ConfigItem forceSplitChars = textConfig.genInputStringItem("ForceSplitChars", "settings.misc.input.split_force");
        Config.ConfigItem splitChars = textConfig.genInputStringItem("SplitChars", "settings.misc.input.split");
        logLevel.setI18NKey("settings.misc.label.log_level");
        previewText.setI18NKey("settings.misc.label.preview_text");
        splitThresholds.setI18NKey("settings.misc.label.split_thresholds");
        forceSplitChars.setI18NKey("settings.misc.label.split_force");
        splitChars.setI18NKey("settings.misc.label.split");
        previewText.setChildrenI18NKeys(I18nKeyMaps.CONTEXT);
        forceSplitChars.setChildrenI18NKeys(I18nKeyMaps.CONTEXT);
        splitChars.setChildrenI18NKeys(I18nKeyMaps.CONTEXT);
        I18N.registerComponents(logLevel, previewText, splitThresholds, forceSplitChars, splitChars);

        miscBox.addConfigItem(logLevel, previewText, splitThresholds, forceSplitChars, splitChars);
        return miscBox;
    }

}
