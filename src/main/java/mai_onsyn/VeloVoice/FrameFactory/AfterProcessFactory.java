package mai_onsyn.VeloVoice.FrameFactory;

import mai_onsyn.AnimeFX.I18N;
import mai_onsyn.VeloVoice.App.Config;

import static mai_onsyn.VeloVoice.App.Constants.*;
import static mai_onsyn.VeloVoice.App.Runtime.*;
import static mai_onsyn.VeloVoice.FrameFactory.MainFactory.mkTitleBar;

public class AfterProcessFactory {


    public static Config.ConfigBox mkAfterProcessArea() {
        Config.ConfigBox configBox = new Config.ConfigBox(UI_SPACING, UI_HEIGHT);

        configBox.getChildren().addAll(getSRTBox(), getShiftBox(), getSectionBox());

        return configBox;
    }

    private static Config.ConfigBox getSRTBox() {
        Config.ConfigBox srtBox = new Config.ConfigBox(UI_SPACING, UI_HEIGHT);
        srtBox.getChildren().add(mkTitleBar("main.audio.srt.title"));

        Config.ConfigItem switchItem = voiceConfig.genSwitchItem("SaveSRT");
        switchItem.setI18NKey("main.audio.general.switch");
        I18N.registerComponent(switchItem);


        srtBox.addConfigItem(switchItem);

        return srtBox;
    }

    private static Config.ConfigBox getShiftBox() {
        Config.ConfigBox shiftBox = new Config.ConfigBox(UI_SPACING, UI_HEIGHT);
        shiftBox.getChildren().add(mkTitleBar("main.audio.shift.title"));

        Config.ConfigItem switchItem = voiceConfig.genSwitchItem("EnableVoiceShift");
        Config.ConfigItem rateItem = voiceConfig.genFloatSlidItem("VoiceRate", 0.1, 4.0, 0.1);
        Config.ConfigItem pitchItem = voiceConfig.genFloatSlidItem("VoicePitch", 0.1, 4.0, 0.1);
        switchItem.setI18NKey("main.audio.general.switch");
        rateItem.setI18NKey("main.audio.shift.label.rate");
        pitchItem.setI18NKey("main.audio.shift.label.pitch");
        I18N.registerComponents(switchItem, rateItem, pitchItem);

        shiftBox.addConfigItem(switchItem, rateItem, pitchItem);
        return shiftBox;
    }

    private static Config.ConfigBox getSectionBox() {
        Config.ConfigBox sectionBox = new Config.ConfigBox(UI_SPACING, UI_HEIGHT);

        sectionBox.getChildren().add(mkTitleBar("main.audio.section.title"));

        Config.ConfigItem switchItem = voiceConfig.genSwitchItem("SaveInSections");
        Config.ConfigItem lengthItem = voiceConfig.genIntegerSlidItem("SectionLength", 1, 60, 1);
        Config.ConfigItem readHeadItem = voiceConfig.genSwitchItem("SectionReadHead");
        Config.ConfigItem unitPatternItem = voiceConfig.genInputStringItem("SectionUnitPattern", "main.audio.section.input.unit_pattern");
        switchItem.setI18NKey("main.audio.general.switch");
        lengthItem.setI18NKey("main.audio.section.label.duration");
        readHeadItem.setI18NKey("main.audio.section.label.read_head");
        unitPatternItem.setI18NKey("main.audio.section.label.unit_pattern");
        unitPatternItem.setChildrenI18NKeys(I18nKeyMaps.CONTEXT);
        I18N.registerComponents(switchItem, lengthItem, readHeadItem, unitPatternItem);

        sectionBox.addConfigItem(switchItem, lengthItem, readHeadItem, unitPatternItem);

        return sectionBox;
    }

}
