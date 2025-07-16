package mai_onsyn.VeloVoice.FrameFactory;

import com.alibaba.fastjson.JSONObject;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import mai_onsyn.AnimeFX.I18N;
import mai_onsyn.AnimeFX.Module.*;
import mai_onsyn.AnimeFX.Utls.*;
import mai_onsyn.AnimeFX.layout.AXScrollPane;
import mai_onsyn.AnimeFX.layout.AutoPane;
import mai_onsyn.AnimeFX.layout.HDoubleSplitPane;
import mai_onsyn.VeloVoice.App.Config;
import mai_onsyn.VeloVoice.App.Resource;
import mai_onsyn.VeloVoice.Audio.AudioPlayer;
import mai_onsyn.VeloVoice.NetWork.TTS.EdgeTTSVoice;
import mai_onsyn.VeloVoice.NetWork.TTS.FixedEdgeTTSClient;
import mai_onsyn.VeloVoice.Text.Sentence;
import mai_onsyn.VeloVoice.Text.TTS;
import mai_onsyn.VeloVoice.Text.TextUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.*;

import static mai_onsyn.VeloVoice.App.Constants.*;
import static mai_onsyn.VeloVoice.App.Runtime.*;
import static mai_onsyn.VeloVoice.FrameFactory.FrameThemes.*;
import static mai_onsyn.VeloVoice.FrameFactory.LogFactory.logStage;
import static mai_onsyn.VeloVoice.FrameFactory.SettingsFactory.settingsStage;

public class MainFactory {

    static final Label textAreaInfo = new Label();
    static final AXTreeView<SimpleStringProperty> treeView = new AXTreeView<>(SimpleStringProperty::new, src -> new SimpleStringProperty(src.get()));
    static final AXTextArea textArea = new AXTextArea();
    static {
        treeView.setTheme(TREE_VIEW);
        textArea.setTheme(TEXT_AREA);
        themeManager.register(treeView, textArea);
    }

    private static final Logger log = LogManager.getLogger(MainFactory.class);

    public static void drawMainFrame(AutoPane root) {
        HDoubleSplitPane textEditArea = getTextEditArea();
        AutoPane operationArea = getOperationArea();

        root.getChildren().addAll(textEditArea, operationArea);

        root.setPosition(textEditArea, false, 40, 520, 40, 40);
        root.setPosition(operationArea, false, 480, 40, 40, 40);
        root.flipRelativeMode(operationArea, AutoPane.Motion.LEFT);

        drawConfigButton(root);
        LogFactory.drawLogFrame();
    }

