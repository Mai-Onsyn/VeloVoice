package mai_onsyn.VeloVoice2.FrameFactory;

import com.alibaba.fastjson.JSONObject;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import mai_onsyn.AnimeFX2.LanguageSwitchable;
import mai_onsyn.AnimeFX2.Module.*;
import mai_onsyn.AnimeFX2.Utls.*;
import mai_onsyn.AnimeFX2.layout.AXScrollPane;
import mai_onsyn.AnimeFX2.layout.AutoPane;
import mai_onsyn.AnimeFX2.layout.HDoubleSplitPane;
import mai_onsyn.VeloVoice2.App.Config;
import mai_onsyn.VeloVoice2.App.Resource;
import mai_onsyn.VeloVoice2.Audio.AudioPlayer;
import mai_onsyn.VeloVoice2.NetWork.Item.Source;
import mai_onsyn.VeloVoice2.NetWork.TTS.EdgeTTSVoice;
import mai_onsyn.VeloVoice2.NetWork.TTS.FixedEdgeTTSClient;
import mai_onsyn.VeloVoice2.Text.Sentence;
import mai_onsyn.VeloVoice2.Text.TTS;

import java.io.File;
import java.util.*;

import static mai_onsyn.VeloVoice2.App.Constants.*;
import static mai_onsyn.VeloVoice2.App.Runtime.*;
import static mai_onsyn.VeloVoice2.FrameFactory.LogFactory.logger;

public class MainFactory {

    private static final Label textAreaInfo = new Label();
    private static final AXTreeView<SimpleStringProperty> treeView = new AXTreeView<>(SimpleStringProperty::new, src -> new SimpleStringProperty(src.get()));
    private static final AXTextArea textArea = new AXTextArea();

    public static void drawMainFrame(AutoPane root) {
        HDoubleSplitPane textEditArea = getTextEditArea();
        AutoPane operationArea = getOperationArea();

        root.getChildren().addAll(textEditArea, operationArea);

        root.setPosition(textEditArea, false, 40, 520, 40, 40);
        root.setPosition(operationArea, false, 480, 40, 40, 40);
        root.flipRelativeMode(operationArea, AutoPane.Motion.LEFT);
    }

