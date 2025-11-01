package mai_onsyn.VeloVoice.FrameFactory;

import com.alibaba.fastjson.JSONObject;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import mai_onsyn.AnimeFX.I18N;
import mai_onsyn.AnimeFX.Module.AXButton;
import mai_onsyn.AnimeFX.Module.AXChoiceBox;
import mai_onsyn.AnimeFX.Module.AXTextField;
import mai_onsyn.AnimeFX.Utls.AXButtonGroup;
import mai_onsyn.AnimeFX.Utls.AXFloatTextField;
import mai_onsyn.AnimeFX.Utls.AXIntegerTextField;
import mai_onsyn.AnimeFX.Utls.AXLangLabel;
import mai_onsyn.AnimeFX.layout.AXScrollPane;
import mai_onsyn.AnimeFX.layout.AutoPane;
import mai_onsyn.VeloVoice.App.Config;
import mai_onsyn.VeloVoice.App.ResourceManager;
import mai_onsyn.VeloVoice.App.WindowManager;
import mai_onsyn.VeloVoice.Audio.AudioPlayer;
import mai_onsyn.VeloVoice.NetWork.TTS.*;
import mai_onsyn.VeloVoice.Text.Sentence;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static mai_onsyn.VeloVoice.App.Constants.*;
import static mai_onsyn.VeloVoice.App.Constants.LANG_NAME_TO_HEADCODE_MAPPING;
import static mai_onsyn.VeloVoice.App.Constants.UI_HEIGHT;
import static mai_onsyn.VeloVoice.App.Runtime.*;
import static mai_onsyn.VeloVoice.FrameFactory.FrameThemes.BUTTON;

public class TTSFactory {
    private static final Logger log = LogManager.getLogger(TTSFactory.class);

