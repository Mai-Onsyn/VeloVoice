package mai_onsyn.VeloVoice;

import com.kieferlam.javafxblur.Blur;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.effect.Blend;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.*;
import javafx.util.Duration;
import mai_onsyn.AnimeFX.Frame.Layout.AutoPane;
import mai_onsyn.AnimeFX.Frame.Module.*;
import mai_onsyn.AnimeFX.Frame.Styles.ButtonStyle;
import mai_onsyn.AnimeFX.Frame.Module.Assistant.Cell;
import mai_onsyn.AnimeFX.Frame.Module.Assistant.DataCell;
import mai_onsyn.AnimeFX.Frame.Styles.CellStyle;
import mai_onsyn.AnimeFX.Frame.Styles.NamePopupStyle;
import mai_onsyn.AnimeFX.Frame.Utils.Toolkit;
import mai_onsyn.VeloVoice.App.Theme;
import mai_onsyn.VeloVoice.NetWork.Crawler.Websites.Kakuyomu;
import mai_onsyn.VeloVoice.NetWork.Crawler.Websites.LiNovel;
import mai_onsyn.VeloVoice.NetWork.Crawler.Websites.WenKu8;
import mai_onsyn.VeloVoice.NetWork.TTS.EdgeTTSClient;
import mai_onsyn.VeloVoice.NetWork.TTS.TTSClient;
import mai_onsyn.VeloVoice.NetWork.Voice;
import mai_onsyn.VeloVoice.Text.TTS;
import mai_onsyn.VeloVoice.Text.TextFactory;
import mai_onsyn.VeloVoice.Text.TextLoader;
import mai_onsyn.VeloVoice.Utils.AudioPlayer;
import mai_onsyn.VeloVoice.Utils.Structure;
import mai_onsyn.VeloVoice.Utils.Util;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static mai_onsyn.VeloVoice.App.AppConfig.*;
import static mai_onsyn.VeloVoice.App.Runtime.*;
import static mai_onsyn.VeloVoice.App.Theme.*;

public class FrameFactory {

    private static final double spacing = 10;
    private static final double fontSize = FONT_NORMAL.getSize() * 1.333333333333333333333333333333;
    private static final java.awt.Toolkit defaultToolkit = java.awt.Toolkit.getDefaultToolkit();
    public static final DecimalFormat numberFormat = new DecimalFormat("#.##");
    private static final ButtonStyle buttonStyle = new ThemedButtonStyle();

    private static SmoothTreeView treeView;
    public static AutoPane getTreeArea() {
        AutoPane pane = new AutoPane();

        AutoPane loadBox = new AutoPane();
        {
            Label label = ModuleCreator.createLabel("加载模式");

            List<DiffusionButton> choices = new ArrayList<>();
            List<LoadType> loadTypes = List.of(
                    LoadType.LOCAL_DIRECTLY,
                    LoadType.LOCAL_FULL,
                    LoadType.LOCAL_VOLUMED,
                    LoadType.WEN_KU8,
                    LoadType.LI_NOVEL,
                    LoadType.KAKUYOMU
            );
            List<String> choiceButtonNames = List.of(
                    "本地(文件)",
                    "本地(全集)",
                    "本地(分卷)",
                    "轻小说文库",
                    "轻之文库",
                    "角川文库Web"
            );
            SmoothChoiceBox choiceBox = ModuleCreator.createChoiceBox(FONT_NORMAL, 300);
            choiceBox.setText(choiceButtonNames.getFirst());
            for (int i = 0; i < loadTypes.size(); i++) {
                int I = i;
                DiffusionButton choiceButton = new DiffusionButton()
                        .name(choiceButtonNames.get(i))
                        .height(fontSize * 1.4)
                        .bgColor(MODULE_BG_COLOR)
                        .bgFocusColor(BUTTON_FOCUS_COLOR)
                        .fillColor(BUTTON_PRESSED_COLOR)
                        .textColor(TEXT_COLOR)
                        .textFocusColor(BUTTON_TEXT_FOCUS_COLOR)
                        .font(FONT_NORMAL)
                        .borderShape(10)
                        .animeDuration(BUTTON_ANIME_DURATION)
                        .init();
                choiceButton.setOnMouseClicked(_ -> {
                    loadType = loadTypes.get(I);
                    choiceBox.setText(choiceButtonNames.get(I));
                });
                choices.add(choiceButton);
            }
            choiceBox.addItem(choices.toArray(new DiffusionButton[0]));

            loadBox.setPosition(label, AutoPane.AlignmentMode.LEFT_CENT, AutoPane.LocateMode.RELATIVE, 0, 0.5);
            loadBox.setPosition(choiceBox, false, fontSize * 4, 0, 0, 0);

            loadBox.getChildren().addAll(label, choiceBox);
        }

        treeView = new SmoothTreeView(new ThemedCellStyle())
                .borderShape(10)
                .borderRadius(0.5)
                .animeDuration(100)
                .borderColor(THEME_COLOR)
                .bgColor(MODULE_BG_COLOR)
                .highlightBGColor(TREE_SWITCHED_BACKGROUND_COLOR)
                .highlightBGFocusColor(TREE_SWITCHED_BACKGROUND_FOCUS_COLOR)
                .highlightTextColor(TREE_SWITCHED_TEXT_COLOR)
                .highlightTextFocusColor(TREE_SWITCHED_TEXT_FOCUS_COLOR)
                .popupButtonStyle(buttonStyle)
                .namePopupStyle(new ThemedNamePopupStyle())
                .init();

        SmoothTextField inputField = ModuleCreator.createTextField();
        inputField.getTextField().setPromptText("txt所在文件夹/目录地址");
        //inputField.getTextField().setText("D:\\Users\\Desktop\\Test\\FullModeTest");

        treeView.setOnDragOver(event -> {
            if (event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY);
            }
            event.consume();
        });

        treeView.setOnDragDropped(event -> {
            event.setDropCompleted(true);
            event.consume();

            List<File> files = event.getDragboard().getFiles();
            if (files == null || files.isEmpty()) return;

            Structure<List<String>> parent = new Structure<>("Root");
            for (File file : files) {
                collectFilesToStructure(file, parent);
            }
            Structure.Factory.writeToTreeView(parent, treeView, true);
        });