    private static HDoubleSplitPane getTextEditArea() {

        HDoubleSplitPane root = new HDoubleSplitPane(40, 0.35, 100, 100);
        AutoPane leftPane = root.getLeft();
        AutoPane rightPane = root.getRight();


        themeManager.register(treeView, textArea);
        treeView.setChildrenI18NKeys(I18nKeyMaps.CONTEXT);
        textArea.setChildrenI18NKeys(I18nKeyMaps.CONTEXT);
        I18N.registerComponents(treeView, textArea);


        textAreaInfo.setAlignment(Pos.CENTER);
        textAreaInfo.setTextFill(Color.gray(0.5));
        textArea.setPosition(textAreaInfo, false, 0, 0, 0, 0);
        textArea.getChildren().add(textAreaInfo);
        AXDatableButtonGroup<AXTreeItem> treeViewGroup = getTreeViewGroup();

        disableTextArea(I18N.getCurrentValue("main.prompt.text_area.no_selected"));
        I18N.addOnChangedAction(() -> {
            if (treeViewGroup.getSelectedButton() != null) {
                if (!(treeViewGroup.getData(treeViewGroup.getSelectedButton()) instanceof AXDataTreeItem<?>)) disableTextArea(I18N.getCurrentValue("main.prompt.text_area.directory_selected"));
            } else {
                disableTextArea(I18N.getCurrentValue("main.prompt.text_area.no_selected"));
            }
        });

        if (firstLaunch) {
            AXTreeItem main = treeView.createFolderItem("Fastly Getting Start");
            AXTreeItem english = treeView.createFileItem("English", new SimpleStringProperty("""
                    Welcome to VeloVoice!
                    
                        VeloVoice is a text-to-speech (TTS) software designed for long-form novels. With it, you can easily load, edit, and convert your text into speech. To get started, navigate to the "Text Processing" section on the right and select "Load" to import your novel. Alternatively, you can manually edit the text you’d like to convert: simply right-click on a folder (or the root directory) in the left panel, select "Create File," and edit the content—the TTS output will always reflect your latest changes!
                    
                        Once you’ve configured everything, hit the "Start" button at the bottom right to convert your text into playable audio. Sit back, relax, and enjoy a coffee—or anything else! VeloVoice runs efficiently in the background, barely touching your system resources. Want to monitor progress in detail? Click the progress bar next to the "Stop" button to open the log window, where you’ll find real-time updates.
                    
                        Thank you for choosing VeloVoice! If you have any questions, feel free to submit an issue on GitHub:
                        https://github.com/Mai-Onsyn/VeloVoice
                        Or message the creator on Bilibili:
                        https://space.bilibili.com/544189344
                    """));
            AXTreeItem chinese = treeView.createFileItem("简体中文", new SimpleStringProperty("""
                    欢迎使用VeloVoice！
                    
                        这是一个适用于长文本小说的文本转语音软件，你可以使用它加载、编辑、和转换文本。在右侧的“文本处理”中选择“加载”以将你的小说加载到该软件中。当然，你也可以手动编辑你需要转换的语音文本：右键左侧的文件夹或根，选择创建文件，然后选中你所创建的文件进行编辑，之后文本转语音的内容将会和你最后一次编辑的内容保持一致！
                    
                        一切配置决定完成后，你可以点击右下角的“开始”按钮，等待程序自动将你左侧配置的所有文本转换为可以播放的语音。在此期间，你可以去做你想做的任何事，因为该程序几乎不会占用你的计算机资源！如果你想在转换时查看详细的进度，可以点击“结束”按钮旁边的进度条来打开日志窗口，日志窗口中有非常详细的任务进度。
                    
                        最后，感谢你使用VeloVoice！如果你有任何问题，欢迎在Github上提交issue！
                        软件仓库：https://github.com/Mai-Onsyn/VeloVoice
                        或者在B站私信作者：https://space.bilibili.com/544189344
                    """));

            treeView.add(treeView.getRoot(), main);
            treeView.add(main, english);
            treeView.add(main, chinese);
        }

        leftPane.getChildren().add(treeView);
        rightPane.getChildren().add(textArea);

        leftPane.setPosition(treeView, false, 0, 0, 0, 0);
        rightPane.setPosition(textArea, false, 0, 0, 0, 0);
        return root;
    }

