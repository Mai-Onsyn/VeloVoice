package mai_onsyn.VeloVoice.FrameFactory;

import com.alibaba.fastjson.JSONObject;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import mai_onsyn.AnimeFX.I18N;
import mai_onsyn.AnimeFX.Module.AXButton;
import mai_onsyn.AnimeFX.Module.AXChoiceBox;
import mai_onsyn.AnimeFX.Module.AXTextField;
import mai_onsyn.AnimeFX.Utls.AXButtonGroup;
import mai_onsyn.AnimeFX.Utls.AXFloatTextField;
import mai_onsyn.AnimeFX.Utls.AXIntegerTextField;
import mai_onsyn.AnimeFX.Utls.AXLangLabel;
import mai_onsyn.AnimeFX.layout.AutoPane;
import mai_onsyn.VeloVoice.App.Config;
import mai_onsyn.VeloVoice.App.Resource;
import mai_onsyn.VeloVoice.Audio.AudioPlayer;
import mai_onsyn.VeloVoice.NetWork.TTS.EdgeTTSClient;
import mai_onsyn.VeloVoice.NetWork.TTS.EdgeTTSVoice;
import mai_onsyn.VeloVoice.NetWork.TTS.NaturalTTSClient;
import mai_onsyn.VeloVoice.NetWork.TTS.ResumableTTSClient;
import mai_onsyn.VeloVoice.Text.Sentence;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import static mai_onsyn.VeloVoice.App.Constants.*;
import static mai_onsyn.VeloVoice.App.Constants.LANG_NAME_TO_HEADCODE_MAPPING;
import static mai_onsyn.VeloVoice.App.Constants.UI_HEIGHT;
import static mai_onsyn.VeloVoice.App.Runtime.*;
import static mai_onsyn.VeloVoice.FrameFactory.FrameThemes.BUTTON;

public class TTSFactory {
    private static final Logger log = LogManager.getLogger(TTSFactory.class);

    static Config.ConfigBox mkTextProcessArea() {

        Config.ConfigBox ttsArea = new Config.ConfigBox(UI_SPACING, UI_HEIGHT);

        Config.ConfigItem ttsEngine = config.genChooseStringItem("TTSEngine", List.of("Edge TTS", "Natural TTS"));
        ttsEngine.setI18NKey("main.tts.general.label.tts_engine");
        I18N.registerComponent(ttsEngine);
        ttsArea.addConfigItem(ttsEngine);

        List<Config.ConfigBox> ttsBoxList = new ArrayList<>();

        //Edge TTS
        Config.ConfigBox edgeTTSBox = getEdgeTTSBox();
        edgeTTSBox.setUserData("Edge TTS");
        ttsBoxList.add(edgeTTSBox);

        //Natural TTS
        Config.ConfigBox naturalTTSBox = getNaturalTTSBox();
        naturalTTSBox.setUserData("Natural TTS");
        ttsBoxList.add(naturalTTSBox);

        for (Config.ConfigBox box : ttsBoxList) {
            if (config.getString("TTSEngine").equals(box.getUserData())) {
                ttsArea.getChildren().add(1, box);
            }
        }

        for (ResumableTTSClient.ClientType clientType : ResumableTTSClient.ClientType.values()) {
            if (clientType.getName().equals(config.getString("TTSEngine"))) {
                CLIENT_TYPE = clientType;
            }
        }

        ((AXChoiceBox) ttsEngine.getContent()).getButtonGroup().addOnSelectChangedListener((o, ov, nv) -> {
            for (Config.ConfigBox box : ttsBoxList) {
                ttsArea.getChildren().remove(box);
                if (nv.getUserData().equals(box.getUserData())) {
                    ttsArea.getChildren().add(1, box);
                }
            }

            for (ResumableTTSClient.ClientType clientType : ResumableTTSClient.ClientType.values()) {
                if (clientType.getName().equals(nv.getUserData())) {
                    CLIENT_TYPE = clientType;
                }
            }
        });

        Config.ConfigItem audioFolderItem = config.genInputStringItem("AudioSaveFolder", "main.tts.general.input.audio_folder");
        audioFolderItem.setI18NKey("main.tts.general.label.audio_folder");
        ((AXTextField) audioFolderItem.getContent()).setChildrenI18NKeys(I18nKeyMaps.CONTEXT);
        I18N.registerComponent(audioFolderItem);

        ttsArea.addConfigItem(audioFolderItem);

        return ttsArea;
    }