        AutoPane operationPane = new AutoPane();
        {
            DiffusionButton saveTreeButton = ModuleCreator.createButton("保存文本结构");
            DiffusionButton loadButton = ModuleCreator.createButton("加载");

            loadButton.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY) {
                    Thread.ofVirtual().name("Load-Thread").start(() -> {
                        String urlString = inputField.getTextField().getText();
                        if (urlString.isEmpty()) {
                            logger.prompt("您想凭空创造吗？");
                            return;
                        }
                        logger.prompt("尝试加载 - " + urlString);

                        Structure<List<String>> rootStructure;

                        try {
                            switch (loadType) {
                                case LOCAL_DIRECTLY, LOCAL_FULL, LOCAL_VOLUMED -> {
                                    File targetFolder = new File(urlString);
                                    if (!targetFolder.exists()) {
                                        logger.error("指定文件夹不存在 - " + urlString);
                                        return;
                                    }
                                    File[] series = targetFolder.listFiles((_, name) -> name.endsWith(".txt"));
                                    if (series == null || series.length == 0) {
                                        logger.error("指定文件夹内没有可读取的txt文件");
                                        return;
                                    }
                                    rootStructure = new Structure<>("Root");
                                    for (File file : series) {
                                        collectFilesToStructure(file, rootStructure);
                                    }
                                    Structure.Factory.writeToTreeView(rootStructure, treeView, true);
                                }
                                case WEN_KU8 -> {
                                    if (urlString.contains("https://www.wenku8.net/book/") || urlString.contains("https://www.wenku8.net/novel/")) {
                                        rootStructure = new WenKu8(urlString).getContents();
                                        logger.prompt("已加载 - " + rootStructure.getName());
                                        Structure.Factory.writeToTreeView(rootStructure, treeView, false);
                                    }
                                    else logger.error(String.format("加载失败 - \"%s\" 不是正确的轻小说文库小说地址", urlString));
                                }
                                case LI_NOVEL -> {
                                    if (urlString.contains("https://www.linovel.net/book/")) {
                                        rootStructure = new LiNovel(urlString).getContents();
                                        logger.prompt("已加载 - " + rootStructure.getName());
                                        Structure.Factory.writeToTreeView(rootStructure, treeView, false);
                                    }
                                    else logger.error(String.format("加载失败 - \"%s\" 不是正确的轻之文库小说地址", urlString));
                                }
                                case KAKUYOMU -> {
                                    if (urlString.contains("https://kakuyomu.jp/works/")) {
                                        rootStructure = new Kakuyomu(urlString).getContents();
                                        logger.prompt("已加载 - " + rootStructure.getName());
                                        Structure.Factory.writeToTreeView(rootStructure, treeView, false);
                                    }
                                    else logger.error(String.format("加载失败 - \"%s\" 不是正确的角川文库小说地址", urlString));
                                }
                            }
                        } catch (Exception e) {
                            logger.error("加载失败 - " + e);
                        }
                    });
                }
            });
            saveTreeButton.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY) {
                    DirectoryChooser chooser = new DirectoryChooser();
                    File dir = chooser.showDialog(FrameApp.rootPane.getScene().getWindow());

                    Thread.ofVirtual().name("Save-Tree-Thread").start(() -> {
                        try {
                            Structure<List<String>> structure = Structure.Factory.ofItem(treeView.getRoot());
                            if (structure.getChildren().size() == 1) Structure.Factory.saveToFile(structure.getChildren().getFirst(), dir, false, 0, 0);
                            else {
                                for (int i = 0; i < structure.getChildren().size(); i++) {
                                    Structure.Factory.saveToFile(structure.getChildren().get(i), dir, isAppendOrdinal, i + 1, structure.getChildren().size());
                                }
                            }
                            logger.prompt("已保存文件树到 - " + dir.getAbsolutePath());
                        } catch (Exception e) {
                            logger.error("保存文件树失败 - " + e);
                            //e.printStackTrace();
                        }
                    });
                }
            });

            operationPane.setPosition(loadButton, true, 0, 0.525, 0, 0);
            operationPane.setPosition(saveTreeButton, true, 0.525, 0, 0, 0);

            operationPane.getChildren().addAll(loadButton, saveTreeButton);
        }

        pane.setPosition(loadBox, false, 0, 0, 0, fontSize * 1.4);
        pane.flipRelativeMode(loadBox, AutoPane.Motion.BOTTOM);

        pane.setPosition(inputField, false, false, false, false, 0, 0, fontSize * 1.4 + spacing, 0);

        pane.setPosition(operationPane, false, 0, 0, fontSize * 2.8 + spacing * 2, fontSize * 4.2 + spacing * 2);
        pane.flipRelativeMode(operationPane, AutoPane.Motion.BOTTOM);
        pane.setPosition(treeView, false, false, false, false, 0, 0, fontSize * 4.2 + spacing * 3, 0);

        pane.getChildren().addAll(loadBox, operationPane, inputField, treeView);
        return pane;
    }

    private static void collectFilesToStructure(File file, Structure<List<String>> parent) {
        if (file.isFile()) {
            String fileName = file.getName();
            if (file.getName().endsWith(".txt")) {
                fileName = fileName.substring(0, fileName.length() - 4);
                try {
                    Structure<List<String>> children;
                    switch (loadType) {
                        case LOCAL_FULL -> {
                            children = Structure.Factory.of(TextFactory.parseFromSeries(TextLoader.load(file)));
                            children.setName(fileName);
                        }
                        case LOCAL_VOLUMED -> {
                            children = Structure.Factory.of(TextFactory.parseFromVolume(TextLoader.load(file)));
                        }
                        case LOCAL_DIRECTLY -> {
                            children = new Structure<>(fileName, new ArrayList<>(List.of(TextLoader.load(file))));
                        }
                        default -> children = new Structure<>("null");
                    }
                    logger.prompt("已加载 - " + file.getName());
                    parent.getChildren().add(children);
                } catch (Exception e) {
                    logger.warn(String.format("加载失败：%s - %s", file.getAbsolutePath(), e));
                }
            }
        }
        else if (file.isDirectory()) {
            Structure<List<String>> subStructure = new Structure<>(file.getName());
            File[] subFiles = file.listFiles();
            if (subFiles != null) {
                for (File subFile : subFiles) {
                    collectFilesToStructure(subFile, subStructure);
                }
            }
            parent.getChildren().add(subStructure);
        }
        else logger.warn(String.format("Not a correct File : %s", file.getAbsolutePath()));
    }

    public static TextArea MAIN_PRESENT_AREA;
    public static AutoPane getTextArea() {
        AutoPane pane = new AutoPane();
        SmoothTextArea textArea = new SmoothTextArea()
                .bgColor(MODULE_BG_COLOR)
                .borderColor(THEME_COLOR)
                .borderRadius(0.5)
                .borderShape(10)
                .font(FONT_SMALLER)
                .textColor(TEXT_COLOR)
                .buttonStyle(buttonStyle)
                .init();
        MAIN_PRESENT_AREA = textArea.getTextArea();

        pane.setPosition(textArea, false, 0, 0, 0, 0);

        pane.getChildren().add(textArea);

        return pane;
    }

    public static AutoPane getOperationArea() {
        AutoPane pane = new AutoPane();

        AutoPane modelBox = new AutoPane();
        {
            Label label = ModuleCreator.createLabel("模型");

            SmoothChoiceBox voiceChoiceBox = ModuleCreator.createChoiceBox(FONT_SMALLER, 600);
            List<DiffusionButton> choices = new ArrayList<>(Voice.getVoiceList().size());
            voiceChoiceBox.setText(Voice.get(voiceModel).getString("ShortName"));
            EdgeTTSClient.setVoice(Voice.get(voiceModel));
            for (int i = 0; i < Voice.getVoiceList().size(); i++) {
                int I = i;
                DiffusionButton choiceButton = new DiffusionButton()
                        .name(Voice.getVoiceList().get(i).getString("ShortName"))
                        .height(fontSize * 1.4)
                        .bgColor(MODULE_BG_COLOR)
                        .bgFocusColor(BUTTON_FOCUS_COLOR)
                        .fillColor(BUTTON_PRESSED_COLOR)
                        .textColor(TEXT_COLOR)
                        .textFocusColor(BUTTON_TEXT_FOCUS_COLOR)
                        .font(FONT_SMALLER)
                        .animeDuration(BUTTON_ANIME_DURATION)
                        .init();
                choiceButton.setOnMouseClicked(event -> {
                    voiceChoiceBox.setText(Voice.getVoiceList().get(I).getString("ShortName"));
                    EdgeTTSClient.setVoice(Voice.getVoiceList().get(I));
                    voiceModel = Voice.getVoiceList().get(I).getString("ShortName");
                });
                choices.add(choiceButton);
            }
            voiceChoiceBox.addItem(choices.toArray(new DiffusionButton[0]));

            DiffusionButton preview = ModuleCreator.createButton("试听", FONT_SMALLER, 10);

            preview.setOnMouseClicked(event -> {
                if (event.getButton() != MouseButton.PRIMARY) return;

                Thread.ofVirtual().name("Preview-Thread").start(() -> {
                    try {
                        logger.prompt("开始加载预览语音");
                        TTSClient ttsClient = new EdgeTTSClient();
                        ttsClient.connect();
                        List<byte[]> audioByteArrayList = ttsClient.sendText(UUID.randomUUID(), previewText);
                        ttsClient.close();
                        logger.prompt("正在播放预览语音");
                        AudioPlayer.play(audioByteArrayList);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });
            });


            modelBox.setPosition(label, AutoPane.AlignmentMode.LEFT_CENT, AutoPane.LocateMode.RELATIVE, 0, 0.5);
            modelBox.setPosition(voiceChoiceBox, false, fontSize * 2.5, 50, 0, fontSize * 1.4);
            modelBox.setPosition(preview, false, 40, 0, 0, fontSize * 1.4);
            modelBox.flipRelativeMode(voiceChoiceBox, AutoPane.Motion.BOTTOM);
            modelBox.flipRelativeMode(preview, AutoPane.Motion.LEFT);
            modelBox.flipRelativeMode(preview, AutoPane.Motion.BOTTOM);

            modelBox.getChildren().addAll(label, voiceChoiceBox, preview);
        }

        AutoPane rateBox = new AutoPane();
        {
            Label label = ModuleCreator.createLabel("语速");

            SmoothSlider slider = ModuleCreator.createSlider(0.05, 2, 1.75);
            SmoothTextField textField = ModuleCreator.createTextField();
            textField.getTextField().setText("1.75");
            EdgeTTSClient.setVoiceRate(1.75);

            rateBox.setPosition(label, AutoPane.AlignmentMode.LEFT_CENT, AutoPane.LocateMode.RELATIVE, 0, 0.5);
            rateBox.setPosition(textField, false, 40, 0, 0, 0);
            rateBox.flipRelativeMode(textField, AutoPane.Motion.LEFT);
            rateBox.setPosition(slider, false, fontSize * 2.5 - 25, 30, -15, 0);

            boolean[] isUpdating = new boolean[1];
            slider.valueProperty().addListener((o, ov, nv) -> {
                double value = Math.round(nv.doubleValue() / 0.05) * 0.05;
                EdgeTTSClient.setVoiceRate(value);
                if (!isUpdating[0]) {
                    isUpdating[0] = true;
                    textField.getTextField().setText(numberFormat.format(value));
                    isUpdating[0] = false;
                }
            });

            ModuleCreator.setAsDecimalField(textField, isUpdating, slider);

            rateBox.getChildren().addAll(label, slider, textField);
        }

        AutoPane volumeBox = new AutoPane();
        {
            Label label = ModuleCreator.createLabel("音量");

            SmoothSlider slider = ModuleCreator.createSlider(0.05, 1.5, 1);
            SmoothTextField textField = ModuleCreator.createTextField();
            textField.getTextField().setText("1");

            volumeBox.setPosition(label, AutoPane.AlignmentMode.LEFT_CENT, AutoPane.LocateMode.RELATIVE, 0, 0.5);
            volumeBox.setPosition(textField, false, 40, 0, 0, 0);
            volumeBox.flipRelativeMode(textField, AutoPane.Motion.LEFT);

            volumeBox.setPosition(slider, false, fontSize * 2.5 - 25, 30, -15, 0);
            boolean[] isUpdating = new boolean[1];
            slider.valueProperty().addListener((o, ov, nv) -> {
                double value = Math.round(nv.doubleValue() / 0.05) * 0.05;
                EdgeTTSClient.setVoiceVolume(value);
                if (!isUpdating[0]) {
                    isUpdating[0] = true;
                    textField.getTextField().setText(numberFormat.format(value));
                    isUpdating[0] = false;
                }
            });

            ModuleCreator.setAsDecimalField(textField, isUpdating, slider);

            volumeBox.getChildren().addAll(label, slider, textField);
        }

        AutoPane pitchBox = new AutoPane();
        {
            Label label = ModuleCreator.createLabel("音调");

            SmoothSlider slider = ModuleCreator.createSlider(0.05, 2, 1);
            SmoothTextField textField = ModuleCreator.createTextField();
            textField.getTextField().setText("1");

            pitchBox.setPosition(label, AutoPane.AlignmentMode.LEFT_CENT, AutoPane.LocateMode.RELATIVE, 0, 0.5);
            pitchBox.setPosition(textField, false, 40, 0, 0, 0);
            pitchBox.flipRelativeMode(textField, AutoPane.Motion.LEFT);

            pitchBox.setPosition(slider, false, fontSize * 2.5 - 25, 30, -15, 0);
            boolean[] isUpdating = new boolean[1];
            slider.valueProperty().addListener((o, ov, nv) -> {
                double value = Math.round(nv.doubleValue() / 0.05) * 0.05;
                EdgeTTSClient.setVoicePitch(value);
                if (!isUpdating[0]) {
                    isUpdating[0] = true;
                    textField.getTextField().setText(numberFormat.format(value));
                    isUpdating[0] = false;
                }
            });

            ModuleCreator.setAsDecimalField(textField, isUpdating, slider);

            pitchBox.getChildren().addAll(label, slider, textField);
        }

        AutoPane saveBox = new AutoPane();
        {
            Label label = ModuleCreator.createLabel("输出路径");

            SmoothTextField textField = ModuleCreator.createTextField();
            textField.getTextField().setPromptText("音频输出文件夹");
            //textField.getTextField().setText("D:\\Users\\Desktop\\Test");

            saveBox.setPosition(label, AutoPane.AlignmentMode.LEFT_CENT, AutoPane.LocateMode.RELATIVE, 0, 0.5);
            saveBox.setPosition(textField, false, fontSize * 4, 0, 0, 0);

            saveBox.getChildren().addAll(label, textField);
        }

        AutoPane operationBox = new AutoPane();
        {
            DiffusionButton startButton = ModuleCreator.createButton("开始", FONT_NORMAL, 30);
            DiffusionButton pauseButton = ModuleCreator.createButton("暂停", FONT_NORMAL, 30);
            DiffusionButton stopButton = ModuleCreator.createButton("终止", FONT_NORMAL, 30);

            startButton.setOnMouseClicked(event -> {
                if (event.getButton() != MouseButton.PRIMARY) return;
                if (runningState) {
                    logger.prompt("还没结束呢");
                    return;
                }

                if (((SmoothTextField) saveBox.getChildren().get(1)).getTextField().getText().isEmpty()) {
                    logger.prompt("还没指定输出路径呢");
                    return;
                }
                File outputFolder = new File(((SmoothTextField) saveBox.getChildren().get(1)).getTextField().getText());
                if (!outputFolder.exists()) if (!outputFolder.mkdirs()) {
                    logger.error("无法创建音频输出目录 - " + outputFolder.getAbsolutePath());
                    return;
                }

                logger.prompt("TTS！启动！");

                ((SmoothProgressBar) progressPane.getChildren().get(0)).setProgress(0);
                ((SmoothProgressBar) progressPane.getChildren().get(1)).setProgress(0);
                ((Label) progressPane.getChildren().get(2)).setText("- 总进度 -");
                ((Label) progressPane.getChildren().get(3)).setText("- 当前 -");
                progressPane.setVisible(true);
                runningState = true;

                Structure<List<String>> tasks = Structure.Factory.ofItem(treeView.getRoot());
                TaskMainThread = Thread.ofVirtual().name("Task-Main-Thread").start(() -> {
                    long startTime = System.currentTimeMillis();
                    TTS.startNewTask(tasks, outputFolder);
                    logger.prompt(String.format("完成！耗时 %s", Util.formatDuration(System.currentTimeMillis() - startTime)));
                    progressPane.setVisible(false);
                    runningState = false;
                    pausing = false;
                    pauseButton.setText("暂停");
                });
            });
            pauseButton.setOnMouseClicked(event -> {
                if (event.getButton() != MouseButton.PRIMARY) return;
                if (!runningState) {
                    logger.prompt("还没开始呢");
                    return;
                }
                pausing = !pausing;
                logger.prompt(pausing ? "-THE WORLD-" : "-時は動き始めだ-");
                pauseButton.setText(pausing ? "继续" : "暂停");
            });
            stopButton.setOnMouseClicked(event -> {
                if (event.getButton() != MouseButton.PRIMARY) return;
                if (!runningState) {
                    logger.prompt("还没开始呢");
                    return;
                }
                if (TaskMainThread != null) {
                    if (pausing) {
                        pausing = false;
                        pauseButton.setText("暂停");
                    }
                    TaskMainThread.interrupt();
                    runningState = false;
                    if (TTS.executor.tasks != null) TTS.executor.tasks.clear();
                    logger.prompt("世界が終わる―――");
                }
            });

            operationBox.setPosition(startButton, true, 0, 0.7, 0, 0);
            operationBox.setPosition(pauseButton, true, 0.35, 0.35, 0, 0);
            operationBox.setPosition(stopButton, true, 0.7, 0, 0, 0);

            operationBox.getChildren().addAll(startButton, pauseButton, stopButton);
        }

        logger = new FXLogger()
                .bgColor(MODULE_BG_COLOR)
                .borderColor(THEME_COLOR)
                .borderRadius(0.5)
                .borderShape(10)
                .font(FONT_SMALLER)
                .textColor(TEXT_COLOR)
                .buttonStyle(buttonStyle)
                .logLevel(logLevel)
                .init();

        pane.setPosition(modelBox, false, 0, 0, 0, fontSize * 1.4);
        pane.flipRelativeMode(modelBox, AutoPane.Motion.BOTTOM);

        pane.setPosition(rateBox, false, 0, 0, fontSize * 1.4 + spacing, fontSize * 2.8 + spacing);
        pane.flipRelativeMode(rateBox, AutoPane.Motion.BOTTOM);

        pane.setPosition(volumeBox, false, 0, 0, fontSize * 2.8 + spacing * 2, fontSize * 4.2 + spacing * 2);
        pane.flipRelativeMode(volumeBox, AutoPane.Motion.BOTTOM);

        pane.setPosition(pitchBox, false, 0, 0, fontSize * 4.2 + spacing * 3, fontSize * 5.6 + spacing * 3);
        pane.flipRelativeMode(pitchBox, AutoPane.Motion.BOTTOM);

        pane.setPosition(saveBox, false, 0, 0, fontSize * 5.6 + spacing * 4, fontSize * 7 + spacing * 4);
        pane.flipRelativeMode(saveBox, AutoPane.Motion.BOTTOM);

        pane.setPosition(operationBox, false, 0, 0, fontSize * 7 + spacing * 5, fontSize * 10 + spacing * 5);
        pane.flipRelativeMode(operationBox, AutoPane.Motion.BOTTOM);

        pane.setPosition(logger, false, 0, 0, fontSize * 10 + spacing * 6, 0);


        pane.getChildren().addAll(modelBox, rateBox, volumeBox, pitchBox, saveBox, operationBox, logger);

        return pane;
    }

    public static Stage settingsWindow;
    public static DiffusionButton getSettingsButton() {
        DiffusionButton settingsButton = ModuleCreator.createCleanImageButton(new Image(LIGHT_THEME ? "textures/setting_light.png" : "textures/setting_dark.png"), BUTTON_FOCUS_COLOR, BUTTON_PRESSED_COLOR);

        settingsWindow = getSettingsWindow();
        FrameApp.STAGE.setOnCloseRequest(_ -> {
            if (settingsWindow.isShowing()) settingsWindow.close();
        });

        settingsButton.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                if (!settingsWindow.isShowing()) {
                    if (isWindowSupport) {
                        settingsWindow = getSettingsWindow();
                        FrameApp.STAGE.setOnCloseRequest(_ -> {
                            if (settingsWindow.isShowing()) settingsWindow.close();
                        });
                    }
                    settingsWindow.show();
                    if (isWindowSupport) Blur.applyBlur(settingsWindow, Theme.blurMode);
                }
                else {
                    if (settingsWindow.isIconified()) {
                        settingsWindow.setIconified(false);
                    }
                    settingsWindow.toFront();
                    defaultToolkit.beep();
                }
            }
        });

        return settingsButton;
    }

    private static Stage getSettingsWindow() {
        Stage stage = new Stage();
        stage.setTitle("设置");

        AutoPane root = new AutoPane();
        root.setOnMousePressed(_ -> root.requestFocus());
        stage.setOnShown(_ -> root.requestFocus());
        Scene scene = new Scene(root);
        stage.setScene(scene);

        {
            if (isWindowSupport) {
                scene.setFill(Color.TRANSPARENT);
                root.setBackground(Background.EMPTY);
                stage.initStyle(StageStyle.TRANSPARENT);

                Rectangle WINDOW_SHADOW = new Rectangle();
                WINDOW_SHADOW.setFill(Theme.LIGHT_THEME ? Theme.LIGHT_THEME_COLOR : Theme.DARK_THEME_COLOR);
                WINDOW_SHADOW.setOpacity(0.01);
                root.setPosition(WINDOW_SHADOW, false, 0, 0, 0, 0);
                root.getChildren().add(WINDOW_SHADOW);

                AutoPane contentPane = new AutoPane();
                drawSettingsWindow(contentPane);

                AutoPane titlePane = getWindowModule(stage);

                root.setPosition(titlePane, false, 0, 0, 0, 28);
                root.setPosition(contentPane, false, 0, 0, 28, 0);
                root.flipRelativeMode(titlePane, AutoPane.Motion.BOTTOM);

                root.getChildren().addAll(contentPane, titlePane);

                Util.DrawUtil.addDrawFunc(stage, root);
            }
            else drawSettingsWindow(root);
        }

        root.setPrefSize(960, 600);
        stage.setMinWidth(400);
        stage.setMinHeight(300);
        stage.getIcons().add(new Image("textures/icon.png"));

        return stage;
    }

    private static void drawSettingsWindow(AutoPane root) {
        AutoPane windowConfig = new AutoPane();
        {
            AutoPane titleBox = new AutoPane();
            {
                Label label = ModuleCreator.createLabel("图形");

                titleBox.setPosition(label, AutoPane.AlignmentMode.CENTER, AutoPane.LocateMode.RELATIVE, 0.5, 0.5);
                titleBox.getChildren().addAll(label);
            }

            AutoPane themeBox = new AutoPane();
            {
                Label label = ModuleCreator.createLabel("主题");

                SmoothSwitch switchButton = new SmoothSwitch()
                        .width(fontSize * 2.2)
                        .height(fontSize * 1.2)
                        .state(LIGHT_THEME)
                        .bgColor(DARK_THEME_COLOR)
                        .bgOpenColor(LIGHT_THEME_COLOR)
                        .bgHoverColor(Toolkit.adjustBrightness(DARK_THEME_COLOR, DARK_THEME_COLOR.getBrightness() + 0.2))
                        .bgOpenHoverColor(Toolkit.adjustBrightness(LIGHT_THEME_COLOR, LIGHT_THEME_COLOR.getBrightness() - 0.2))
                        .thumbColor(LIGHT_THEME_COLOR)
                        .thumbOpenColor(DARK_THEME_COLOR)
                        .thumbHoverColor(Toolkit.adjustBrightness(LIGHT_THEME_COLOR, LIGHT_THEME_COLOR.getBrightness() - 0.2))
                        .thumbPressedColor(Toolkit.adjustBrightness(LIGHT_THEME_COLOR, LIGHT_THEME_COLOR.getBrightness() - 0.4))
                        .thumbOpenHoverColor(Toolkit.adjustBrightness(DARK_THEME_COLOR, DARK_THEME_COLOR.getBrightness() + 0.2))
                        .thumbOpenPressedColor(Toolkit.adjustBrightness(DARK_THEME_COLOR, DARK_THEME_COLOR.getBrightness() + 0.4))
                        .borderRadius(0.5)
                        .borderColor(THEME_COLOR)
                        .init();
                //switchButton.changedProperty().setValue(true);

                Label valueLabel = ModuleCreator.createLabel(LIGHT_THEME ? "Light" : "Dark♂");

                switchButton.stateProperty().addListener((o, ov, nv) -> {
                    LIGHT_THEME = nv;
                    valueLabel.setText(LIGHT_THEME ? "Light" : "Dark♂");
                });


                themeBox.setPosition(label, AutoPane.AlignmentMode.LEFT_CENT, AutoPane.LocateMode.RELATIVE, 0, 0.5);
                themeBox.setPosition(switchButton, AutoPane.AlignmentMode.LEFT_CENT, AutoPane.LocateMode.ABSOLUTE, fontSize * 6, fontSize * 0.7);
                themeBox.setPosition(valueLabel, AutoPane.AlignmentMode.LEFT_CENT, AutoPane.LocateMode.ABSOLUTE, fontSize * 9, fontSize * 0.7);

                themeBox.getChildren().addAll(label, switchButton, valueLabel);
            }

            AutoPane colorBox = new AutoPane();
            {
                Label label = ModuleCreator.createLabel("主题色");

                SmoothChoiceBox choiceBox = new SmoothChoiceBox(25)
                        .name("#" + Toolkit.colorToString(THEME_COLOR))
                        .font(FONT_NORMAL)
                        .popupAnimeDuration(100)
                        .borderShape(10)
                        .popupBorderShape(10)
                        .popupMaxHeight(500)
                        .borderColor(THEME_COLOR)
                        .bgColor(TRANSPERTANT_THEME_COLOR)
                        .bgFocusColor(Toolkit.adjustBrightness(TRANSPERTANT_THEME_COLOR, TRANSPERTANT_THEME_COLOR.getBrightness() + 0.2))
                        .fillColor(BUTTON_PRESSED_COLOR)
                        .textColor(TEXT_COLOR)
                        .textFocusColor(BUTTON_TEXT_FOCUS_COLOR)
                        .borderRadius(0.5)
                        .height(fontSize * 1.4)
                        .width(fontSize * 5)
                        .animeDuration(BUTTON_ANIME_DURATION)
                        .init();
                List<DiffusionButton> themeButtons = new ArrayList<>();
                for (Color color : ALTERNATE_THEMES) {
                    DiffusionButton choiceButton = new DiffusionButton()
                            .name("#" + Toolkit.colorToString(color))
                            .height(fontSize * 1.4)
                            .bgColor(Toolkit.adjustOpacity(color, 0.7))
                            .bgFocusColor(Toolkit.adjustBrightness(color, color.getBrightness() + 0.2, 0.7))
                            .fillColor(BUTTON_PRESSED_COLOR)
                            .textColor(TEXT_COLOR)
                            .textFocusColor(color)
                            .font(FONT_SMALLER)
                            .animeDuration(BUTTON_ANIME_DURATION)
                            .init();
                    choiceButton.setOnMouseClicked(event -> {
                        if (event.getButton() == MouseButton.PRIMARY) {
                            THEME_COLOR = color;
                            choiceBox.setBgColor(Toolkit.adjustOpacity(color, 0.3));
                            choiceBox.setBgFocusColor(Toolkit.adjustBrightness(color, color.getBrightness() + 0.2, 0.3), true);
                            choiceBox.setText("#" + Toolkit.colorToString(color));
                        }
                    });

                    themeButtons.add(choiceButton);
                }
                choiceBox.addItem(themeButtons.toArray(new DiffusionButton[0]));


                themeBox.setPosition(label, AutoPane.AlignmentMode.LEFT_CENT, AutoPane.LocateMode.RELATIVE, 0, 0.5);
                themeBox.setPosition(choiceBox, false, fontSize * 6, 0, 0, 0);

                colorBox.getChildren().addAll(label, choiceBox);
            }

            AutoPane bgImageBox = new AutoPane();
            {
                Label label = ModuleCreator.createLabel("背景图片");

                SmoothTextField textField = ModuleCreator.createTextField();
                textField.getTextField().setPromptText("图片url");

                textField.getTextField().textProperty().addListener((o, ov, nv) -> {
                    try {
                        BACKGROUND_IMAGE = Toolkit.loadImage(nv);
                        BACKGROUND_IMAGE_URI = nv;

                        if (!enableWinUI) {
                            Blend effect = Toolkit.getColorBlend(LIGHT_THEME ? LIGHT_THEME_COLOR : DARK_THEME_COLOR, BACKGROUND_BRIGHTNESS);
                            effect.setBottomInput(new GaussianBlur(BACKGROUND_AMBIGUITY));
                            Toolkit.addBackGroundImage(FrameApp.rootPane, BACKGROUND_IMAGE);
                            Toolkit.setBackGroundImageEffect(FrameApp.rootPane, effect);
                            Toolkit.addBackGroundImage(root, BACKGROUND_IMAGE);
                            Toolkit.setBackGroundImageEffect(root, effect);
                        }
                    } catch (IOException _) {}
                });
                textField.getTextField().setText(BACKGROUND_IMAGE_URI);

                bgImageBox.setPosition(label, AutoPane.AlignmentMode.LEFT_CENT, AutoPane.LocateMode.RELATIVE, 0, 0.5);
                bgImageBox.setPosition(textField, false, fontSize * 6, 0, 0, 0);

                bgImageBox.getChildren().addAll(label, textField);
            }

            AutoPane bgImageAmbiguityBox = new AutoPane();
            {
                Label label = ModuleCreator.createLabel("背景模糊度");

                SmoothSlider slider = ModuleCreator.createSlider(0, 60, BACKGROUND_AMBIGUITY);
                SmoothTextField textField = ModuleCreator.createTextField();
                textField.getTextField().setText(String.valueOf((int) BACKGROUND_AMBIGUITY));

                bgImageAmbiguityBox.setPosition(label, AutoPane.AlignmentMode.LEFT_CENT, AutoPane.LocateMode.RELATIVE, 0, 0.5);
                bgImageAmbiguityBox.setPosition(textField, false, 40, 0, 0, 0);
                bgImageAmbiguityBox.flipRelativeMode(textField, AutoPane.Motion.LEFT);
                bgImageAmbiguityBox.setPosition(slider, false, fontSize * 6 - 25, 30, -15, 0);

                boolean[] isUpdating = new boolean[1];
                slider.valueProperty().addListener((o, ov, nv) -> {
                    double value = Math.round(nv.doubleValue());
                    BACKGROUND_AMBIGUITY = value;

                    Blend effect = Toolkit.getColorBlend(LIGHT_THEME ? LIGHT_THEME_COLOR : DARK_THEME_COLOR, BACKGROUND_BRIGHTNESS);
                    effect.setBottomInput(new GaussianBlur(value));
                    Toolkit.setBackGroundImageEffect(FrameApp.rootPane, effect);
                    Toolkit.setBackGroundImageEffect(root, effect);
                    if (!isUpdating[0]) {
                        isUpdating[0] = true;
                        textField.getTextField().setText(numberFormat.format(value));
                        isUpdating[0] = false;
                    }
                });

                ModuleCreator.setAsDecimalField(textField, isUpdating, slider);

                bgImageAmbiguityBox.getChildren().addAll(label, slider, textField);
            }

            AutoPane bgImageBrightnessBox = new AutoPane();
            {
                Label label = ModuleCreator.createLabel(LIGHT_THEME ? "背景亮度" : "背景暗度");

                SmoothSlider slider = ModuleCreator.createSlider(0, 1, BACKGROUND_BRIGHTNESS);
                SmoothTextField textField = ModuleCreator.createTextField();
                textField.getTextField().setText(numberFormat.format(BACKGROUND_BRIGHTNESS));

                bgImageBrightnessBox.setPosition(label, AutoPane.AlignmentMode.LEFT_CENT, AutoPane.LocateMode.RELATIVE, 0, 0.5);
                bgImageBrightnessBox.setPosition(textField, false, 40, 0, 0, 0);
                bgImageBrightnessBox.flipRelativeMode(textField, AutoPane.Motion.LEFT);

                bgImageBrightnessBox.setPosition(slider, false, fontSize * 6 - 25, 30, -15, 0);
                boolean[] isUpdating = new boolean[1];
                slider.valueProperty().addListener((o, ov, nv) -> {
                    double value = Math.round(nv.doubleValue() / 0.05) * 0.05;

                    BACKGROUND_BRIGHTNESS = value;

                    Blend effect = Toolkit.getColorBlend(LIGHT_THEME ? LIGHT_THEME_COLOR : DARK_THEME_COLOR, value);
                    effect.setBottomInput(new GaussianBlur(BACKGROUND_AMBIGUITY));
                    Toolkit.setBackGroundImageEffect(FrameApp.rootPane, effect);
                    Toolkit.setBackGroundImageEffect(root, effect);
                    if (!isUpdating[0]) {
                        isUpdating[0] = true;
                        textField.getTextField().setText(numberFormat.format(value));
                        isUpdating[0] = false;
                    }
                });

                ModuleCreator.setAsDecimalField(textField, isUpdating, slider);

                bgImageBrightnessBox.getChildren().addAll(label, slider, textField);
            }

            AutoPane choiceWinUIBox = new AutoPane();
            {
                Label label = ModuleCreator.createLabel("Blur Effect");

                List<DiffusionButton> choices = new ArrayList<>();
                List<Blur> blurs = List.of(
                        Blur.NONE,
                        Blur.BLUR_BEHIND,
                        Blur.ACRYLIC
                );
                List<String> choiceButtonNames = List.of(
                        "Transparent",
                        "Blur Behind",
                        "Acrylic"
                );
                SmoothChoiceBox choiceBox = ModuleCreator.createChoiceBox(FONT_NORMAL, 300);
                choiceBox.setText(switch (blurMode) {
                    case ACRYLIC -> "Acrylic";
                    case BLUR_BEHIND -> "Blur Behind";
                    case NONE -> "Transparent";
                });
                for (int i = 0; i < 3; i++) {
                    int I = i;
                    DiffusionButton choiceButton = new DiffusionButton()
                            .name(choiceButtonNames.get(i))
                            .height(fontSize * 1.4)
                            .bgColor(MODULE_BG_COLOR)
                            .bgFocusColor(BUTTON_FOCUS_COLOR)
                            .fillColor(BUTTON_PRESSED_COLOR)
                            .textColor(TEXT_COLOR)
                            .textFocusColor(BUTTON_TEXT_FOCUS_COLOR)
                            .font(FONT_NORMAL)
                            .borderShape(10)
                            .animeDuration(BUTTON_ANIME_DURATION)
                            .init();
                    choiceButton.setOnMouseClicked(_ -> {
                        blurMode = blurs.get(I);
                        Blur.applyBlur(FrameApp.STAGE, blurMode);
                        Blur.applyBlur(settingsWindow, blurMode);
                        choiceBox.setText(choiceButtonNames.get(I));
                    });
                    choices.add(choiceButton);
                }
                choiceBox.addItem(choices.toArray(new DiffusionButton[0]));

                choiceWinUIBox.setPosition(label, AutoPane.AlignmentMode.LEFT_CENT, AutoPane.LocateMode.RELATIVE, 0, 0.5);
                choiceWinUIBox.setPosition(choiceBox, false, fontSize * 6, 0, 0, 0);

                choiceWinUIBox.getChildren().addAll(label, choiceBox);
            }

            AutoPane enableWinUIBox = new AutoPane();
            {
                Label label = ModuleCreator.createLabel("Windows UI");


                SmoothSwitch switchButton = ModuleCreator.createSwitch(enableWinUI);

                Label valueLabel = ModuleCreator.createLabel(enableWinUI ? "启用" : "禁用");

                switchButton.stateProperty().addListener((o, ov, nv) -> {
                    enableWinUI = nv;
                    valueLabel.setText(enableWinUI ? "启用" : "禁用");

                    bgImageBox.setDisable(nv);
                    bgImageAmbiguityBox.setDisable(nv);
                    bgImageBrightnessBox.setDisable(nv);
                    choiceWinUIBox.setDisable(!nv);
                    if (nv) {
                        if (FrameApp.rootPane.getChildren().getFirst() instanceof ImageView) FrameApp.rootPane.getChildren().removeFirst();
                        if (root.getChildren().getFirst() instanceof ImageView) root.getChildren().removeFirst();
                    }
                    else {
                        Blend effect = Toolkit.getColorBlend(LIGHT_THEME ? LIGHT_THEME_COLOR : DARK_THEME_COLOR, BACKGROUND_BRIGHTNESS);
                        effect.setBottomInput(new GaussianBlur(BACKGROUND_AMBIGUITY));
                        Toolkit.addBackGroundImage(FrameApp.rootPane, BACKGROUND_IMAGE);
                        Toolkit.setBackGroundImageEffect(FrameApp.rootPane, effect);
                        Toolkit.addBackGroundImage(root, BACKGROUND_IMAGE);
                        Toolkit.setBackGroundImageEffect(root, effect);
                    }
                });

                Label warnLabel = ModuleCreator.createLabel("不稳定！");
                warnLabel.setTextFill(THEME_COLOR);

                enableWinUIBox.setPosition(label, AutoPane.AlignmentMode.LEFT_CENT, AutoPane.LocateMode.RELATIVE, 0, 0.5);
                enableWinUIBox.setPosition(switchButton, AutoPane.AlignmentMode.LEFT_CENT, AutoPane.LocateMode.ABSOLUTE, fontSize * 6, fontSize * 0.7);
                enableWinUIBox.setPosition(valueLabel, AutoPane.AlignmentMode.LEFT_CENT, AutoPane.LocateMode.ABSOLUTE, fontSize * 9, fontSize * 0.7);
                enableWinUIBox.setPosition(warnLabel, AutoPane.AlignmentMode.LEFT_CENT, AutoPane.LocateMode.ABSOLUTE, fontSize * 12, fontSize * 0.7);

                enableWinUIBox.getChildren().addAll(label, switchButton, valueLabel, warnLabel);
            }

            if (isWindowSupport) {
                bgImageBox.setDisable(true);
                bgImageAmbiguityBox.setDisable(true);
                bgImageBrightnessBox.setDisable(true);
            } else {
                choiceWinUIBox.setDisable(true);
            }

            if (systemSupportButLibraryNotExist || !Util.isWindowSupport()) {
                choiceWinUIBox.setDisable(true);
                enableWinUIBox.setDisable(true);
            }

            collectConfigPart(windowConfig, titleBox, themeBox, colorBox, enableWinUIBox, choiceWinUIBox, bgImageBox, bgImageAmbiguityBox, bgImageBrightnessBox);
            titleBox.setStyle("-fx-background-color: #" + Toolkit.colorToString(TRANSPERTANT_THEME_COLOR));
        }

        AutoPane networkConfig = new AutoPane();
        {
            AutoPane titleBox = new AutoPane();
            {
                Label label = ModuleCreator.createLabel("网络");

                titleBox.setPosition(label, AutoPane.AlignmentMode.CENTER, AutoPane.LocateMode.RELATIVE, 0.5, 0.5);
                titleBox.getChildren().addAll(label);
            }

            AutoPane threadBox = new AutoPane();
            {
                Label label = ModuleCreator.createLabel("连接线程数");

                SmoothSlider slider = ModuleCreator.createSlider(1, 128, maxConnectThread);
                SmoothTextField textField = ModuleCreator.createTextField();
                textField.getTextField().setText(String.valueOf(maxConnectThread));

                threadBox.setPosition(label, AutoPane.AlignmentMode.LEFT_CENT, AutoPane.LocateMode.RELATIVE, 0, 0.5);
                threadBox.setPosition(textField, false, 40, 0, 0, 0);
                threadBox.flipRelativeMode(textField, AutoPane.Motion.LEFT);

                threadBox.setPosition(slider, false, fontSize * 6 - 25, 30, -15, 0);
                boolean[] isUpdating = new boolean[1];
                slider.valueProperty().addListener((o, ov, nv) -> {
                    int value = (int) Math.round(nv.doubleValue());
                    maxConnectThread = value;
                    if (!isUpdating[0]) {
                        isUpdating[0] = true;
                        textField.getTextField().setText(numberFormat.format(value));
                        isUpdating[0] = false;
                    }
                });

                ModuleCreator.setAsIntegerField(textField, isUpdating, slider);

                threadBox.getChildren().addAll(label, slider, textField);
            }
            
            AutoPane retryBox = new AutoPane();
            {
                Label label = ModuleCreator.createLabel("重试次数");

                SmoothSlider slider = ModuleCreator.createSlider(0, 24, retryCount);
                SmoothTextField textField = ModuleCreator.createTextField();
                textField.getTextField().setText(String.valueOf(retryCount));

                retryBox.setPosition(label, AutoPane.AlignmentMode.LEFT_CENT, AutoPane.LocateMode.RELATIVE, 0, 0.5);
                retryBox.setPosition(textField, false, 40, 0, 0, 0);
                retryBox.flipRelativeMode(textField, AutoPane.Motion.LEFT);

                retryBox.setPosition(slider, false, fontSize * 6 - 25, 30, -15, 0);

                boolean[] isUpdating = new boolean[1];
                slider.valueProperty().addListener((o, ov, nv) -> {
                    int value = (int) Math.round(nv.doubleValue());
                    retryCount = value;
                    if (!isUpdating[0]) {
                        isUpdating[0] = true;
                        textField.getTextField().setText(numberFormat.format(value));
                        isUpdating[0] = false;
                    }
                });

                ModuleCreator.setAsIntegerField(textField, isUpdating, slider);

                retryBox.getChildren().addAll(label, slider, textField);
            }

            AutoPane timeoutBox = new AutoPane();
            {
                Label label = ModuleCreator.createLabel("超时时间(秒)");

                SmoothSlider slider = ModuleCreator.createSlider(1, 30, timeoutSeconds);
                SmoothTextField textField = ModuleCreator.createTextField();
                textField.getTextField().setText(String.valueOf(timeoutSeconds));

                timeoutBox.setPosition(label, AutoPane.AlignmentMode.LEFT_CENT, AutoPane.LocateMode.RELATIVE, 0, 0.5);
                timeoutBox.setPosition(textField, false, 40, 0, 0, 0);
                timeoutBox.flipRelativeMode(textField, AutoPane.Motion.LEFT);
                timeoutBox.setPosition(slider, false, fontSize * 6 - 25, 30, -15, 0);

                boolean[] isUpdating = new boolean[1];
                slider.valueProperty().addListener((o, ov, nv) -> {
                    int value = (int) Math.round(nv.doubleValue());
                    timeoutSeconds = value;
                    if (!isUpdating[0]) {
                        isUpdating[0] = true;
                        textField.getTextField().setText(numberFormat.format(value));
                        isUpdating[0] = false;
                    }
                });

                ModuleCreator.setAsIntegerField(textField, isUpdating, slider);

                timeoutBox.getChildren().addAll(label, slider, textField);
            }

            collectConfigPart(networkConfig, titleBox, threadBox, retryBox, timeoutBox);
            titleBox.setStyle("-fx-background-color: #" + Toolkit.colorToString(TRANSPERTANT_THEME_COLOR));
        }

        AutoPane textConfig = new AutoPane();
        {
            AutoPane titleBox = new AutoPane();
            {
                Label label = ModuleCreator.createLabel("文本");

                titleBox.setPosition(label, AutoPane.AlignmentMode.CENTER, AutoPane.LocateMode.RELATIVE, 0.5, 0.5);
                titleBox.getChildren().addAll(label);
            }

            AutoPane pieceBox = new AutoPane();
            {
                Label label = ModuleCreator.createLabel("切片最大长度");

                SmoothSlider slider = ModuleCreator.createSlider(16, 512, textPieceSize);
                SmoothTextField textField = ModuleCreator.createTextField();
                textField.getTextField().setText(String.valueOf(textPieceSize));

                pieceBox.setPosition(label, AutoPane.AlignmentMode.LEFT_CENT, AutoPane.LocateMode.RELATIVE, 0, 0.5);
                pieceBox.setPosition(textField, false, 40, 0, 0, 0);
                pieceBox.flipRelativeMode(textField, AutoPane.Motion.LEFT);
                pieceBox.setPosition(slider, false, fontSize * 6 - 25, 30, -15, 0);

                boolean[] isUpdating = new boolean[1];
                slider.valueProperty().addListener((o, ov, nv) -> {
                    int value = (int) Math.round(nv.doubleValue());
                    textPieceSize = value;
                    if (!isUpdating[0]) {
                        isUpdating[0] = true;
                        textField.getTextField().setText(numberFormat.format(value));
                        isUpdating[0] = false;
                    }
                });

                ModuleCreator.setAsIntegerField(textField, isUpdating, slider);

                pieceBox.getChildren().addAll(label, slider, textField);
            }

            AutoPane symbolBox = new AutoPane();
            {
                Label label = ModuleCreator.createLabel("切片首选符号");

                SmoothTextField textField = ModuleCreator.createTextField();
                textField.getTextField().setPromptText("图片url");

                textField.getTextField().textProperty().addListener((o, ov, nv) -> {
                    if (Objects.equals(nv, ov)) return;
                    textSplitSymbols.clear();
                    textSplitSymbols.addAll(nv.chars().mapToObj(c -> (char) c).toList());
                });
                textField.getTextField().setText(textSplitSymbols.stream().map(String::valueOf).collect(Collectors.joining("")));

                symbolBox.setPosition(label, AutoPane.AlignmentMode.LEFT_CENT, AutoPane.LocateMode.RELATIVE, 0, 0.5);
                symbolBox.setPosition(textField, false, fontSize * 6, 0, 0, 0);

                symbolBox.getChildren().addAll(label, textField);
            }

            AutoPane appendNameBox = new AutoPane();
            {
                Label label = ModuleCreator.createLabel("章节名添加卷名");

                SmoothSwitch switchButton = ModuleCreator.createSwitch(isAppendVolumeName);

                Label valueLabel = ModuleCreator.createLabel(isAppendVolumeName ? "启用" : "禁用");

                switchButton.stateProperty().addListener((o, ov, nv) -> {
                    isAppendVolumeName = nv;
                    valueLabel.setText(isAppendVolumeName ? "启用" : "禁用");
                });


                appendNameBox.setPosition(label, AutoPane.AlignmentMode.LEFT_CENT, AutoPane.LocateMode.RELATIVE, 0, 0.5);
                appendNameBox.setPosition(switchButton, AutoPane.AlignmentMode.LEFT_CENT, AutoPane.LocateMode.ABSOLUTE, fontSize * 6, fontSize * 0.7);
                appendNameBox.setPosition(valueLabel, AutoPane.AlignmentMode.LEFT_CENT, AutoPane.LocateMode.ABSOLUTE, fontSize * 9, fontSize * 0.7);

                appendNameBox.getChildren().addAll(label, switchButton, valueLabel);
            }

            AutoPane appendOrdinalBox = new AutoPane();
            {
                Label label = ModuleCreator.createLabel("文件名添加序号");

                SmoothSwitch switchButton = ModuleCreator.createSwitch(isAppendOrdinal);

                Label valueLabel = ModuleCreator.createLabel(isAppendOrdinal ? "启用" : "禁用");

                switchButton.stateProperty().addListener((o, ov, nv) -> {
                    isAppendOrdinal = nv;
                    valueLabel.setText(isAppendOrdinal ? "启用" : "禁用");
                });


                appendOrdinalBox.setPosition(label, AutoPane.AlignmentMode.LEFT_CENT, AutoPane.LocateMode.RELATIVE, 0, 0.5);
                appendOrdinalBox.setPosition(switchButton, AutoPane.AlignmentMode.LEFT_CENT, AutoPane.LocateMode.ABSOLUTE, fontSize * 6, fontSize * 0.7);
                appendOrdinalBox.setPosition(valueLabel, AutoPane.AlignmentMode.LEFT_CENT, AutoPane.LocateMode.ABSOLUTE, fontSize * 9, fontSize * 0.7);

                appendOrdinalBox.getChildren().addAll(label, switchButton, valueLabel);
            }

            AutoPane appendExtraChapterNameBox = new AutoPane();
            {
                Label label = ModuleCreator.createLabel("添加额外章节名");

                SmoothSwitch switchButton = ModuleCreator.createSwitch(isAppendExtraChapterName);

                Label valueLabel = ModuleCreator.createLabel(isAppendExtraChapterName ? "启用" : "禁用");

                switchButton.stateProperty().addListener((o, ov, nv) -> {
                    isAppendExtraChapterName = nv;
                    valueLabel.setText(isAppendExtraChapterName ? "启用" : "禁用");
                });


                appendExtraChapterNameBox.setPosition(label, AutoPane.AlignmentMode.LEFT_CENT, AutoPane.LocateMode.RELATIVE, 0, 0.5);
                appendExtraChapterNameBox.setPosition(switchButton, AutoPane.AlignmentMode.LEFT_CENT, AutoPane.LocateMode.ABSOLUTE, fontSize * 6, fontSize * 0.7);
                appendExtraChapterNameBox.setPosition(valueLabel, AutoPane.AlignmentMode.LEFT_CENT, AutoPane.LocateMode.ABSOLUTE, fontSize * 9, fontSize * 0.7);

                appendExtraChapterNameBox.getChildren().addAll(label, switchButton, valueLabel);
            }

            AutoPane appendExtraVolumeNameBox = new AutoPane();
            {
                Label label = ModuleCreator.createLabel("添加额外卷名");


                SmoothSwitch switchButton = ModuleCreator.createSwitch(isAppendExtraVolumeName);

                Label valueLabel = ModuleCreator.createLabel(isAppendExtraVolumeName ? "启用" : "禁用");

                switchButton.stateProperty().addListener((o, ov, nv) -> {
                    isAppendExtraVolumeName = nv;
                    valueLabel.setText(isAppendExtraVolumeName ? "启用" : "禁用");
                });


                appendExtraVolumeNameBox.setPosition(label, AutoPane.AlignmentMode.LEFT_CENT, AutoPane.LocateMode.RELATIVE, 0, 0.5);
                appendExtraVolumeNameBox.setPosition(switchButton, AutoPane.AlignmentMode.LEFT_CENT, AutoPane.LocateMode.ABSOLUTE, fontSize * 6, fontSize * 0.7);
                appendExtraVolumeNameBox.setPosition(valueLabel, AutoPane.AlignmentMode.LEFT_CENT, AutoPane.LocateMode.ABSOLUTE, fontSize * 9, fontSize * 0.7);

                appendExtraVolumeNameBox.getChildren().addAll(label, switchButton, valueLabel);
            }

            collectConfigPart(textConfig, titleBox, pieceBox, symbolBox, appendNameBox, appendOrdinalBox, appendExtraChapterNameBox, appendExtraVolumeNameBox);
            titleBox.setStyle("-fx-background-color: #" + Toolkit.colorToString(TRANSPERTANT_THEME_COLOR));
        }

        AutoPane audioConfig = new AutoPane();
        {
            AutoPane titleBox = new AutoPane();
            {
                Label label = ModuleCreator.createLabel("音频");

                titleBox.setPosition(label, AutoPane.AlignmentMode.CENTER, AutoPane.LocateMode.RELATIVE, 0.5, 0.5);
                titleBox.getChildren().addAll(label);
            }

            AutoPane previewTextBox = new AutoPane();
            {
                Label label = ModuleCreator.createLabel("试听文本");

                SmoothTextField textField = ModuleCreator.createTextField();
                textField.getTextField().setPromptText("一段优美的语言");

                textField.getTextField().textProperty().addListener((o, ov, nv) -> {
                    if (Objects.equals(nv, ov) || nv.isEmpty()) return;

                    previewText = nv;
                });
                textField.getTextField().focusedProperty().addListener((o, ov, nv) -> {
                    if (!nv && textField.getTextField().getText().isEmpty()) {
                        textField.getTextField().setText("全名制作人们大家好，我是练习时长2.5年的个人练习生0d00，喜欢唱、跳、ciallo、0b0000001011010001");
                    }
                });
                textField.getTextField().setText(previewText);

                previewTextBox.setPosition(label, AutoPane.AlignmentMode.LEFT_CENT, AutoPane.LocateMode.RELATIVE, 0, 0.5);
                previewTextBox.setPosition(textField, false, fontSize * 6, 0, 0, 0);

                previewTextBox.getChildren().addAll(label, textField);
            }

            AutoPane appendChapterNameBox = new AutoPane();
            {
                Label label = ModuleCreator.createLabel("分段添加名称");

                SmoothSwitch switchButton = ModuleCreator.createSwitch(isAppendNameForSplitChapter);

                Label valueLabel = ModuleCreator.createLabel(isAppendNameForSplitChapter ? "启用" : "禁用");

                switchButton.stateProperty().addListener((o, ov, nv) -> {
                    isAppendNameForSplitChapter = nv;
                    valueLabel.setText(isAppendNameForSplitChapter ? "启用" : "禁用");
                });

                if (!splitChapter) appendChapterNameBox.setDisable(true);

                appendChapterNameBox.setPosition(label, AutoPane.AlignmentMode.LEFT_CENT, AutoPane.LocateMode.RELATIVE, 0, 0.5);
                appendChapterNameBox.setPosition(switchButton, AutoPane.AlignmentMode.LEFT_CENT, AutoPane.LocateMode.ABSOLUTE, fontSize * 6, fontSize * 0.7);
                appendChapterNameBox.setPosition(valueLabel, AutoPane.AlignmentMode.LEFT_CENT, AutoPane.LocateMode.ABSOLUTE, fontSize * 9, fontSize * 0.7);

                appendChapterNameBox.getChildren().addAll(label, switchButton, valueLabel);
            }

            AutoPane maxAudioDurationBox = new AutoPane();
            {
                Label label = ModuleCreator.createLabel("分段时长");

                SmoothSlider slider = ModuleCreator.createSlider(1, 120, maxAudioDuration);
                SmoothTextField textField = ModuleCreator.createTextField();
                textField.getTextField().setText(String.valueOf((int) maxAudioDuration));

                maxAudioDurationBox.setPosition(label, AutoPane.AlignmentMode.LEFT_CENT, AutoPane.LocateMode.RELATIVE, 0, 0.5);
                maxAudioDurationBox.setPosition(textField, false, 40, 0, 0, 0);
                maxAudioDurationBox.flipRelativeMode(textField, AutoPane.Motion.LEFT);
                maxAudioDurationBox.setPosition(slider, false, fontSize * 6 - 25, 30, -15, 0);

                boolean[] isUpdating = new boolean[1];
                slider.valueProperty().addListener((o, ov, nv) -> {
                    double value = Math.round(nv.doubleValue());
                    maxAudioDuration = value;

                    if (!isUpdating[0]) {
                        isUpdating[0] = true;
                        textField.getTextField().setText(numberFormat.format(value));
                        isUpdating[0] = false;
                    }
                });

                ModuleCreator.setAsDecimalField(textField, isUpdating, slider);
                if (!splitChapter) maxAudioDurationBox.setDisable(true);

                maxAudioDurationBox.getChildren().addAll(label, slider, textField);
            }

            AutoPane splitChapterBox = new AutoPane();
            {
                Label label = ModuleCreator.createLabel("分段保存音频");

                SmoothSwitch switchButton = ModuleCreator.createSwitch(splitChapter);

                Label valueLabel = ModuleCreator.createLabel(splitChapter ? "启用" : "禁用");

                switchButton.stateProperty().addListener((o, ov, nv) -> {
                    splitChapter = nv;
                    valueLabel.setText(splitChapter ? "启用" : "禁用");
                    if (splitChapter) {
                        appendChapterNameBox.setDisable(false);
                        maxAudioDurationBox.setDisable(false);
                    }
                    else {
                        appendChapterNameBox.setDisable(true);
                        maxAudioDurationBox.setDisable(true);
                    }
                });


                splitChapterBox.setPosition(label, AutoPane.AlignmentMode.LEFT_CENT, AutoPane.LocateMode.RELATIVE, 0, 0.5);
                splitChapterBox.setPosition(switchButton, AutoPane.AlignmentMode.LEFT_CENT, AutoPane.LocateMode.ABSOLUTE, fontSize * 6, fontSize * 0.7);
                splitChapterBox.setPosition(valueLabel, AutoPane.AlignmentMode.LEFT_CENT, AutoPane.LocateMode.ABSOLUTE, fontSize * 9, fontSize * 0.7);

                splitChapterBox.getChildren().addAll(label, switchButton, valueLabel);
            }

            collectConfigPart(audioConfig, titleBox, previewTextBox, splitChapterBox, appendChapterNameBox, maxAudioDurationBox);
            titleBox.setStyle("-fx-background-color: #" + Toolkit.colorToString(TRANSPERTANT_THEME_COLOR));
        }

        AutoPane rootBox = new AutoPane();

        typeConfigPartTile(rootBox, windowConfig, networkConfig, textConfig, audioConfig);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(rootBox);
        scrollPane.setFitToWidth(true);
        scrollPane.getStylesheets().add("styles/scroll-pane.css");
        Toolkit.addSmoothScrolling(scrollPane);

        BooleanProperty widthExceedsThreshold = new SimpleBooleanProperty(root.getWidth() > configWindowChangeWidth);
        root.widthProperty().addListener((o, ov, nv) -> {
            boolean exceedsThresholdNow = nv.doubleValue() > configWindowChangeWidth;
            if (widthExceedsThreshold.get() != exceedsThresholdNow) {
                widthExceedsThreshold.set(exceedsThresholdNow);
            }
        });

        widthExceedsThreshold.addListener((o, ov, nv) -> {
            rootBox.getChildren().clear();
            for (Node node : rootBox.getChildren()) {
                rootBox.delete(node);
            }

            if (nv) typeConfigPartTile(rootBox, windowConfig, networkConfig, textConfig, audioConfig);
            else typeConfigPartVertical(rootBox, windowConfig, networkConfig, textConfig, audioConfig);
        });

        root.setPosition(scrollPane, false, 20, 20, 20, 20);
        root.getChildren().add(scrollPane);
    }

    public static void drawProgressArea() {

        SmoothProgressBar totalProgressBar = new SmoothProgressBar()
                .borderShape(1)
                .borderColor(THEME_COLOR)
                .bgColor(MODULE_BG_COLOR)
                .progressColor(Toolkit.adjustOpacity(THEME_COLOR, 0.5))
                .borderRadius(0.5)
                .animeDuration(250)
                .innerHInsets(1)
                .innerWInsets(1)
                .init();
        SmoothProgressBar chapterProgressBar = new SmoothProgressBar()
                .borderShape(1)
                .borderColor(THEME_COLOR)
                .bgColor(MODULE_BG_COLOR)
                .progressColor(Toolkit.adjustOpacity(THEME_COLOR, 0.5))
                .borderRadius(0.5)
                .animeDuration(250)
                .innerHInsets(1)
                .innerWInsets(1)
                .init();

        Label totalLabel = new Label("总进度");
        Label chapterLabel = new Label("当前进度");
        totalLabel.setFont(new Font(10));
        chapterLabel.setFont(new Font(10));
        totalLabel.setTextFill(TEXT_COLOR);
        chapterLabel.setTextFill(TEXT_COLOR);

        progressPane.setPosition(totalProgressBar, true, 0, 0, 0, 0.6);
        progressPane.setPosition(chapterProgressBar, true, 0, 0, 0.6, 0);
        progressPane.setPosition(totalLabel, AutoPane.AlignmentMode.CENTER, AutoPane.LocateMode.RELATIVE, 0.5, 0.2);
        progressPane.setPosition(chapterLabel, AutoPane.AlignmentMode.CENTER, AutoPane.LocateMode.RELATIVE, 0.5, 0.8);


        totalProgress.addListener((o, ov, nv) -> {
            double progress = nv.doubleValue() / totalCount;
            Platform.runLater(() -> {
                totalProgressBar.setProgress(progress);
                totalLabel.setText(String.format("总进度 - %.2f%% - [%d/%d]", progress * 100, nv.intValue(), totalCount));
                progressPane.flushWidth(progressPane.getLayoutBounds().getWidth());
            });
        });
        currentProgress.addListener((o, ov, nv) -> {
            double progress = nv.doubleValue() / currentCount;
            Platform.runLater(() -> {
                chapterProgressBar.setProgress(progress);
                chapterLabel.setText(String.format("当前 - %s - %.2f%% - [%d/%d]", currentFile, progress * 100, nv.intValue(), currentCount));
                progressPane.flushWidth(progressPane.getLayoutBounds().getWidth());
                if (nv.doubleValue() > 1e-8) totalProgress.setValue(totalProgress.getValue() + 1);
            });
        });

        progressPane.getChildren().addAll(totalProgressBar, chapterProgressBar, totalLabel, chapterLabel);
    }

    public static AutoPane getWindowModule(Stage stage) {
        AutoPane bar = new AutoPane();

        ImageView icon = new ImageView(new Image("textures/icon.png"));
        icon.setFitWidth(18);
        icon.setFitHeight(18);
        Label title = new Label(stage.getTitle());
        title.setTextFill(TEXT_COLOR);

        DiffusionButton minimize = ModuleCreator.createCleanImageButton(LIGHT_THEME ? new Image("textures/minimize_light.png") : new Image("textures/minimize_dark.png"), Toolkit.adjustBrightness(BUTTON_FOCUS_COLOR, LIGHT_THEME ? 0.4 : 0.5), BUTTON_PRESSED_COLOR);
        DiffusionButton maximize = ModuleCreator.createCleanImageButton(LIGHT_THEME ? new Image("textures/maximize_light.png") : new Image("textures/maximize_dark.png"), Toolkit.adjustBrightness(BUTTON_FOCUS_COLOR, LIGHT_THEME ? 0.4 : 0.5), BUTTON_PRESSED_COLOR);
        DiffusionButton close = ModuleCreator.createCleanImageButton(LIGHT_THEME ? new Image("textures/close_light.png") : new Image("textures/close_dark.png"), Toolkit.adjustOpacity(Color.rgb(232, 17, 35), 1), Toolkit.adjustSaturation(Color.rgb(232, 17, 35), 0.5));

        close.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {

                Timeline fade = new Timeline(new KeyFrame(Duration.millis(100), new KeyValue(stage.opacityProperty(), 0)));
                fade.play();

                // 动画结束时关闭窗口
                fade.setOnFinished(_ -> {
                    stage.close();
                    stage.setOpacity(1);
                    if (settingsWindow.isShowing()) {
                        Timeline settingCloseFade = new Timeline(new KeyFrame(Duration.millis(200), new KeyValue(settingsWindow.opacityProperty(), 0)));
                        settingCloseFade.setOnFinished(_ -> {
                            settingsWindow.close();
                        });
                        settingCloseFade.play();
                    }
                });

            }
        });
        minimize.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                Timeline fade = new Timeline(new KeyFrame(Duration.millis(100), new KeyValue(stage.opacityProperty(), 0)));
                fade.play();
                fade.setOnFinished(_ -> {
                    stage.setIconified(true);
                });
            }
        });

        stage.iconifiedProperty().addListener((o, ov, nv) -> {
            if (!nv) {
                Timeline fade = new Timeline(new KeyFrame(Duration.millis(100), new KeyValue(stage.opacityProperty(), 1)));
                fade.play();
                Platform.runLater(() -> {
                    stage.getScene().getRoot().requestLayout();
                    stage.getScene().getRoot().setVisible(false);
                    stage.getScene().getRoot().setVisible(true);
                });
            }
        });

        maximize.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                stage.setMaximized(!stage.isMaximized());
                if (stage.isMaximized()) stage.setHeight(Screen.getPrimary().getBounds().getHeight() - java.awt.Toolkit.getDefaultToolkit().getScreenInsets(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration()).bottom);
            }
        });

        final double[] offset = new double[2];
        bar.setOnMousePressed(event -> {
            offset[0] = event.getSceneX();
            offset[1] = event.getSceneY();
        });
        bar.setOnMouseDragged(event -> {
            if (stage.isMaximized()) {
                stage.setMaximized(false);
            }
            stage.setX(event.getScreenX() - offset[0]);
            stage.setY(event.getScreenY() - offset[1]);
        });
        bar.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                stage.setMaximized(!stage.isMaximized());
                if (stage.isMaximized()) stage.setHeight(Screen.getPrimary().getBounds().getHeight() - java.awt.Toolkit.getDefaultToolkit().getScreenInsets(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration()).bottom);
            }
        });
        bar.addEventFilter(MouseEvent.MOUSE_DRAGGED, event -> {
            if (event.getTarget() != bar) {
                event.consume();
            }
        });

        bar.setPosition(icon, false, 5, 22, 5, 22);
        bar.setPosition(title, AutoPane.AlignmentMode.LEFT_CENT, AutoPane.LocateMode.ABSOLUTE, 30, 15);
        bar.setPosition(minimize, false, 144, 96, 0, 0);
        bar.setPosition(maximize, false, 96, 48, 0, 0);
        bar.setPosition(close, false, 48, 0, 0, 0);

        bar.flipRelativeMode(icon, AutoPane.Motion.RIGHT);
        bar.flipRelativeMode(icon, AutoPane.Motion.BOTTOM);
        bar.flipRelativeMode(minimize, AutoPane.Motion.LEFT);
        bar.flipRelativeMode(maximize, AutoPane.Motion.LEFT);
        bar.flipRelativeMode(close, AutoPane.Motion.LEFT);

        bar.setStyle(String.format("-fx-background-color: #%s", Toolkit.colorToString(Toolkit.adjustOpacity(MODULE_BG_COLOR, 0.4))));


        bar.getChildren().addAll(icon, title, minimize, maximize, close);

        return bar;
    }

    /**
     * 0 - readable number;
     * 1 - decimal end with '.';
     * 2 - empty;
     * 3 - not a number;
     */
    private static int checkDecimal(String s) {
        if (s == null || s.isEmpty()) {
            return 2;
        }

        boolean hasDecimalPoint = false;
        boolean hasMinusSign = false;
        int length = s.length();

        // Check if the string ends with a decimal point.
        if (s.charAt(length - 1) == '.') {
            // Check if the rest of the string (excluding the last character) is all digits.
            if (length > 1 && s.substring(0, length - 1).matches("-?\\d+")) {
                return 1;
            } else {
                return 3;
            }
        }

        // Check if the string starts with a minus sign.
        if (s.charAt(0) == '-') {
            hasMinusSign = true;
        }

        // Check if the string is a valid number.
        for (int i = hasMinusSign ? 1 : 0; i < length; i++) {
            char ch = s.charAt(i);

            if (ch == '.') {
                if (hasDecimalPoint) {
                    return 3; // More than one decimal point
                }
                hasDecimalPoint = true;
            } else if (!Character.isDigit(ch)) {
                return 3; // Contains non-digit characters
            }
        }

        // Valid number with at most one decimal point, no non-digit characters, and an optional leading minus sign
        return 0;
    }

    /**
     * 0 - readable number;
     * 1 - empty;
     * 2 - not a number;
     */
    private static int checkInteger(String s) {
        if (s == null || s.isEmpty()) {
            return 1;
        }

        if (!s.matches("\\d+")) return 2;

        return 0;
    }

    private static class ModuleCreator {
        public static SmoothTextField createTextField() {
            return new SmoothTextField()
                    .borderShape(10)
                    .borderColor(THEME_COLOR)
                    .bgColor(MODULE_BG_COLOR)
                    .borderRadius(0.5)
                    .font(FONT_SMALLER)
                    .textColor(TEXT_COLOR)
                    .buttonStyle(buttonStyle)
                    .subLineColor(TEXT_COLOR)
                    .init();
        }

        public static SmoothChoiceBox createChoiceBox(Font font, double maxHeight) {
            return new SmoothChoiceBox(25)
                    .font(font)
                    .popupAnimeDuration(100)
                    .borderShape(10)
                    .popupBorderShape(10)
                    .popupMaxHeight(maxHeight)
                    .borderColor(THEME_COLOR)
                    .bgColor(MODULE_BG_COLOR)
                    .bgFocusColor(BUTTON_FOCUS_COLOR)
                    .fillColor(BUTTON_PRESSED_COLOR)
                    .textColor(TEXT_COLOR)
                    .textFocusColor(BUTTON_TEXT_FOCUS_COLOR)
                    .borderRadius(0.5)
                    .height(fontSize * 1.4)
                    .width(fontSize * 5)
                    .animeDuration(BUTTON_ANIME_DURATION)
                    .init();
        }

        public static DiffusionButton createButton(String name) {
            return new DiffusionButton()
                    .name(name)
                    .height(fontSize * 1.4)
                    .width(fontSize * 5)
                    .borderShape(10)
                    .borderColor(THEME_COLOR)
                    .bgColor(MODULE_BG_COLOR)
                    .bgFocusColor(BUTTON_FOCUS_COLOR)
                    .fillColor(BUTTON_PRESSED_COLOR)
                    .textColor(TEXT_COLOR)
                    .textFocusColor(BUTTON_TEXT_FOCUS_COLOR)
                    .borderRadius(0.5)
                    .font(FONT_NORMAL)
                    .animeDuration(BUTTON_ANIME_DURATION)
                    .init();
        }

        public static DiffusionButton createCleanImageButton (Image image, Color focusColor, Color pressColor) {
            return new DiffusionButton()
                    .ftImage(image)
                    .borderShape(10)
                    .bgColor(Toolkit.adjustOpacity(focusColor, 0))
                    .bgFocusColor(focusColor)
                    .fillColor(pressColor)
                    .textColor(TEXT_COLOR)
                    .textFocusColor(BUTTON_TEXT_FOCUS_COLOR)
                    .ftImageRatio(0.7)
                    .animeDuration(BUTTON_ANIME_DURATION)
                    .init();
        }

        public static DiffusionButton createButton(String name, Font font, double borderShape) {
            return new DiffusionButton()
                    .name(name)
                    .height(fontSize * 1.4)
                    .width(fontSize * 5)
                    .borderShape(borderShape)
                    .borderColor(THEME_COLOR)
                    .bgColor(MODULE_BG_COLOR)
                    .bgFocusColor(BUTTON_FOCUS_COLOR)
                    .fillColor(BUTTON_PRESSED_COLOR)
                    .textColor(TEXT_COLOR)
                    .textFocusColor(BUTTON_TEXT_FOCUS_COLOR)
                    .borderRadius(0.5)
                    .font(font)
                    .animeDuration(BUTTON_ANIME_DURATION)
                    .init();
        }

        public static SmoothSlider createSlider(double min, double max, double value) {
            Circle thumb = new Circle(10);
            thumb.setStroke(THEME_COLOR);
            thumb.setStrokeWidth(0.5);
            SmoothSlider slider = new SmoothSlider(min, max, value)
                    .thumb(thumb)
                    .trackColor(SLIDER_BG_COLOR)
                    .trackProgressColor(TRANSPERTANT_THEME_COLOR)
                    .thumbColor(SLIDER_THUMB_COLOR)
                    .hoverColor(SLIDER_THUMB_HOVER_COLOR)
                    .pressedColor(SLIDER_THUMB_PRESSED_COLOR)
                    .init();

            Rectangle sliderClip = new Rectangle(100, 50);
            slider.setClip(sliderClip);
            sliderClip.setLayoutX(10);
            sliderClip.setLayoutY(12);
            sliderClip.setHeight(40);
            slider.widthProperty().addListener((o, ov, nv) -> sliderClip.setWidth(nv.doubleValue() - 20));

            return slider;
        }

        public static SmoothSwitch createSwitch(boolean value) {
            return new SmoothSwitch()
                    .width(fontSize * 2.2)
                    .height(fontSize * 1.2)
                    .state(value)
                    .bgColor(MODULE_BG_COLOR)
                    .bgOpenColor(TRANSPERTANT_THEME_COLOR)
                    .bgHoverColor(Toolkit.adjustBrightness(MODULE_BG_COLOR, MODULE_BG_COLOR.getBrightness() + (LIGHT_THEME ? -0.2 : 0.2)))
                    .bgOpenHoverColor(Toolkit.adjustBrightness(THEME_COLOR, THEME_COLOR.getBrightness() + (LIGHT_THEME ? -0.2 : 0.2)))
                    .thumbColor(THEME_COLOR)
                    .thumbOpenColor(SLIDER_THUMB_COLOR)
                    .thumbHoverColor(Toolkit.adjustBrightness(THEME_COLOR, THEME_COLOR.getBrightness() + (LIGHT_THEME ? -0.2 : 0.2)))
                    .thumbPressedColor(Toolkit.adjustBrightness(THEME_COLOR, THEME_COLOR.getBrightness() + (LIGHT_THEME ? -0.4 : 0.4)))
                    .thumbOpenHoverColor(Toolkit.adjustBrightness(SLIDER_THUMB_COLOR, SLIDER_THUMB_COLOR.getBrightness() + (LIGHT_THEME ? -0.2 : 0.2)))
                    .thumbOpenPressedColor(Toolkit.adjustBrightness(SLIDER_THUMB_COLOR, SLIDER_THUMB_COLOR.getBrightness() + (LIGHT_THEME ? -0.4 : 0.4)))
                    .borderRadius(0.5)
                    .borderColor(THEME_COLOR)
                    .init();
        }

        public static Label createLabel(String text) {
            Label label = new Label(text);
            label.setFont(FONT_NORMAL);
            label.setTextFill(TEXT_COLOR);
            return label;
        }

        public static void setAsDecimalField(SmoothTextField textField, boolean[] isUpdating, SmoothSlider slider) {
            textField.getTextField().textProperty().addListener((o, ov, nv) -> {
                if (!isUpdating[0]) {

                    isUpdating[0] = true;
                    switch (checkDecimal(nv)) {
                        case 0 -> {
                            double value = Math.max(slider.getMin(), Math.min(slider.getMax(), Double.parseDouble(nv)));
                            slider.setValue(value);
                            if (value != Double.parseDouble(nv)) textField.getTextField().setText(numberFormat.format(value));
                        }
                        case 3 -> {
                            textField.getTextField().setText(ov);
                            defaultToolkit.beep();
                        }
                    }
                    isUpdating[0] = false;
                }
            });

            textField.getTextField().focusedProperty().addListener((o, ov, nv) -> {
                String text = textField.getTextField().getText();
                switch (checkDecimal(text)) {
                    case 1 -> {
                        textField.getTextField().setText(text.substring(0, text.length() - 1));
                        slider.setValue(Double.parseDouble(text + "0"));
                    }
                    case 2, 3 -> {
                        slider.setValue(1.0);
                        textField.getTextField().setText("1");
                    }
                }
            });
        }

        public static void setAsIntegerField(SmoothTextField textField, boolean[] isUpdating, SmoothSlider slider) {
            textField.getTextField().textProperty().addListener((o, ov, nv) -> {
                if (!isUpdating[0]) {

                    isUpdating[0] = true;
                    switch (checkInteger(nv)) {
                        case 0 -> {
                            int value = (int) Math.max(slider.getMin(), Math.min(slider.getMax(), Integer.parseInt(nv)));
                            slider.setValue(value);
                            if (value != Integer.parseInt(nv)) textField.getTextField().setText(String.valueOf(value));

                        }
                        case 2 -> {
                            textField.getTextField().setText(ov);
                            defaultToolkit.beep();
                        }
                    }
                    isUpdating[0] = false;
                }
            });
            textField.getTextField().focusedProperty().addListener((o, ov, nv) -> {
                String text = textField.getTextField().getText();
                switch (checkInteger(text)) {
                    case 1, 2 -> {
                        textField.getTextField().setText("1");
                        slider.setValue(1.0);
                    }
                }
            });
        }
    }

    private static void collectConfigPart(AutoPane root, AutoPane... children) {
        double top = 0;

        for (AutoPane child : children) {
            root.setPosition(child, false, 0, 0, top, top + fontSize * 1.4);
            root.flipRelativeMode(child, AutoPane.Motion.BOTTOM);
            root.getChildren().add(child);
            top += fontSize * 1.4 + spacing;
        }
    }

    private static double getConfigPartHeight(AutoPane pane) {
        int childCount = pane.getChildren().size();

        return (childCount + 1) * (fontSize * 1.4 + spacing);
    }

    private static void typeConfigPartVertical(AutoPane root, AutoPane... children) {
        double startY = 0;
        for (AutoPane config : children) {
            root.setPosition(config, false, 0, 0, startY, startY + getConfigPartHeight(config));
            root.flipRelativeMode(config, AutoPane.Motion.BOTTOM, true);
            root.getChildren().add(config);
            startY += getConfigPartHeight(config);
        }
        root.setMaxHeight(startY);
        root.setMinHeight(startY);
    }

    private static void typeConfigPartTile(AutoPane root, AutoPane... children) {
        AutoPane boxA = new AutoPane();
        AutoPane boxB = new AutoPane();

        typeConfigPartVertical(boxA, children[0], children[1]);
        typeConfigPartVertical(boxB, children[2], children[3]);

        double height = Math.max(boxA.getMaxHeight(), boxB.getMaxHeight());

        root.setMaxHeight(height);
        root.setMinHeight(height);

        root.setPosition(boxA, true , 0, 0.515, 0, 0);
        root.setPosition(boxB, true , 0.515, 0, 0, 0);

        root.getChildren().addAll(boxA, boxB);
    }
}

