package mai_onsyn.VeloVoice2.FrameFactory;

import com.alibaba.fastjson.JSONObject;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import mai_onsyn.AnimeFX2.I18N;
import mai_onsyn.AnimeFX2.Module.*;
import mai_onsyn.AnimeFX2.Utls.*;
import mai_onsyn.AnimeFX2.layout.AXScrollPane;
import mai_onsyn.AnimeFX2.layout.AutoPane;
import mai_onsyn.AnimeFX2.layout.HDoubleSplitPane;
import mai_onsyn.VeloVoice2.App.Config;
import mai_onsyn.VeloVoice2.App.Resource;
import mai_onsyn.VeloVoice2.Audio.AudioPlayer;
import mai_onsyn.VeloVoice2.NetWork.TTS.EdgeTTSVoice;
import mai_onsyn.VeloVoice2.NetWork.TTS.FixedEdgeTTSClient;
import mai_onsyn.VeloVoice2.Text.Sentence;
import mai_onsyn.VeloVoice2.Text.TTS;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.*;

import static mai_onsyn.VeloVoice2.App.Constants.*;
import static mai_onsyn.VeloVoice2.App.Runtime.*;
import static mai_onsyn.VeloVoice2.FrameFactory.LogFactory.logStage;

public class MainFactory {

    static final Label textAreaInfo = new Label();
    static final AXTreeView<SimpleStringProperty> treeView = new AXTreeView<>(SimpleStringProperty::new, src -> new SimpleStringProperty(src.get()));
    static final AXTextArea textArea = new AXTextArea();

    private static final Logger log = LogManager.getLogger(MainFactory.class);

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
        treeView.setChildrenI18NKeys(I18nKeyMaps.CONTEXT_MENU);
        textArea.setChildrenI18NKeys(I18nKeyMaps.CONTEXT_MENU);
        I18N.registerComponents(treeView, textArea);


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
            AXTreeItem file4 = treeView.createFileItem("file4", new SimpleStringProperty(
                    """
                            晴朗的五月天空之下，我就这么仰躺在草原上，眼看着将要溺死。
                                                        
                                有个状似浮游生物的东西，以蓝天为背景生龙活虎地跳动着。记得曾在哪本书上看过，那其实是眼球里的白血球。微风轻拂过我仰面朝天的脸庞，风中夹带着一股类似鱼腥味的刺鼻气味。我不确定周围是否有鱼，因为打从我进入〈里侧〉以来，至今未曾见过任何一条鱼。
                                                        
                                我现在仰躺于长得又高又茂盛的草丛里。由于水已淹过草根，因此我的背部浸泡在水里，模样有如半身浴。不对，我说错了，不该这么形容，真要说来是更接近超级大众澡堂里的「躺浴」。但因为水深二十公分多，若是我没努力把脸探出水面，水便会直接灌进口鼻里。这世上哪会有这种躺浴，假如当真存在，根本就是某种水刑，如假包换的死亡躺浴。
                                                        
                                实际上，我的确是一步一步地朝死亡靠拢。不论是我身上的UNIQLO刷毛外套和迷彩裤，都因为吸满水而沉重无比。我身陷如此状况……已过了几分钟？由于目前我没办法确认手表，因此不知道经过了多少时间，但我快无法继续把脸探出水面了。总觉得脖子酸痛得快要抽筋了，腹肌从刚才起也不停痉挛。说到底身体根本使不上力。感觉类似在梦中想要往前跑，但双脚却软趴趴地使不上力。打从我瞥见那个<、、>之后，四肢就一直呈现麻痹的状态。
                                                        
                                没想到居然会陷入这种情况——我太大意了。当初因为发现了这个世界<、、、、>，兴高采烈地跑来这里探险，结果却碰上这种狠角色，导致自己就快要溺毙了。
                                                        
                                如果我死在这里会发生什么事？在表世界中会被当成二十岁女大学生神秘失踪事件而上新闻吗？呜哇～总觉得会被人乱写一些有的没的，感觉真讨厌。是我对不起你，妈妈。
                                                        
                                ……不对，就算我忽然失踪，老实说应该也没有谁会特别在意。毕竟我没有朋友，而会为此伤脑筋的人，大不了就是发现我还没缴学费的校方人员，以及注意到我没有准时缴纳助学贷款的金融单位罢了——
                                                        
                                一想到这里，总觉得心情越来越糟。
                                                      
                                """
            ));

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