    static Config.ConfigBox mkTextProcessArea() {

        Config.ConfigBox ttsArea = new Config.ConfigBox(UI_SPACING, UI_HEIGHT);

        Config.ConfigItem ttsEngine = config.genChooseStringItem("TTSEngine", availableTTSNames);
        ttsEngine.setI18NKey("main.tts.general.label.tts_engine");
        I18N.registerComponent(ttsEngine);
        ttsArea.addConfigItem(ttsEngine);

        List<Config.ConfigBox> ttsBoxList = getTTSBoxList();

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

    private static List<Config.ConfigBox> getTTSBoxList() {
        List<Config.ConfigBox> ttsBoxList = new ArrayList<>();

        //Edge TTS
        Config.ConfigBox edgeTTSBox = getEdgeTTSBox();
        edgeTTSBox.setUserData("Edge TTS");
        ttsBoxList.add(edgeTTSBox);

        //Natural TTS
        Config.ConfigBox naturalTTSBox = getNaturalTTSBox();
        naturalTTSBox.setUserData("Natural TTS");
        ttsBoxList.add(naturalTTSBox);

        //Multi TTS
        Config.ConfigBox multiTTSBox = getMultiTTSBox();
        multiTTSBox.setUserData("Multi TTS");
        ttsBoxList.add(multiTTSBox);

        //GPT-SoVITS
        Config.ConfigBox gptSoVITSBox = getGPTSoVITSBox();
        gptSoVITSBox.setUserData("GPT-SoVITS TTS");
        ttsBoxList.add(gptSoVITSBox);

        return ttsBoxList;
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

    private static Config.ConfigBox getMultiTTSBox() {
        Config.ConfigBox multiTTSBox = new Config.ConfigBox(UI_SPACING, UI_HEIGHT);

        List<String> voices = MultiTTSClient.getVoiceIDs();
        voices.addFirst("Default");
        Config.ConfigItem url = multiTTSConfig.genInputStringItem("Url", "main.tts.multi.input.url");
        Config.ConfigItem voiceModel = multiTTSConfig.genChooseStringItem("VoiceModel", voices);
        Config.ConfigItem voiceRate = multiTTSConfig.genIntegerSlidItem("VoiceRate", 0, 100, 1);
        Config.ConfigItem voiceVolume = multiTTSConfig.genIntegerSlidItem("VoiceVolume", 0, 100, 1);
        Config.ConfigItem voicePitch = multiTTSConfig.genIntegerSlidItem("VoicePitch", 0, 100, 1);
        addPreviewButton(voiceModel);
        url.setI18NKey("main.tts.multi.label.url");
        voiceModel.setI18NKey("main.tts.multi.label.voice_model");
        voiceRate.setI18NKey("main.tts.multi.label.voice_rate");
        voiceVolume.setI18NKey("main.tts.multi.label.voice_volume");
        voicePitch.setI18NKey("main.tts.multi.label.voice_pitch");
        I18N.registerComponents(url, voiceModel, voiceRate, voiceVolume, voicePitch);

        //UI implements
        {
            ((AXChoiceBox) voiceModel.getContent()).getButtonGroup().addOnSelectChangedListener((o, ov, nv) -> MultiTTSClient.setVoiceModel(nv.getText()));

            ((AXIntegerTextField) voiceRate.getContent().getChildren().get(1)).valueProperty().addListener((o, ov, nv) -> MultiTTSClient.setVoiceRate(nv.intValue()));

            ((AXIntegerTextField) voiceVolume.getContent().getChildren().get(1)).valueProperty().addListener((o, ov, nv) -> MultiTTSClient.setVoiceVolume(nv.intValue()));

            ((AXIntegerTextField) voicePitch.getContent().getChildren().get(1)).valueProperty().addListener((o, ov, nv) -> MultiTTSClient.setVoicePitch(nv.intValue()));
        }

        //flush model button
        {
            AXButton flushButton = new AXButton();
            ImageView icon = new ImageView(ResourceManager.flush);
            icon.setFitWidth(UI_HEIGHT * 0.8);
            icon.setFitHeight(UI_HEIGHT * 0.8);
            flushButton.getChildren().add(icon);
            flushButton.setPosition(icon, AutoPane.AlignmentMode.CENTER, AutoPane.LocateMode.RELATIVE, 0.5, 0.5);
            flushButton.setTheme(BUTTON);
            themeManager.register(flushButton);

            url.setPosition(url.getContent(), true, false, false, false, 0.4, UI_HEIGHT * 2, 0, 0);
            url.getChildren().add(flushButton);
            url.setPosition(flushButton, false, UI_HEIGHT * 1.4, 0, 0, 0);
            url.flipRelativeMode(flushButton, AutoPane.Motion.LEFT);

            AXChoiceBox modelChoiceBox = (AXChoiceBox) voiceModel.getContent();
            flushButton.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                if (e.getButton() == MouseButton.PRIMARY) {
                    List<AXButton> oldItemList = modelChoiceBox.getButtonGroup().getButtonList();
                    List<String> oldOptions = oldItemList.stream().map(b -> (String) b.getUserData()).toList();
                    List<AXButton> oldExclusions = new ArrayList<>();

                    List<String> options = MultiTTSClient.getVoiceIDs();
                    options.addFirst("Default");
                    for (String option : options) {
                        if (oldOptions.contains(option)) {
                            List<AXButton> buttonList = modelChoiceBox.getButtonGroup().getButtonList();
                            AXButton targetButton = null;
                            for (AXButton button : buttonList) {
                                if (button.getUserData().equals(option)) {
                                    targetButton = button;
                                    break;
                                }
                            }
                            if (!modelChoiceBox.containsItem(targetButton)) modelChoiceBox.showItem(targetButton);
                            continue;
                        }
                        AXButton item = modelChoiceBox.createItem();
                        item.setUserData(option);
                        item.setText(option);
                        item.setOnMouseClicked(event -> {
                            multiTTSConfig.setString("VoiceModel", option);
                            modelChoiceBox.getTextLabel().setText(item.getText());
                        });
                    }
                    multiTTSConfig.setString("VoiceModel", "Default");
                    modelChoiceBox.getTextLabel().setText("Default");

                    oldItemList.forEach(b -> {
                        if (!options.contains((String) b.getUserData())) oldExclusions.add(b);
                    });
                    oldExclusions.forEach(modelChoiceBox::removeItem);

                    modelChoiceBox.getButtonGroup().selectButton(modelChoiceBox.getButtonGroup().getButtonList().getFirst());

                    log.info(I18N.getCurrentValue("log.tts_factory.info.flushed"));
                }
            });
        }


        multiTTSBox.addConfigItem(url, voiceModel, voiceRate, voiceVolume, voicePitch);
        return multiTTSBox;
    }

