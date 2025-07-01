package mai_onsyn.VeloVoice2.FrameFactory;

import javafx.geometry.Pos;
import mai_onsyn.AnimeFX2.I18N;
import mai_onsyn.AnimeFX2.Utls.AXLangLabel;
import mai_onsyn.AnimeFX2.layout.AutoPane;
import mai_onsyn.VeloVoice2.App.Config;

import static mai_onsyn.VeloVoice2.App.Constants.*;
import static mai_onsyn.VeloVoice2.App.Runtime.*;

public class AfterProcessFactory {


    public static Config.ConfigBox mkAfterProcessArea() {
        Config.ConfigBox configBox = new Config.ConfigBox(UI_SPACING, UI_HEIGHT);

        configBox.getChildren().addAll(getSRTBox(), getShiftBox());

        return configBox;
    }

    private static Config.ConfigBox getSRTBox() {
        Config.ConfigBox srtBox = new Config.ConfigBox(UI_SPACING, UI_HEIGHT);
        srtBox.getChildren().add(mkTitle("frame.main.item.label.after_process.title.srt"));

        Config.ConfigItem switchItem = voiceConfig.genSwitchItem("SaveSRT");
        switchItem.setI18NKey("frame.main.item.label.after_process.srt.switch");
        I18N.registerComponent(switchItem);


        srtBox.addConfigItem(switchItem);

        return srtBox;
    }

    private static Config.ConfigBox getShiftBox() {
        Config.ConfigBox shiftBox = new Config.ConfigBox(UI_SPACING, UI_HEIGHT);
        shiftBox.getChildren().add(mkTitle("frame.main.item.label.after_process.title.shift"));

        Config.ConfigItem rateItem = voiceConfig.genFloatSlidItem("VoiceRate", 0.1, 4.0, 0.1);
        Config.ConfigItem pitchItem = voiceConfig.genFloatSlidItem("VoicePitch", 0.1, 4.0, 0.1);
        rateItem.setI18NKey("frame.main.item.label.after_process.shift.rate");
        pitchItem.setI18NKey("frame.main.item.label.after_process.shift.pitch");
        I18N.registerComponent(rateItem);
        I18N.registerComponent(pitchItem);

        shiftBox.addConfigItem(rateItem, pitchItem);
        return shiftBox;
    }

    private static AutoPane mkTitle(String namespace) {
        AXLangLabel langLabel = new AXLangLabel();
        langLabel.setI18NKey(namespace);
        I18N.registerComponent(langLabel);
        //langLabel.setStyle("-fx-background-color: #2020ff20");
        langLabel.setAlignment(Pos.CENTER);

        AutoPane autoPane = new AutoPane();
        autoPane.getChildren().add(langLabel);
        autoPane.setMaxHeight(UI_HEIGHT);
        autoPane.setMinHeight(UI_HEIGHT);
        autoPane.setPosition(langLabel, false, 0, 0, 0, 0);
        return autoPane;
    }

}