class ThemedCellStyle implements CellStyle {

    private final Image fileIcon = new Image(LIGHT_THEME ? "textures/file_light.png" : "textures/file_dark.png");
    private final Image folderIcon = new Image(LIGHT_THEME ? "textures/folder_light.png" : "textures/folder_dark.png");
    private final Image rootIcon = new Image(LIGHT_THEME ? "textures/root_light.png" : "textures/root_dark.png");

    @Override
    public Cell createFileCell(String name, SmoothTreeView treeView) {
        DataCell<SimpleStringProperty> fileCell = new DataCell<SimpleStringProperty>(20, 15, fileIcon, " " + name)
                .bgColor(MODULE_BG_TRANSPERTANT_COLOR)
                .bgFocusColor(BUTTON_FOCUS_COLOR)
                .fillColor(BUTTON_PRESSED_COLOR)
                .textColor(TEXT_COLOR)
                .textFocusColor(BUTTON_TEXT_FOCUS_COLOR)
                .animeDuration(BUTTON_ANIME_DURATION)
                .borderShape(10)
                .init();

        SimpleStringProperty fileProperty = new SimpleStringProperty();
        fileCell.setData(fileProperty);
        fileCell.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            for (DiffusionButton button : treeView.getGroup().getAllButtons()) {
                if (button instanceof DataCell<?> dataCell) {
                    if (dataCell.getData() instanceof SimpleStringProperty property) {
                        property.unbind();
                    }
                }
            }
            FrameFactory.MAIN_PRESENT_AREA.setText(fileCell.getData().get());
            fileCell.getData().bind(FrameFactory.MAIN_PRESENT_AREA.textProperty());
        });
        fileCell.setType(true);
        return fileCell;
    }

    @Override
    public Cell createFolderCell(String name, SmoothTreeView treeView) {
        Cell cell = new Cell(20, 15, folderIcon, " " + name)
                .bgColor(MODULE_BG_TRANSPERTANT_COLOR)
                .bgFocusColor(BUTTON_FOCUS_COLOR)
                .fillColor(BUTTON_PRESSED_COLOR)
                .textColor(TEXT_COLOR)
                .textFocusColor(BUTTON_TEXT_FOCUS_COLOR)
                .animeDuration(BUTTON_ANIME_DURATION)
                .borderShape(10)
                .init();
        cell.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            for (DiffusionButton button : treeView.getGroup().getAllButtons()) {
                if (button instanceof DataCell<?> dataCell) {
                    if (dataCell.getData() instanceof SimpleStringProperty property) {
                        property.unbind();
                    }
                }
            }
            FrameFactory.MAIN_PRESENT_AREA.clear();
        });
        return cell;
    }

    @Override
    public Cell createRootCell(String name, SmoothTreeView treeView) {
        Cell cell = new Cell(20, 15, rootIcon, " " + name)
                .bgColor(MODULE_BG_TRANSPERTANT_COLOR)
                .bgFocusColor(BUTTON_FOCUS_COLOR)
                .fillColor(BUTTON_PRESSED_COLOR)
                .textColor(TEXT_COLOR)
                .textFocusColor(BUTTON_TEXT_FOCUS_COLOR)
                .animeDuration(BUTTON_ANIME_DURATION)
                .borderShape(10)
                .init();
        cell.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            for (DiffusionButton button : treeView.getGroup().getAllButtons()) {
                if (button instanceof DataCell<?> dataCell) {
                    if (dataCell.getData() instanceof SimpleStringProperty property) {
                        property.unbind();
                    }
                }
            }
            FrameFactory.MAIN_PRESENT_AREA.clear();
        });
        return cell;
    }

}