    private static HDoubleSplitPane getTextEditArea() {

        HDoubleSplitPane root = new HDoubleSplitPane(40, 0.35, 100, 100);
        AutoPane leftPane = root.getLeft();
        AutoPane rightPane = root.getRight();


        themeManager.register(treeView, textArea);
        Map<LanguageSwitchable, String> treeViewLanguageElements = treeView.getLanguageElements();
        for (Map.Entry<LanguageSwitchable, String> entry : treeViewLanguageElements.entrySet()) {
            languageManager.register(entry.getKey(), "frame.contextMenu." + entry.getValue());
        }
        Map<LanguageSwitchable, String> textAreaLanguageElements = textArea.getLanguageElements();
        for (Map.Entry<LanguageSwitchable, String> entry : textAreaLanguageElements.entrySet()) {
            languageManager.register(entry.getKey(), "frame.contextMenu." + entry.getValue());
        }


        textAreaInfo.setAlignment(Pos.CENTER);
        textArea.setPosition(textAreaInfo, false, 0, 0, 0, 0);
        textArea.getChildren().add(textAreaInfo);
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
                    disableTextArea("Unable to edit a directory");
                    textArea.clear();
                }
            }
            if (nv == null) {
                disableTextArea("Choose a file to edit");
                textArea.clear();
            }
        });

        disableTextArea("Choose a file to edit");

        //test
        {
            AXTreeItem folder1 = treeView.createFolderItem("folder1");
            AXTreeItem folder2 = treeView.createFolderItem("folder2");

            AXTreeItem file1 = treeView.createFileItem("file1", new SimpleStringProperty("Content of file 1"));
            AXTreeItem file2 = treeView.createFileItem("file2", new SimpleStringProperty("Content of file 2"));
            AXTreeItem file3 = treeView.createFileItem("file3", new SimpleStringProperty("Content of file 3"));
            AXTreeItem file4 = treeView.createFileItem("file4", new SimpleStringProperty("Content of file 4"));

            treeView.add(treeView.getRoot(), folder1);
            treeView.add(treeView.getRoot(), folder2);
            treeView.add(folder1, file1);
            treeView.add(folder1, file2);
            treeView.add(folder2, file3);
            treeView.add(folder2, file4);
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
        }


        //三配置面板
        ScrollPane scrollPane = new AXScrollPane();
        AutoPane switchRoot = new AutoPane();
        {
            scrollPane.setContent(switchRoot);
            scrollPane.setFitToWidth(true);

            Config.ConfigBox ttsArea = new Config.ConfigBox(UI_SPACING, UI_HEIGHT);
            {
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
                        voiceShortNameList.forEach(shortName -> langNameSet.add(shortName.substring(0, 2)));
                    }

                    Config.ConfigItem langItem = edgeTTSConfig.genChooseStringItem("SelectedLanguage", langNameSet.stream().toList());
                    Config.ConfigItem modelItem = edgeTTSConfig.genChooseStringItem("SelectedModel", voiceShortNameList);
                    Config.ConfigItem rateItem = edgeTTSConfig.genFloatSlidItem("VoiceRate", 0.05, 2.0, 0.05);
                    Config.ConfigItem volumeItem = edgeTTSConfig.genFloatSlidItem("VoiceVolume", 0.05, 2.0, 0.05);
                    Config.ConfigItem pitchItem = edgeTTSConfig.genFloatSlidItem("VoicePitch", 0.05, 1.5, 0.05);
                    Config.ConfigItem threadItem = edgeTTSConfig.genIntegerSlidItem("ThreadCount", 1, 4, 1);

                    languageManager.register(
                            langItem, "frame.main.item.label.edgeTTS.lang",
                            modelItem, "frame.main.item.label.edgeTTS.model",
                            rateItem, "frame.main.item.label.edgeTTS.rate",
                            volumeItem, "frame.main.item.label.edgeTTS.volume",
                            pitchItem, "frame.main.item.label.edgeTTS.pitch",
                            threadItem, "frame.main.item.label.edgeTTS.threadCount"
                    );

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

                            previewButton.setOnMouseClicked(e -> {
                                if (e.getButton() == MouseButton.PRIMARY) {
                                    Thread.ofVirtual().name("Preview-Thread").start(() -> {
                                        FixedEdgeTTSClient client = new FixedEdgeTTSClient();
                                        try {
                                            logger.info("Loading preview text...");
                                            client.connect();
                                            Sentence sentence = client.process(config.getString("PreviewText"));
                                            client.close();

                                            logger.info("Playing preview text...");
                                            AudioPlayer.play(sentence.getAudio());
                                        } catch (Exception ex) {
                                            logger.error("Error while loading preview text: ", ex.toString());
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
//                                if (Objects.equals(button.getTextLabel().getText().substring(0, 2), initLang)) {
//                                    langButtonGroup.selectButton(button);
//                                }
//                            });
//
//                            AXButtonGroup modelButtonGroup = modelChoiceBox.getButtonGroup();
//                            modelButtonGroup.getButtonList().forEach(button -> {
//                                if (Objects.equals(button.getTextLabel().getText(), edgeTTSConfig.getString("SelectedModel"))) {
//                                    modelButtonGroup.selectButton(button);
//                                }
//                            });


                            //根据语言显示模型
                            showEdgeTTSModel(modelChoiceBox, initLang);
                            langButtonGroup.setOnSelectedChanged((o, ov, nv) -> {
                                showEdgeTTSModel(modelChoiceBox, nv.getTextLabel().getText());
                            });
                        }
                    }

                    //EdgeTTS配置的应用
                    {
                        ((AXChoiceBox) modelItem.getContent()).getButtonGroup().setOnSelectedChanged((o, ov, nv) -> FixedEdgeTTSClient.setVoice(nv.getTextLabel().getText()));

                        ((AXFloatTextField) rateItem.getContent().getChildren().get(1)).valueProperty().addListener((o, ov, nv) -> FixedEdgeTTSClient.setVoiceRate(nv.doubleValue()));

                        ((AXFloatTextField) volumeItem.getContent().getChildren().get(1)).valueProperty().addListener((o, ov, nv) -> FixedEdgeTTSClient.setVoiceVolume(nv.doubleValue()));

                        ((AXFloatTextField) pitchItem.getContent().getChildren().get(1)).valueProperty().addListener((o, ov, nv) -> FixedEdgeTTSClient.setVoicePitch(nv.doubleValue()));
                    }

                    edgeTTSBox.addConfigItem(langItem, modelItem, rateItem, volumeItem, pitchItem, threadItem);
                }

                Config.ConfigItem audioFolderItem = config.genInputStringItem("AudioSaveFolder", "frame.main.textField.audioFolder");
                //languageManager.register("frame.main.item.label.edgeTTS.audioFolder", audioFolderItem);
                Map<LanguageSwitchable, String> languageElements = ((AXTextField) audioFolderItem.getContent()).getLanguageElements();
                for (Map.Entry<LanguageSwitchable, String> entry : languageElements.entrySet()) {
                    languageManager.register(entry.getKey(), "frame.contextMenu." + entry.getValue());
                }

                ttsArea.getChildren().addAll(edgeTTSBox);

                ttsArea.addConfigItem(audioFolderItem);
            }

            Config.ConfigBox afterProcessArea = new Config.ConfigBox(UI_SPACING, UI_HEIGHT);
            {
                Config.ConfigItem afterProcessItem = new Config.ConfigItem("afterProcessItem", new AutoPane(), 0.4);

                afterProcessArea.addConfigItem(afterProcessItem);
            }

            Config.ConfigBox textProcessArea = new Config.ConfigBox(UI_SPACING, UI_HEIGHT);
            {
                Config.ConfigBox loadBox = new Config.ConfigBox(UI_SPACING, UI_HEIGHT);
                {
                    AXLangLabel title = loadBox.addTitle("Load");

                    List<String> loadList = new ArrayList<>();
                    {
                        for (Map.Entry<String, Source> entry : sources.entrySet()) {
                            loadList.add(entry.getKey());
                        }
                    }
                    Config.ConfigItem sourceItem = textConfig.genChooseStringItem("LoadSource", loadList);

                    Config.ConfigBox sourceConfigBox = new Config.ConfigBox(UI_SPACING, UI_HEIGHT);
                    {

                    }

                    Config.ConfigItem uriItem = textConfig.genInputStringItem("LoadUri", "frame.main.textField.textLoadUri");
                    languageManager.register(uriItem, "frame.main.item.label.load.textLoadUri");
                    Map<LanguageSwitchable, String> languageElements = ((AXTextField) uriItem.getContent()).getLanguageElements();
                    for (Map.Entry<LanguageSwitchable, String> entry : languageElements.entrySet()) {
                        languageManager.register(entry.getKey(), "frame.contextMenu." + entry.getValue());
                    }

                    AXButton startButton = new AXButton("Load");
                    startButton.setMaxSize(100, UI_SPACING + UI_HEIGHT);
                    startButton.setMinSize(100, UI_SPACING + UI_HEIGHT);
                    loadBox.setAlignment(Pos.TOP_RIGHT);

                    startButton.setOnMouseClicked(e -> {
                        if (e.getButton() == MouseButton.PRIMARY) {
                            Source source = sources.get(textConfig.getString("LoadSource"));
                            source.process(textConfig.getString("LoadUri"), treeView.getRoot());
                        }
                    });

                    languageManager.register(title, "frame.main.item.label.textProcess.load.title");
                    loadBox.addConfigItem(sourceItem, uriItem);
                    loadBox.getChildren().addAll(sourceConfigBox, startButton);
                }

                textProcessArea.getChildren().add(loadBox);
            }

            switchGroup.setOnSelectedChangedDatable((o, ov, nv) -> {
                switch (nv.getValue()) {
                    case "TTS" -> {
                        ttsArea.setVisible(true);
                        afterProcessArea.setVisible(false);
                        textProcessArea.setVisible(false);
                    }
                    case "AfterProcess" -> {
                        ttsArea.setVisible(false);
                        afterProcessArea.setVisible(true);
                        textProcessArea.setVisible(false);
                    }
                    case "TextProcess" -> {
                        ttsArea.setVisible(false);
                        afterProcessArea.setVisible(false);
                        textProcessArea.setVisible(true);
                    }
                }
            });
            afterProcessArea.setVisible(false);
            textProcessArea.setVisible(false);

            switchRoot.getChildren().addAll(ttsArea, afterProcessArea, textProcessArea);
            switchRoot.setPosition(ttsArea, false, 0, 0, 0, 0);
            switchRoot.setPosition(afterProcessArea, false, 0, 0, 0, 0);
            switchRoot.setPosition(textProcessArea, false, 0, 0, 0, 0);
        }


        //控制开始,日志,进度的面板
        AutoPane ctrlArea = new AutoPane();
        {
            AXButton logButton = new AXButton("Log");
            Label logTextLabel = logButton.getTextLabel();
            logTextLabel.setWrapText(true);

            AXProgressBar totalProgressBar = new AXProgressBar();
            AXProgressBar currentProgressBar = new AXProgressBar();
            totalProgressBar.setVisible(false);
            currentProgressBar.setVisible(false);

            logButton.getChildren().addAll(totalProgressBar, currentProgressBar);
            logButton.setPosition(totalProgressBar, true, 0, 0, 0, 0.65);
            logButton.setPosition(currentProgressBar, true, 0, 0, 0.65, 0);

            AXButton startButton = new AXButton("Start");

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

                            logger.info("All tasks have been completed");

                        } catch (InterruptedException ex) {
                            logger.info("Task interrupted by user");
                        } finally {
                            Platform.runLater(() -> isRunning.set(false));
                        }
                    });
                }
            });


            isRunning.addListener((o, ov, nv) -> {
                if (nv) {
                    startButton.setText("Stop");
                    logTextLabel.setVisible(false);

                    totalProgressBar.setVisible(true);
                    currentProgressBar.setVisible(true);
                    ctrlArea.setPosition(logButton, false, 0, UI_SPACING + 100, 0, 0);
                    ctrlArea.flipRelativeMode(logButton, AutoPane.Motion.LEFT, false);
                }
                else {
                    startButton.setText("Start");
                    logTextLabel.setVisible(true);

                    totalProgressBar.setVisible(false);
                    currentProgressBar.setVisible(false);
                    ctrlArea.setPosition(logButton, false, UI_SPACING + 140, UI_SPACING + 100, 0, 0);
                    ctrlArea.flipRelativeMode(logButton, AutoPane.Motion.LEFT, true);
                }
                ctrlArea.flush();
            });


            //Test
//            Thread.ofVirtual().start(() -> {
//                while (true) {
//                    Platform.runLater(() -> {
//                        isRunning.set(!isRunning.get());
//                        totalProgressBar.setProgress(0.5);
//                        totalProgressBar.setProgress(0.6);
//                    });
//                    try {
//                        Thread.sleep(3000);
//                    } catch (InterruptedException _) {}
//                }
//            });


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
            String name = button.getTextLabel().getText();
            if (name.startsWith(lang) && !box.containsItem(button)) box.showItem(button);
            else if (!name.startsWith(lang)) box.removeItem(button);
        });
    }

}