                    langItem.setI18NKey("frame.main.item.label.edge_tts.lang");
                    modelItem.setI18NKey("frame.main.item.label.edge_tts.model");
                    rateItem.setI18NKey("frame.main.item.label.edge_tts.rate");
                    volumeItem.setI18NKey("frame.main.item.label.edge_tts.volume");
                    pitchItem.setI18NKey("frame.main.item.label.edge_tts.pitch");
                    threadItem.setI18NKey("frame.main.item.label.edge_tts.thread_count");

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

                            previewButton.setOnMouseClicked(e -> {
                                if (e.getButton() == MouseButton.PRIMARY) {
                                    Thread.ofVirtual().name("Preview-Thread").start(() -> {
                                        FixedEdgeTTSClient client = new FixedEdgeTTSClient();
                                        try {
                                            log.info("Loading preview text...");
                                            client.connect();
                                            Sentence sentence = client.process(config.getString("PreviewText"));
                                            client.close();

                                            log.info("Playing preview text...");
                                            AudioPlayer.play(sentence.audioByteArray());
                                        } catch (Exception ex) {
                                            log.error("Error while loading preview text: " + ex.toString());
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

                Config.ConfigItem audioFolderItem = config.genInputStringItem("AudioSaveFolder", "frame.main.text_field.audio_folder");
                audioFolderItem.setI18NKey("frame.main.item.label.audio_folder");
                ((AXTextField) audioFolderItem.getContent()).setChildrenI18NKeys(I18nKeyMaps.CONTEXT_MENU);
                I18N.registerComponent(audioFolderItem);

                ttsArea.getChildren().addAll(edgeTTSBox);

                ttsArea.addConfigItem(audioFolderItem);
            }

            //语音处理相关
            Config.ConfigBox afterProcessArea = AfterProcessFactory.mkAfterProcessArea();
//            {
//                Config.ConfigItem afterProcessItem = new Config.ConfigItem("afterProcessItem", new AutoPane(), 0.4);
//
//                afterProcessArea.addConfigItem(afterProcessItem);
//            }

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
            AXButton logButton = new AXButton("Log");
            logButton.setTheme(FrameThemes.NORMAL_BUTTON);
            Label logTextLabel = logButton.getTextLabel();
            logTextLabel.setWrapText(true);

            AXProgressBar totalProgressBar = new AXProgressBar();
            AXProgressBar currentProgressBar = new AXProgressBar();
            totalProgressBar.setVisible(false);
            currentProgressBar.setVisible(false);

            logButton.getChildren().addAll(totalProgressBar, currentProgressBar);
            logButton.setPosition(totalProgressBar, true, 0, 0, 0, 0.65);
            logButton.setPosition(currentProgressBar, true, 0, 0, 0.65, 0);

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

                            log.info("All tasks have been completed");

                        } catch (InterruptedException ex) {
                            log.info("Task interrupted by user");
                        } catch (Exception er) {
                            log.error("An error occurred while executing the task: " + er.getMessage());
                        }
                        finally {
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

                    logButton.setTheme(FrameThemes.TRANSPARENT_BUTTON);
                    logButton.update();
                }
                else {
                    startButton.setText("Start");
                    logTextLabel.setVisible(true);

                    totalProgressBar.setVisible(false);
                    currentProgressBar.setVisible(false);
                    ctrlArea.setPosition(logButton, false, UI_SPACING + 140, UI_SPACING + 100, 0, 0);
                    ctrlArea.flipRelativeMode(logButton, AutoPane.Motion.LEFT, true);

                    LogFactory.totalProgressBar.setProgress(0, 1);
                    LogFactory.currentProgressBar.setProgress(0, 1);

                    logButton.setTheme(FrameThemes.NORMAL_BUTTON);
                    logButton.update();
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
            String name = button.getText();
            if (name.startsWith(lang) && !box.containsItem(button)) box.showItem(button);
            else if (!name.startsWith(lang)) box.removeItem(button);
        });
    }

}