    private static Config.ConfigBox getEdgeTTSBox() {
        Config.ConfigBox edgeTTSBox = new Config.ConfigBox(UI_SPACING, UI_HEIGHT);

        List<String> voiceShortNameList = new ArrayList<>();
        Set<String> langNameSet = new LinkedHashSet<>();
        {
            for (Object voice : EdgeTTSVoice.getVoices()) {
                String shortName = ((JSONObject) voice).getString("ShortName");
                voiceShortNameList.add(shortName);
            }
            voiceShortNameList.sort(String::compareTo);
            voiceShortNameList.forEach(shortName -> langNameSet.add(LANG_HEADCODE_TO_NAME_MAPPING.get(shortName.substring(0, 2))));
        }

        Config.ConfigItem langItem = edgeTTSConfig.genChooseStringItem("SelectedLanguage", langNameSet.stream().toList());
        Config.ConfigItem modelItem = edgeTTSConfig.genChooseStringItem("SelectedModel", voiceShortNameList);
        Config.ConfigItem rateItem = edgeTTSConfig.genFloatSlidItem("VoiceRate", 0.05, 2.0, 0.05);
        Config.ConfigItem volumeItem = edgeTTSConfig.genFloatSlidItem("VoiceVolume", 0.05, 2.0, 0.05);
        Config.ConfigItem pitchItem = edgeTTSConfig.genFloatSlidItem("VoicePitch", 0.05, 1.5, 0.05);
        Config.ConfigItem threadItem = edgeTTSConfig.genIntegerSlidItem("ThreadCount", 1, 4, 1);

        langItem.setI18NKey("main.tts.edge.label.lang");
        modelItem.setI18NKey("main.tts.edge.label.model");
        rateItem.setI18NKey("main.tts.edge.label.rate");
        volumeItem.setI18NKey("main.tts.edge.label.volume");
        pitchItem.setI18NKey("main.tts.edge.label.pitch");
        threadItem.setI18NKey("main.tts.edge.label.thread_count");

        I18N.registerComponents(langItem, modelItem, rateItem, volumeItem, pitchItem, threadItem);

        //modelItem配置
        {
            //添加预览按钮
            addPreviewButton(modelItem);
//            {
//                ImageView horn = new ImageView(Resource.horn);
//                previewButton.getChildren().add(horn);
//                previewButton.setPosition(horn, AutoPane.AlignmentMode.CENTER, AutoPane.LocateMode.RELATIVE, 0.5, 0.5);
//                horn.setFitWidth(UI_HEIGHT * 0.8);
//                horn.setFitHeight(UI_HEIGHT * 0.8);
//                previewButton.setTheme(BUTTON);
//                themeManager.register(previewButton);
//
//                previewButton.setOnMouseClicked(e -> {
//                    if (e.getButton() == MouseButton.PRIMARY) {
//                        Thread.ofVirtual().name("EdgeTTS-Preview-Thread").start(() -> {
//                            ResumableTTSClient client = new ResumableTTSClient(ResumableTTSClient.ClientType.EDGE);
//                            try {
//                                log.info(I18N.getCurrentValue("log.main_factory.info.load_preview"));
//                                client.establish();
//                                Sentence sentence = client.process(config.getString("PreviewText"));
//                                client.terminate();
//
//                                AudioPlayer.play(sentence.audioByteArray());
//                            } catch (Exception ex) {
//                                log.error(I18N.getCurrentValue("log.main_factory.error.load_preview_failed"), ex.toString());
//                                throw new RuntimeException(ex);
//                            }
//                        });
//                    }
//                });
//
//                modelItem.setPosition(modelChoiceBox, true, false, false, false, 0.4, UI_HEIGHT * 2, 0, 0);
//
//                modelItem.getChildren().add(previewButton);
//                modelItem.setPosition(previewButton, false, UI_HEIGHT * 1.4, 0, 0, 0);
//                modelItem.flipRelativeMode(previewButton, AutoPane.Motion.LEFT);
//            }

            AXChoiceBox modelChoiceBox = (AXChoiceBox) modelItem.getContent();
            AXChoiceBox langChoiceBox = (AXChoiceBox) langItem.getContent();
            //手动选择当前设置的模型
            {
                String initLang = edgeTTSConfig.getString("SelectedLanguage");
                AXButtonGroup langButtonGroup = langChoiceBox.getButtonGroup();
                //根据语言显示模型
                showEdgeTTSModel(modelChoiceBox, LANG_NAME_TO_HEADCODE_MAPPING.get(initLang));
                langButtonGroup.addOnSelectChangedListener((o, ov, nv) -> {
                    showEdgeTTSModel(modelChoiceBox, LANG_NAME_TO_HEADCODE_MAPPING.get(nv.getText()));
                });
            }
        }

        //UI数据应用到EdgeTTS配置项
        {
            ((AXChoiceBox) modelItem.getContent()).getButtonGroup().addOnSelectChangedListener((o, ov, nv) -> EdgeTTSClient.setVoice(nv.getText()));

            ((AXFloatTextField) rateItem.getContent().getChildren().get(1)).valueProperty().addListener((o, ov, nv) -> EdgeTTSClient.setVoiceRate(nv.doubleValue()));

            ((AXFloatTextField) volumeItem.getContent().getChildren().get(1)).valueProperty().addListener((o, ov, nv) -> EdgeTTSClient.setVoiceVolume(nv.doubleValue()));

            ((AXFloatTextField) pitchItem.getContent().getChildren().get(1)).valueProperty().addListener((o, ov, nv) -> EdgeTTSClient.setVoicePitch(nv.doubleValue()));
        }

        edgeTTSBox.addConfigItem(langItem, modelItem, rateItem, volumeItem, pitchItem, threadItem);

        return edgeTTSBox;
    }