    private static AutoPane getOperationArea() {
        AutoPane root = new AutoPane();

        //配置面板切换条
        AutoPane menuBar = new AutoPane();
        AXDatableButtonGroup<String> switchGroup = new AXDatableButtonGroup<>();
        {
            AXButton ttsSwitchButton = new AXButton("TTS");
            AXButton afterProcessSwitchButton = new AXButton("AfterProcess");
            AXButton textProcessSwitchButton = new AXButton("TextProcess");
            switchGroup.register(ttsSwitchButton, "TTS");
            switchGroup.register(afterProcessSwitchButton, "AfterProcess");
            switchGroup.register(textProcessSwitchButton, "TextProcess");
            switchGroup.selectButton(ttsSwitchButton);
            menuBar.getChildren().addAll(ttsSwitchButton, afterProcessSwitchButton, textProcessSwitchButton);
            menuBar.setPosition(ttsSwitchButton, true, 0, 0.6666666, 0, 0);
            menuBar.setPosition(afterProcessSwitchButton, true, 0.3333333, 0.3333333, 0, 0);
            menuBar.setPosition(textProcessSwitchButton, true, 0.6666666, 0, 0, 0);

            ttsSwitchButton.setI18NKey("main.general.label.tts");
            afterProcessSwitchButton.setI18NKey("main.general.label.after_process");
            textProcessSwitchButton.setI18NKey("main.general.label.text_process");
            I18N.registerComponents(ttsSwitchButton, afterProcessSwitchButton, textProcessSwitchButton);

            switchGroup.setSelectedStyle(TRANSPARENT_SELECTED_BUTTON);
            switchGroup.setFreeStyle(TRANSPARENT_BUTTON);
            themeManager.register(switchGroup);

        }


        //三配置面板
        ScrollPane scrollPane = new AXScrollPane();
        AutoPane switchRoot = new AutoPane();
        {
            scrollPane.setContent(switchRoot);
            scrollPane.setFitToWidth(true);

            //TTS配置面板
            Config.ConfigBox ttsArea = new Config.ConfigBox(UI_SPACING, UI_HEIGHT);
            {
                //Edge TTS
                Config.ConfigBox edgeTTSBox = new Config.ConfigBox(UI_SPACING, UI_HEIGHT);
                {
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
                        AXChoiceBox modelChoiceBox = (AXChoiceBox) modelItem.getContent();
                        AXChoiceBox langChoiceBox = (AXChoiceBox) langItem.getContent();
                        AXButton previewButton = new AXButton();
                        {
                            ImageView horn = new ImageView(Resource.horn);
                            previewButton.getChildren().add(horn);
                            previewButton.setPosition(horn, AutoPane.AlignmentMode.CENTER, AutoPane.LocateMode.RELATIVE, 0.5, 0.5);
                            horn.setFitWidth(UI_HEIGHT * 0.8);
                            horn.setFitHeight(UI_HEIGHT * 0.8);
                            previewButton.setTheme(BUTTON);
                            themeManager.register(previewButton);

                            previewButton.setOnMouseClicked(e -> {
                                if (e.getButton() == MouseButton.PRIMARY) {
                                    Thread.ofVirtual().name("Preview-Thread").start(() -> {
                                        FixedEdgeTTSClient client = new FixedEdgeTTSClient();
                                        try {
                                            log.info(I18N.getCurrentValue("log.main_factory.info.load_preview"));
                                            client.connect();
                                            Sentence sentence = client.process(config.getString("PreviewText"));
                                            client.close();

                                            AudioPlayer.play(sentence.audioByteArray());
                                        } catch (Exception ex) {
                                            log.error(I18N.getCurrentValue("log.main_factory.error.load_preview_failed"), ex.toString());
                                            throw new RuntimeException(ex);
                                        }
                                    });
                                }
                            });

                            modelItem.setPosition(modelChoiceBox, true, false, false, false, 0.4, UI_HEIGHT * 2, 0, 0);

                            modelItem.getChildren().add(previewButton);
                            modelItem.setPosition(previewButton, false, UI_HEIGHT * 1.4, 0, 0, 0);
                            modelItem.flipRelativeMode(previewButton, AutoPane.Motion.LEFT);
                        }

                        //手动选择当前设置的模型
                        {
                            String initLang = edgeTTSConfig.getString("SelectedLanguage");
                            AXButtonGroup langButtonGroup = langChoiceBox.getButtonGroup();
//                            langButtonGroup.getButtonList().forEach(button -> {
//                                if (Objects.equals(button.getText().substring(0, 2), initLang)) {
//                                    langButtonGroup.selectButton(button);
//                                }
//                            });
//
//                            AXButtonGroup modelButtonGroup = modelChoiceBox.getButtonGroup();
//                            modelButtonGroup.getButtonList().forEach(button -> {
//                                if (Objects.equals(button.getText(), edgeTTSConfig.getString("SelectedModel"))) {
//                                    modelButtonGroup.selectButton(button);
//                                }
//                            });


                            //根据语言显示模型
                            showEdgeTTSModel(modelChoiceBox, LANG_NAME_TO_HEADCODE_MAPPING.get(initLang));
                            langButtonGroup.addOnSelectChangedListener((o, ov, nv) -> {
                                showEdgeTTSModel(modelChoiceBox, LANG_NAME_TO_HEADCODE_MAPPING.get(nv.getText()));
                            });
                        }
                    }

                    //UI数据应用到EdgeTTS配置项
                    {
                        ((AXChoiceBox) modelItem.getContent()).getButtonGroup().addOnSelectChangedListener((o, ov, nv) -> FixedEdgeTTSClient.setVoice(nv.getText()));

                        ((AXFloatTextField) rateItem.getContent().getChildren().get(1)).valueProperty().addListener((o, ov, nv) -> FixedEdgeTTSClient.setVoiceRate(nv.doubleValue()));

                        ((AXFloatTextField) volumeItem.getContent().getChildren().get(1)).valueProperty().addListener((o, ov, nv) -> FixedEdgeTTSClient.setVoiceVolume(nv.doubleValue()));

                        ((AXFloatTextField) pitchItem.getContent().getChildren().get(1)).valueProperty().addListener((o, ov, nv) -> FixedEdgeTTSClient.setVoicePitch(nv.doubleValue()));
                    }

                    edgeTTSBox.addConfigItem(langItem, modelItem, rateItem, volumeItem, pitchItem, threadItem);
                }

                Config.ConfigItem audioFolderItem = config.genInputStringItem("AudioSaveFolder", "main.tts.general.input.audio_folder");
                audioFolderItem.setI18NKey("main.tts.general.label.audio_folder");
                ((AXTextField) audioFolderItem.getContent()).setChildrenI18NKeys(I18nKeyMaps.CONTEXT);
                I18N.registerComponent(audioFolderItem);

                ttsArea.getChildren().addAll(edgeTTSBox);

                ttsArea.addConfigItem(audioFolderItem);
            }

            //语音处理相关
            Config.ConfigBox afterProcessArea = AfterProcessFactory.mkAfterProcessArea();

            //文本处理相关
            Config.ConfigBox textProcessArea = TextProcessFactory.mkTextProcessArea();

            switchGroup.setOnSelectedChangedDatable((o, ov, nv) -> {
                switch (nv.getValue()) {
                    case "TTS" -> {
                        switchRoot.getChildren().removeLast();
                        switchRoot.getChildren().add(ttsArea);
                    }
                    case "AfterProcess" -> {
                        switchRoot.getChildren().removeLast();
                        switchRoot.getChildren().add(afterProcessArea);
                    }
                    case "TextProcess" -> {
                        switchRoot.getChildren().removeLast();
                        switchRoot.getChildren().add(textProcessArea);
                    }
                }
            });

            switchRoot.getChildren().add(ttsArea);
            switchRoot.widthProperty().addListener((o, ov, nv) -> {
                ttsArea.setPrefWidth(nv.doubleValue());
                afterProcessArea.setPrefWidth(nv.doubleValue());
                textProcessArea.setPrefWidth(nv.doubleValue());
            });
        }