class ThemedButtonStyle implements ButtonStyle {

    @Override
    public DiffusionButton createButton(String name) {
        return new DiffusionButton()
                .name(name)
                .height(30)
                //.borderShape(10)
                //.borderColor(THEME_COLOR)
                .bgColor(MODULE_BG_COLOR)
                .bgFocusColor(BUTTON_FOCUS_COLOR)
                .fillColor(BUTTON_PRESSED_COLOR)
                .textColor(TEXT_COLOR)
                .textFocusColor(BUTTON_TEXT_FOCUS_COLOR)
                //.borderRadius(0.5)
                .font(FONT_NORMAL)
                .animeDuration(BUTTON_ANIME_DURATION)
                .init();
    }
}

class ThemedNamePopupStyle implements NamePopupStyle {

    @Override
    public SmoothTextField createTextField() {
        SmoothTextField textField = new SmoothTextField()
                .borderShape(10)
                .borderColor(THEME_COLOR)
                .bgColor(MODULE_BG_COLOR)
                .borderRadius(0.5)
                .font(FONT_SMALLER)
                .textColor(TEXT_COLOR)
                .subLineColor(TEXT_COLOR)
                .buttonStyle(new ThemedButtonStyle())
                .init();
        textField.getTextField().setPromptText("名称");
        return textField;
    }

    @Override
    public Label createDescription(String description) {
        Label label = new Label(description);
        label.setFont(FONT_NORMAL);
        label.setTextFill(TEXT_COLOR);
        return label;
    }

    @Override
    public Rectangle createBackground(double width, double height) {
        Rectangle rectangle = new Rectangle(width, height);
        rectangle.setFill(Toolkit.adjustOpacity(MODULE_BG_COLOR, 0.5));
        return rectangle;
    }
}