    private static Config.ConfigBox getNaturalTTSBox() {
        Config.ConfigBox naturalTTSBox = new Config.ConfigBox(UI_SPACING, UI_HEIGHT);

        if (disableNaturalTTS) {
            AutoPane labelBox = new AutoPane();

            AXLangLabel label = new AXLangLabel();
            label.setI18NKey("main.tts.natural.label.disabled");
            I18N.registerComponent(label);
            label.setTheme(FrameThemes.STANDARD_LABEL);
            themeManager.register(label);
            labelBox.getChildren().add(label);
            labelBox.setPosition(label, false, 0, 0, 0, 0);

            naturalTTSBox.addConfigItem(labelBox);
        } else {
            Config.ConfigItem modelItem = naturalTTSConfig.genChooseStringItem("SelectedModel", NaturalTTSClient.getVoiceList());
            Config.ConfigItem rateItem = naturalTTSConfig.genIntegerSlidItem("VoiceRate", -10, 10, 1);
            Config.ConfigItem volumeItem = naturalTTSConfig.genFloatSlidItem("VoiceVolume", 0, 1.0, 0.05);
            Config.ConfigItem threadItem = naturalTTSConfig.genIntegerSlidItem("ThreadCount", 1, 64, 1);
            modelItem.setI18NKey("main.tts.natural.label.model");
            rateItem.setI18NKey("main.tts.natural.label.rate");
            volumeItem.setI18NKey("main.tts.natural.label.volume");
            threadItem.setI18NKey("main.tts.natural.label.thread_count");
            I18N.registerComponents(modelItem, rateItem, volumeItem, threadItem);

            //UI数据应用到NaturalTTS
            {
                ((AXChoiceBox) modelItem.getContent()).getButtonGroup().addOnSelectChangedListener((o, ov, nv) -> NaturalTTSClient.setVoice(nv.getText()));

                ((AXIntegerTextField) rateItem.getContent().getChildren().get(1)).valueProperty().addListener((o, ov, nv) -> NaturalTTSClient.setVoiceRate(nv.intValue()));

                ((AXFloatTextField) volumeItem.getContent().getChildren().get(1)).valueProperty().addListener((o, ov, nv) -> NaturalTTSClient.setVoiceVolume(nv.doubleValue()));
            }

            //预览按钮
            addPreviewButton(modelItem);

            naturalTTSBox.addConfigItem(modelItem, rateItem, volumeItem, threadItem);
        }
        return naturalTTSBox;
    }

    private static void showEdgeTTSModel(AXChoiceBox box, String lang) {
        box.getButtonGroup().getButtonList().forEach(button -> {
            String name = button.getText();
            if (name.startsWith(lang) && !box.containsItem(button)) box.showItem(button);
            else if (!name.startsWith(lang)) box.removeItem(button);
        });
    }

    private static void addPreviewButton(Config.ConfigItem modelItem) {
        AXButton previewButton = new AXButton();
        AXChoiceBox modelChoiceBox = (AXChoiceBox) modelItem.getContent();

        // 按钮图标和样式配置
        ImageView horn = new ImageView(Resource.horn);
        previewButton.getChildren().add(horn);
        previewButton.setPosition(horn, AutoPane.AlignmentMode.CENTER,
                AutoPane.LocateMode.RELATIVE, 0.5, 0.5);
        horn.setFitWidth(UI_HEIGHT * 0.8);
        horn.setFitHeight(UI_HEIGHT * 0.8);
        previewButton.setTheme(BUTTON);
        themeManager.register(previewButton);

        EventHandler<MouseEvent> previewAction =  e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                Thread.ofVirtual().name("TTS-Preview-Thread").start(() -> {
                    ResumableTTSClient client = new ResumableTTSClient(CLIENT_TYPE);
                    try {
                        log.info(I18N.getCurrentValue("log.main_factory.info.load_preview"));
                        client.establish();
                        Sentence sentence = client.process(config.getString("PreviewText"));
                        client.terminate();

                        AudioPlayer.play(sentence);
                    } catch (Exception ex) {
                        log.error(I18N.getCurrentValue("log.main_factory.error.load_preview_failed"), ex.toString());
                        throw new RuntimeException(ex);
                    }
                });
            }
        };

        // 事件处理
        previewButton.addEventFilter(MouseEvent.MOUSE_CLICKED, previewAction);

        // 布局配置
        modelItem.setPosition(modelChoiceBox, true, false, false, false, 0.4, UI_HEIGHT * 2, 0, 0);
        modelItem.getChildren().add(previewButton);
        modelItem.setPosition(previewButton, false, UI_HEIGHT * 1.4, 0, 0, 0);
        modelItem.flipRelativeMode(previewButton, AutoPane.Motion.LEFT);
    }
}