    private static Config.ConfigBox getGPTSoVITSBox() {
        Config.ConfigBox gptSoVITSBox = new Config.ConfigBox(UI_SPACING, UI_HEIGHT);

        List<String> langs = List.of("zh", "ja", "en", "ko", "yue");
        Config.ConfigItem url = gptSoVITSConfig.genInputStringItem("Url", "main.tts.gpt.input.url");
        Config.ConfigItem textLang = gptSoVITSConfig.genChooseStringItem("TextLang", langs);
        Config.ConfigItem refAudioPath = gptSoVITSConfig.genInputStringItem("RefAudioPath", "main.tts.gpt.input.ref_audio_path");
        Config.ConfigItem promptLang = gptSoVITSConfig.genChooseStringItem("PromptLang", langs);
        Config.ConfigItem promptText = gptSoVITSConfig.genInputStringItem("PromptText", "main.tts.gpt.input.prompt_text");

        Config.ConfigItem topK = gptSoVITSConfig.genIntegerSlidItem("TopK", 1, 100, 1);
        Config.ConfigItem topP = gptSoVITSConfig.genFloatSlidItem("TopP", 0, 1, 0.05);
        Config.ConfigItem temperature = gptSoVITSConfig.genFloatSlidItem("Temperature", 0, 1, 0.05);

        Config.ConfigItem textSplitMethod = gptSoVITSConfig.genInputStringItem("TextSplitMethod", "main.tts.gpt.input.text_split_method");
        Config.ConfigItem batchSize = gptSoVITSConfig.genIntegerSlidItem("BatchSize", 1, 16, 1);
        Config.ConfigItem batchThreshold = gptSoVITSConfig.genFloatSlidItem("BatchThreshold", 0.1, 1.0, 0.05);
        Config.ConfigItem splitBucket = gptSoVITSConfig.genSwitchItem("SplitBucket");
        Config.ConfigItem speedFactor = gptSoVITSConfig.genFloatSlidItem("SpeedFactor", 0.5, 2.0, 0.05);
        Config.ConfigItem fragmentInterval = gptSoVITSConfig.genFloatSlidItem("FragmentInterval", 0.01, 1.0, 0.01);
        Config.ConfigItem seed = gptSoVITSConfig.genInputIntegerItem("Seed", -2147483648, 2147483647, "main.tts.gpt.input.seed");
        Config.ConfigItem mediaType = gptSoVITSConfig.genInputStringItem("MediaType", "Unsupported");
        Config.ConfigItem repetitionPenalty = gptSoVITSConfig.genFloatSlidItem("RepetitionPenalty", 1.0, 2.0, 0.05);
        Config.ConfigItem sampleSteps = gptSoVITSConfig.genIntegerSlidItem("SampleSteps", 16, 64, 16);
        Config.ConfigItem superSampling = gptSoVITSConfig.genSwitchItem("SuperSampling");
        mediaType.setDisable(true);

        url.setI18NKey("main.tts.gpt.label.url");
        textLang.setI18NKey("main.tts.gpt.label.text_lang");
        refAudioPath.setI18NKey("main.tts.gpt.label.ref_audio_path");
        promptLang.setI18NKey("main.tts.gpt.label.prompt_lang");
        promptText.setI18NKey("main.tts.gpt.label.prompt_text");
        topK.setI18NKey("main.tts.gpt.label.top_k");
        topP.setI18NKey("main.tts.gpt.label.top_p");
        temperature.setI18NKey("main.tts.gpt.label.temperature");
        textSplitMethod.setI18NKey("main.tts.gpt.label.text_split_method");
        batchSize.setI18NKey("main.tts.gpt.label.batch_size");
        batchThreshold.setI18NKey("main.tts.gpt.label.batch_threshold");
        splitBucket.setI18NKey("main.tts.gpt.label.split_bucket");
        speedFactor.setI18NKey("main.tts.gpt.label.speed_factor");
        fragmentInterval.setI18NKey("main.tts.gpt.label.fragment_interval");
        seed.setI18NKey("main.tts.gpt.label.seed");
        mediaType.setI18NKey("main.tts.gpt.label.media_type");
        repetitionPenalty.setI18NKey("main.tts.gpt.label.repetition_penalty");
        sampleSteps.setI18NKey("main.tts.gpt.label.sample_steps");
        superSampling.setI18NKey("main.tts.gpt.label.super_sampling");


        Stage advanceStage = new Stage();
        {
            advanceStage.setTitle("Settings");
            advanceStage.getIcons().add(ResourceManager.icon);
            WindowManager.register(advanceStage);
            I18N.addOnChangedAction(() -> advanceStage.setTitle(I18N.getCurrentValue("stage.sovits_advance.title")));

            AutoPane root = new AutoPane();
            advanceStage.setScene(new Scene(root, 640, 480));
            advanceStage.setMinWidth(540);
            advanceStage.setMinHeight(300);

            advanceStage.setOnShown(event -> root.requestFocus());

            Config.ConfigBox advanceBox = new Config.ConfigBox(UI_SPACING, UI_HEIGHT);
            AXScrollPane scrollPane = new AXScrollPane(advanceBox);
            scrollPane.setFitToWidth(true);
            advanceBox.addConfigItem(textSplitMethod, batchSize, batchThreshold, splitBucket, seed, mediaType, repetitionPenalty, sampleSteps, superSampling);
            root.getChildren().add(scrollPane);
            root.setPosition(scrollPane, false, 20, 20, 20, 20);
        }
        AXButton advanceButton = new AXButton();
        Config.ConfigItem advance = new Config.ConfigItem("Advance", advanceButton, 0.4);
        {
            advanceButton.setTheme(BUTTON);
            themeManager.register(advanceButton);
            ImageView editIcon = new ImageView(ResourceManager.edit);
            editIcon.setFitWidth(UI_HEIGHT * 0.8);
            editIcon.setFitHeight(UI_HEIGHT * 0.8);
            advanceButton.getChildren().add(editIcon);
            advanceButton.setPosition(editIcon, AutoPane.AlignmentMode.CENTER, AutoPane.LocateMode.RELATIVE, 0.5, 0.5);
            advanceButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                if (event.getButton() == MouseButton.PRIMARY) {
                    if (advanceStage.isShowing()) advanceStage.toFront();
                    else advanceStage.show();
                }
            });
        }

        advance.setI18NKey("main.tts.gpt.label.advance");
        I18N.registerComponents(url, textLang, refAudioPath, promptLang, promptText, topK, topP, temperature, textSplitMethod, batchSize, batchThreshold, splitBucket, speedFactor, fragmentInterval, seed, mediaType, repetitionPenalty, sampleSteps, superSampling, advance);
        gptSoVITSBox.addConfigItem(url, textLang, refAudioPath, promptLang, promptText, speedFactor, fragmentInterval, topK, topP, temperature, advance);
        return gptSoVITSBox;
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
        ImageView horn = new ImageView(ResourceManager.horn);
        previewButton.getChildren().add(horn);
        previewButton.setPosition(horn, AutoPane.AlignmentMode.CENTER, AutoPane.LocateMode.RELATIVE, 0.5, 0.5);
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