        //控制开始,日志,进度的面板
        AutoPane ctrlArea = new AutoPane();
        {
            AXButton logButton = new AXButton();
            ImageView logIcon = new ImageView(Resource.list); {
                logIcon.setFitWidth(UI_HEIGHT * 0.8);
                logIcon.setFitHeight(UI_HEIGHT * 0.8);
                logButton.getChildren().add(logIcon);
                logButton.setPosition(logIcon, AutoPane.AlignmentMode.CENTER, AutoPane.LocateMode.RELATIVE, 0.5, 0.5);
            }
            logButton.setTheme(BUTTON);

            AXProgressBar totalProgressBar = new AXProgressBar();
            AXProgressBar currentProgressBar = new AXProgressBar();
            totalProgressBar.setVisible(false);
            currentProgressBar.setVisible(false);

            logButton.getChildren().addAll(totalProgressBar, currentProgressBar);
            logButton.setPosition(totalProgressBar, true, 0, 0, 0, 0.65);
            logButton.setPosition(currentProgressBar, true, 0, 0, 0.65, 0);

            logButton.setTheme(BUTTON);
            totalProgressBar.setTheme(WIDE_PROGRESS_BAR);
            currentProgressBar.setTheme(WIDE_PROGRESS_BAR);
            themeManager.register(logButton, totalProgressBar, currentProgressBar);

            LogFactory.totalProgressBar.progressProperty().addListener((o, ov, nv) -> {
                totalProgressBar.setProgress(nv.doubleValue());
            });
            LogFactory.currentProgressBar.progressProperty().addListener((o, ov, nv) -> {
                currentProgressBar.setProgress(nv.doubleValue());
            });

            logButton.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
                if (logStage.isShowing()) logStage.toFront();
                else logStage.show();
                LogFactory.root.flush();
            });

            AXButton startButton = new AXButton("Start");
            startButton.setTheme(BUTTON);
            themeManager.register(startButton);
            startButton.setI18NKey("main.general.button.start");
            I18N.registerComponent(startButton);

            startButton.setOnMouseClicked(e -> {
                if (isRunning.get()) {
                    isRunning.set(false);

                    EDGE_TTS_THREAD.interrupt();
                }
                else {
                    isRunning.set(true);

                    EDGE_TTS_THREAD = Thread.ofVirtual().name("EdgeTTS-Main").start(() -> {
                        try {
                            TTS.start(treeView.getRoot(), new File(config.getString("AudioSaveFolder")));

                            log.info(I18N.getCurrentValue("log.main_factory.info.tts_all_finished"), TextUtil.formatMillisToTime(System.currentTimeMillis() - totalStartTime));

                        } catch (InterruptedException ex) {
                            log.info(I18N.getCurrentValue("log.main_factory.info.tts_interrupted"));
                        } catch (Exception er) {
                            log.error(I18N.getCurrentValue("log.main_factory.error.tts_execute_failed"), er.getMessage());
                        }
                        finally {
                            Platform.runLater(() -> isRunning.set(false));
                            LogFactory.totalInfo.setText(I18N.getCurrentValue("log.progress.total") + ": 0% [0/0]");
                            LogFactory.currentInfo.setText(I18N.getCurrentValue("log.progress.current") + ": 0% [0/0] (" + I18N.getCurrentValue("log.progress.initializing") + ")");
                            currentFileName = I18N.getCurrentValue("log.progress.initializing");
                        }
                    });
                }
            });

            isRunning.addListener((o, ov, nv) -> {
                if (nv) {
                    startButton.setText(I18N.getCurrentValue("main.general.button.stop"));
                    logIcon.setVisible(false);

                    totalProgressBar.setVisible(true);
                    currentProgressBar.setVisible(true);
                    ctrlArea.setPosition(logButton, false, 0, UI_SPACING + 100, 0, 0);
                    ctrlArea.flipRelativeMode(logButton, AutoPane.Motion.LEFT, false);

                    logButton.setTheme(FrameThemes.TRANSPARENT_BUTTON);
                    logButton.update();
                }
                else {
                    startButton.setText(I18N.getCurrentValue("main.general.button.start"));
                    logIcon.setVisible(true);

                    totalProgressBar.setVisible(false);
                    currentProgressBar.setVisible(false);
                    ctrlArea.setPosition(logButton, false, UI_SPACING + 140, UI_SPACING + 100, 0, 0);
                    ctrlArea.flipRelativeMode(logButton, AutoPane.Motion.LEFT, true);

                    LogFactory.totalProgressBar.setProgress(0, 1);
                    LogFactory.currentProgressBar.setProgress(0, 1);

                    logButton.setTheme(BUTTON);
                    logButton.update();
                }
                ctrlArea.flush();
            });

            ctrlArea.getChildren().addAll(logButton, startButton);
            ctrlArea.setPosition(startButton, false, 100, 0, 0, 0);
            ctrlArea.setPosition(logButton, false, UI_SPACING + 140, UI_SPACING + 100, 0, 0);
            ctrlArea.flipRelativeMode(startButton, AutoPane.Motion.LEFT);
            ctrlArea.flipRelativeMode(logButton, AutoPane.Motion.LEFT, true);
        }


        root.getChildren().addAll(menuBar, scrollPane, ctrlArea);

        root.setPosition(menuBar, false, 0, 0, 0, UI_HEIGHT);
        root.flipRelativeMode(menuBar, AutoPane.Motion.BOTTOM);
        root.setPosition(scrollPane, false, 0, 0, UI_HEIGHT + UI_SPACING, 60 + UI_SPACING);
        root.setPosition(ctrlArea, false, 0, 0, 50, 0);
        root.flipRelativeMode(ctrlArea, AutoPane.Motion.TOP);

        return root;
    }

    private static void drawConfigButton(AutoPane root) {
        AXButton configButton = new AXButton();
        configButton.setTheme(FrameThemes.TRANSPARENT_BUTTON);
        themeManager.register(configButton);

        ImageView settingIcon = new ImageView(Resource.setting);
        configButton.getChildren().add(settingIcon);
        configButton.setPosition(settingIcon, AutoPane.AlignmentMode.CENTER, AutoPane.LocateMode.RELATIVE, 0.5, 0.5);
        settingIcon.setFitWidth(UI_HEIGHT);
        settingIcon.setFitHeight(UI_HEIGHT);

        root.getChildren().add(configButton);
        root.setPosition(configButton, false, 35, 0, 0, 35);
        root.flipRelativeMode(configButton, AutoPane.Motion.LEFT);
        root.flipRelativeMode(configButton, AutoPane.Motion.BOTTOM);

        settingsStage.getTitle();//init static method
        configButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                if (!settingsStage.isShowing()) settingsStage.show();
                else if (settingsStage.isIconified()) settingsStage.setIconified(false);
                else settingsStage.toFront();
            }
        });
    }

    private static AXDatableButtonGroup<AXTreeItem> getTreeViewGroup() {
        AXDatableButtonGroup<AXTreeItem> treeViewGroup = treeView.getGroup();


        treeViewGroup.setOnSelectedChangedDatable((o, ov, nv) -> {
            if (ov != null) {
                if (ov.getValue() instanceof AXDataTreeItem<?> item) {
                    if (item.getData() instanceof SimpleStringProperty property) {
                        textArea.textProperty().unbindBidirectional(property);
                    }
                }
            }
            if (nv != null) {
                if (nv.getValue() instanceof AXDataTreeItem<?> item) {
                    enableTextArea();
                    if (item.getData() instanceof SimpleStringProperty property) {
                        textArea.setText(property.get());
                        textArea.textProperty().bindBidirectional(property);
                    }
                }
                else {
                    disableTextArea(I18N.getCurrentValue("main.prompt.text_area.directory_selected"));
                    textArea.clear();
                }
            }
            if (nv == null) {
                disableTextArea(I18N.getCurrentValue("main.prompt.text_area.no_selected"));
                textArea.clear();
            }
        });
        return treeViewGroup;
    }
    private static void disableTextArea(String reason) {
        textArea.setDisable(true);
        textAreaInfo.setText(reason);
        textAreaInfo.setVisible(true);
    }
    private static void enableTextArea() {
        textArea.setDisable(false);
        textAreaInfo.setVisible(false);
    }
    private static void showEdgeTTSModel(AXChoiceBox box, String lang) {
        box.getButtonGroup().getButtonList().forEach(button -> {
            String name = button.getText();
            if (name.startsWith(lang) && !box.containsItem(button)) box.showItem(button);
            else if (!name.startsWith(lang)) box.removeItem(button);
        });
    }


    public static AutoPane mkTitleBar(String namespace) {
        AXLangLabel langLabel = new AXLangLabel();
        langLabel.setI18NKey(namespace);
        I18N.registerComponent(langLabel);
        langLabel.setAlignment(Pos.CENTER);
        langLabel.setTheme(TITLE_LABEL);
        themeManager.register(langLabel);

        AutoPane autoPane = new AutoPane();
        autoPane.getChildren().add(langLabel);
        autoPane.setMaxHeight(UI_HEIGHT);
        autoPane.setMinHeight(UI_HEIGHT);
        autoPane.setPosition(langLabel, false, 0, 0, 0, 0);
        return autoPane;
    }
}
