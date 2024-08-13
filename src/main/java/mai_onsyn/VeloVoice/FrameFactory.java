package mai_onsyn.VeloVoice;

import com.ibm.icu.impl.locale.XCldrStub;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.effect.Blend;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import mai_onsyn.AnimeFX.Frame.Layout.AutoPane;
import mai_onsyn.AnimeFX.Frame.Module.*;
import mai_onsyn.AnimeFX.Frame.Styles.ButtonStyle;
import mai_onsyn.AnimeFX.Frame.Module.Assistant.Cell;
import mai_onsyn.AnimeFX.Frame.Module.Assistant.DataCell;
import mai_onsyn.AnimeFX.Frame.Styles.CellStyle;
import mai_onsyn.AnimeFX.Frame.Styles.NamePopupStyle;
import mai_onsyn.AnimeFX.Frame.Utils.Toolkit;
import mai_onsyn.VeloVoice.App.Runtime;
import mai_onsyn.VeloVoice.NetWork.Crawler.Websites.WenKu8;
import mai_onsyn.VeloVoice.NetWork.TTS.EdgeTTSClient;
import mai_onsyn.VeloVoice.NetWork.Voice;
import mai_onsyn.VeloVoice.Text.TTS;
import mai_onsyn.VeloVoice.Text.TextFactory;
import mai_onsyn.VeloVoice.Text.TextLoader;
import mai_onsyn.VeloVoice.Utils.Structure;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

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
            Label typeLabel = new Label("加载模式");
            typeLabel.setFont(FONT_NORMAL);
            typeLabel.setTextFill(TEXT_COLOR);

            List<DiffusionButton> choices = new ArrayList<>();
            List<LoadType> loadTypes = List.of(
                    LoadType.LOCAL_FULL,
                    LoadType.LOCAL_VOLUMED,
                    LoadType.WENKU8
            );
            List<String> choiceButtonNames = List.of(
                    "本地(全集)",
                    "本地(分卷)",
                    "轻小说文库"
            );
            SmoothChoiceBox choiceBox = new SmoothChoiceBox(25)
                    .font(FONT_NORMAL)
                    .popupAnimeDuration(100)
                    .borderShape(10)
                    .popupBorderShape(10)
                    .popupMaxHeight(300)
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
            choiceBox.setText(choiceButtonNames.getFirst());
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
                choiceButton.setOnMouseClicked(event -> {
                    loadType = loadTypes.get(I);
                    choiceBox.setText(choiceButtonNames.get(I));
                });
                choices.add(choiceButton);
            }
            choiceBox.addItem(choices.toArray(new DiffusionButton[0]));

            loadBox.setPosition(typeLabel, AutoPane.AlignmentMode.LEFT_CENT, AutoPane.LocateMode.RELATIVE, 0, 0.5);
            loadBox.setPosition(choiceBox, false, fontSize * 4, 0, 0, 0);

            loadBox.getChildren().addAll(typeLabel, choiceBox);
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

        SmoothTextField inputField = new SmoothTextField()
                .borderShape(10)
                .borderColor(THEME_COLOR)
                .bgColor(MODULE_BG_COLOR)
                .borderRadius(0.5)
                .font(FONT_SMALLER)
                .textColor(TEXT_COLOR)
                .buttonStyle(buttonStyle)
                .subLineColor(TEXT_COLOR)
                .init();
        inputField.getTextField().setPromptText("txt所在文件夹/目录地址");
        //inputField.getTextField().setText("D:\\Users\\Desktop\\Test\\FullModeTest");


        AutoPane operationPane = new AutoPane();
        {
            DiffusionButton saveTreeButton = new DiffusionButton()
                    .name("保存文本结构")
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


            DiffusionButton loadButton = new DiffusionButton()
                    .name("加载")
                    .height(fontSize * 1.4)
                    .width(fontSize * 3)
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

            loadButton.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY) {
                    Thread.ofVirtual().name("Load-Thread").start(() -> {
                        String urlString = inputField.getTextField().getText();
                        if (urlString.isEmpty()) {
                            logger.prompt("您想凭空创造吗？");
                            return;
                        }
                        logger.prompt("尝试加载 - " + urlString);

                        Structure<List<String>> loadStructure;

                        try {
                            switch (loadType) {
                                case LOCAL_FULL -> {
                                    File targetFolder = new File(urlString);
                                    if (!targetFolder.exists()) {
                                        logger.error("指定文件夹不存在 - " + urlString);
                                        return;
                                    }
                                    File[] series = targetFolder.listFiles((dir, name) -> name.endsWith(".txt"));
                                    if (series == null || series.length == 0) {
                                        logger.error("指定文件夹内没有可读取的txt文件");
                                        return;
                                    }
                                    loadStructure = new Structure<>("Root");
                                    for (File file : series) {
                                        String fileName = file.getName();
                                        Structure<List<String>> seriesStructure = Structure.Factory.of(TextFactory.parseFromSeries(TextLoader.load(file)));
                                        seriesStructure.setName(fileName.substring(0, fileName.lastIndexOf(".")));
                                        loadStructure.getChildren().add(seriesStructure);
                                        logger.prompt("已加载 - " + seriesStructure.getName());
                                    }
                                    Structure.Factory.writeToTreeView(loadStructure, treeView, true);
                                }
                                case LOCAL_VOLUMED -> {
                                    File targetFolder = new File(urlString);
                                    if (!targetFolder.exists()) {
                                        logger.error("指定文件夹不存在 - " + urlString);
                                        return;
                                    }
                                    File[] volumes = targetFolder.listFiles((dir, name) -> name.endsWith(".txt"));
                                    if (volumes == null || volumes.length == 0) {
                                        logger.error("指定文件夹内没有可读取的txt文件");
                                        return;
                                    }
                                    loadStructure = new Structure<>("Root");
                                    for (File file : volumes) {
                                        Structure<List<String>> volumesStructure = Structure.Factory.of(TextFactory.parseFromVolume(TextLoader.load(file)));
                                        loadStructure.getChildren().add(volumesStructure);
                                        logger.prompt("已加载 - " + volumesStructure.getName());
                                    }
                                    //System.out.println(loadStructure);
                                    Structure.Factory.writeToTreeView(loadStructure, treeView, true);

                                }
                                case WENKU8 -> {
                                    if (urlString.contains("https://www.wenku8.net/book/") || urlString.contains("https://www.wenku8.net/novel/")) {
                                        loadStructure = new WenKu8(urlString).getContents();
                                        logger.prompt("已加载 - " + loadStructure.getName());
                                        Structure.Factory.writeToTreeView(loadStructure, treeView, false);
                                    }
                                    else logger.error(String.format("加载失败 - \"%s\" 不是正确的轻小说文库小说地址", urlString));
                                }
                            }
                        } catch (Exception e) {
                            if (e instanceof NoSuchElementException) logger.error("加载失败，不支持的格式 - " + e);
                            else logger.error("加载失败 - " + e);
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
                            if (structure.getChildren().size() == 1) Structure.Factory.saveToFile(structure.getChildren().getFirst(), dir, false, 0);
                            else {
                                for (int i = 0; i < structure.getChildren().size(); i++) {
                                    Structure.Factory.saveToFile(structure.getChildren().get(i), dir, isAppendOrdinal, i + 1);
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
            Label modelLabel = new Label("模型");
            modelLabel.setFont(FONT_NORMAL);
            modelLabel.setTextFill(TEXT_COLOR);

            SmoothChoiceBox voiceChoiceBox = new SmoothChoiceBox(25)
                    .font(FONT_SMALLER)
                    .popupAnimeDuration(100)
                    .borderShape(10)
                    .popupBorderShape(10)
                    .popupMaxHeight(600)
                    .borderColor(THEME_COLOR)
                    .bgColor(MODULE_BG_COLOR)
                    .bgFocusColor(BUTTON_FOCUS_COLOR)
                    .fillColor(BUTTON_PRESSED_COLOR)
                    .textColor(TEXT_COLOR)
                    .textFocusColor(BUTTON_TEXT_FOCUS_COLOR)
                    .borderRadius(0.5)
                    .height(fontSize * 1.4)
                    .width(fontSize * 12)
                    .animeDuration(BUTTON_ANIME_DURATION)
                    .init();
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


            modelBox.setPosition(modelLabel, AutoPane.AlignmentMode.LEFT_CENT, AutoPane.LocateMode.RELATIVE, 0, 0.5);
            modelBox.setPosition(voiceChoiceBox, false, fontSize * 2.5, 0, 0, fontSize * 1.4);
            modelBox.flipRelativeMode(voiceChoiceBox, AutoPane.Motion.BOTTOM);

            modelBox.getChildren().addAll(modelLabel, voiceChoiceBox);
        }

        AutoPane rateBox = new AutoPane();
        {
            Label label = new Label("语速");
            label.setFont(FONT_NORMAL);
            label.setTextFill(TEXT_COLOR);

            Circle thumb = new Circle(10);
            thumb.setStroke(THEME_COLOR);
            thumb.setStrokeWidth(0.5);
            SmoothSlider slider = new SmoothSlider(0.05, 2, 1.75)
                    .thumb(thumb)
                    .trackColor(SLIDER_BG_COLOR)
                    .trackProgressColor(TRANSPERTANT_THEME_COLOR)
                    .thumbColor(SLIDER_THUMB_COLOR)
                    .hoverColor(SLIDER_THUMB_HOVER_COLOR)
                    .pressedColor(SLIDER_THUMB_PRESSED_COLOR)
                    .init();
            SmoothTextField textField = new SmoothTextField()
                    .borderShape(10)
                    .borderColor(THEME_COLOR)
                    .bgColor(MODULE_BG_COLOR)
                    .borderRadius(0.5)
                    .font(FONT_SMALLER)
                    .textColor(TEXT_COLOR)
                    .buttonStyle(buttonStyle)
                    .subLineColor(TEXT_COLOR)
                    .init();
            textField.getTextField().setText("1.75");
            EdgeTTSClient.setVoiceRate(1.75);

            rateBox.setPosition(label, AutoPane.AlignmentMode.LEFT_CENT, AutoPane.LocateMode.RELATIVE, 0, 0.5);
            rateBox.setPosition(textField, false, 40, 0, 0, 0);
            rateBox.flipRelativeMode(textField, AutoPane.Motion.LEFT);

            Rectangle sliderClip = new Rectangle(100, 50);
            slider.setClip(sliderClip);
            sliderClip.setLayoutX(10);
            sliderClip.setLayoutY(12);
            sliderClip.setHeight(40);
            rateBox.setPosition(slider, false, fontSize * 2.5 - 25, 30, -15, 0);
            slider.widthProperty().addListener((o, ov, nv) -> sliderClip.setWidth(nv.doubleValue() - 20));
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

            textField.getTextField().textProperty().addListener((o, ov, nv) -> {
                if (!isUpdating[0]) {

                    isUpdating[0] = true;
                    switch (checkDecimal(nv)) {
                        case 0 -> {
                            double value = Math.max(0, Math.min(slider.getMax(), Double.parseDouble(nv)));
                            slider.setValue(value);
                            if (value !=Double.parseDouble(nv)) {
                                textField.getTextField().setText(String.valueOf(value));
                            }
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
                        textField.getTextField().setText(text + "0");
                        slider.setValue(Double.parseDouble(text + "0"));
                    }
                    case 2, 3 -> slider.setValue(1.5);
                }
            });

            rateBox.getChildren().addAll(label, slider, textField);
        }

        AutoPane volumeBox = new AutoPane();
        {
            Label label = new Label("音量");
            label.setFont(FONT_NORMAL);
            label.setTextFill(TEXT_COLOR);

            Circle thumb = new Circle(10);
            thumb.setStroke(THEME_COLOR);
            thumb.setStrokeWidth(0.5);
            SmoothSlider slider = new SmoothSlider(0.05, 1.5, 1)
                    .thumb(thumb)
                    .trackColor(SLIDER_BG_COLOR)
                    .trackProgressColor(TRANSPERTANT_THEME_COLOR)
                    .thumbColor(SLIDER_THUMB_COLOR)
                    .hoverColor(SLIDER_THUMB_HOVER_COLOR)
                    .pressedColor(SLIDER_THUMB_PRESSED_COLOR)
                    .init();
            SmoothTextField textField = new SmoothTextField()
                    .borderShape(10)
                    .borderColor(THEME_COLOR)
                    .bgColor(MODULE_BG_COLOR)
                    .borderRadius(0.5)
                    .font(FONT_SMALLER)
                    .textColor(TEXT_COLOR)
                    .buttonStyle(buttonStyle)
                    .subLineColor(TEXT_COLOR)
                    .init();
            textField.getTextField().setText("1");

            volumeBox.setPosition(label, AutoPane.AlignmentMode.LEFT_CENT, AutoPane.LocateMode.RELATIVE, 0, 0.5);
            volumeBox.setPosition(textField, false, 40, 0, 0, 0);
            volumeBox.flipRelativeMode(textField, AutoPane.Motion.LEFT);

            Rectangle sliderClip = new Rectangle(100, 50);
            slider.setClip(sliderClip);
            sliderClip.setLayoutX(10);
            sliderClip.setLayoutY(12);
            sliderClip.setHeight(40);
            volumeBox.setPosition(slider, false, fontSize * 2.5 - 25, 30, -15, 0);
            slider.widthProperty().addListener((o, ov, nv) -> sliderClip.setWidth(nv.doubleValue() - 20));
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

            textField.getTextField().textProperty().addListener((o, ov, nv) -> {
                if (!isUpdating[0]) {

                    isUpdating[0] = true;
                    switch (checkDecimal(nv)) {
                        case 0 -> {
                            double value = Math.max(0, Math.min(slider.getMax(), Double.parseDouble(nv)));
                            slider.setValue(value);
                            if (value !=Double.parseDouble(nv)) {
                                textField.getTextField().setText(String.valueOf(value));
                            }
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
                        textField.getTextField().setText(text + "0");
                        slider.setValue(Double.parseDouble(text + "0"));
                    }
                    case 2, 3 -> slider.setValue(1);
                }
            });

            volumeBox.getChildren().addAll(label, slider, textField);
        }

        AutoPane pitchBox = new AutoPane();
        {
            Label label = new Label("音调");
            label.setFont(FONT_NORMAL);
            label.setTextFill(TEXT_COLOR);

            Circle thumb = new Circle(10);
            thumb.setStroke(THEME_COLOR);
            thumb.setStrokeWidth(0.5);
            SmoothSlider slider = new SmoothSlider(0.05, 2, 1)
                    .thumb(thumb)
                    .trackColor(SLIDER_BG_COLOR)
                    .trackProgressColor(TRANSPERTANT_THEME_COLOR)
                    .thumbColor(SLIDER_THUMB_COLOR)
                    .hoverColor(SLIDER_THUMB_HOVER_COLOR)
                    .pressedColor(SLIDER_THUMB_PRESSED_COLOR)
                    .init();
            SmoothTextField textField = new SmoothTextField()
                    .borderShape(10)
                    .borderColor(THEME_COLOR)
                    .bgColor(MODULE_BG_COLOR)
                    .borderRadius(0.5)
                    .font(FONT_SMALLER)
                    .textColor(TEXT_COLOR)
                    .buttonStyle(buttonStyle)
                    .subLineColor(TEXT_COLOR)
                    .init();
            textField.getTextField().setText("1");

            pitchBox.setPosition(label, AutoPane.AlignmentMode.LEFT_CENT, AutoPane.LocateMode.RELATIVE, 0, 0.5);
            pitchBox.setPosition(textField, false, 40, 0, 0, 0);
            pitchBox.flipRelativeMode(textField, AutoPane.Motion.LEFT);

            Rectangle sliderClip = new Rectangle(100, 50);
            slider.setClip(sliderClip);
            sliderClip.setLayoutX(10);
            sliderClip.setLayoutY(12);
            sliderClip.setHeight(40);
            pitchBox.setPosition(slider, false, fontSize * 2.5 - 25, 30, -15, 0);
            slider.widthProperty().addListener((o, ov, nv) -> sliderClip.setWidth(nv.doubleValue() - 20));
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

            textField.getTextField().textProperty().addListener((o, ov, nv) -> {
                if (!isUpdating[0]) {

                    isUpdating[0] = true;
                    switch (checkDecimal(nv)) {
                        case 0 -> {
                            double value = Math.max(0, Math.min(slider.getMax(), Double.parseDouble(nv)));
                            slider.setValue(value);
                            if (value !=Double.parseDouble(nv)) {
                                textField.getTextField().setText(String.valueOf(value));
                            }
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
                        textField.getTextField().setText(text + "0");
                        slider.setValue(Double.parseDouble(text + "0"));
                    }
                    case 2, 3 -> slider.setValue(1);
                }
            });

            pitchBox.getChildren().addAll(label, slider, textField);
        }

        AutoPane saveBox = new AutoPane();
        {
            Label saveLabel = new Label("输出路径");
            saveLabel.setFont(FONT_NORMAL);
            saveLabel.setTextFill(TEXT_COLOR);

            SmoothTextField textField = new SmoothTextField()
                    .borderShape(10)
                    .borderColor(THEME_COLOR)
                    .bgColor(MODULE_BG_COLOR)
                    .borderRadius(0.5)
                    .font(FONT_SMALLER)
                    .textColor(TEXT_COLOR)
                    .buttonStyle(buttonStyle)
                    .subLineColor(TEXT_COLOR)
                    .init();
            textField.getTextField().setPromptText("音频输出文件夹");
            //textField.getTextField().setText("D:\\Users\\Desktop\\Test\\VoiceTest");

            saveBox.setPosition(saveLabel, AutoPane.AlignmentMode.LEFT_CENT, AutoPane.LocateMode.RELATIVE, 0, 0.5);
            saveBox.setPosition(textField, false, fontSize * 4, 0, 0, 0);

            saveBox.getChildren().addAll(saveLabel, textField);
        }

        AutoPane operationBox = new AutoPane();
        {
            DiffusionButton startButton = new DiffusionButton()
                    .name("开始")
                    .height(fontSize * 1.4)
                    .width(fontSize * 5)
                    .borderShape(40)
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

            DiffusionButton pauseButton = new DiffusionButton()
                    .name("暂停")
                    .height(fontSize * 1.4)
                    .width(fontSize * 5)
                    .borderShape(40)
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

            DiffusionButton stopButton = new DiffusionButton()
                    .name("终止")
                    .height(fontSize * 1.4)
                    .width(fontSize * 5)
                    .borderShape(40)
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
                    logger.prompt(String.format("完成！耗时 %s", formatDuration(System.currentTimeMillis() - startTime)));
                    progressPane.setVisible(false);
                    runningState = false;
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

    public static DiffusionButton getSettingsButton() {
        DiffusionButton settingsButton = new DiffusionButton()
                .ftImage(new Image(LIGHT_THEME ? "textures/setting_light.png" : "textures/setting_dark.png"))
                .borderShape(10)
                .bgColor(MODULE_BG_TRANSPERTANT_COLOR)
                .bgFocusColor(BUTTON_FOCUS_COLOR)
                .fillColor(BUTTON_PRESSED_COLOR)
                .textColor(TEXT_COLOR)
                .textFocusColor(BUTTON_TEXT_FOCUS_COLOR)
                .ftImageRatio(0.7)
                .animeDuration(BUTTON_ANIME_DURATION)
                .init();

        Stage settingsWindow = getSettingsWindow();
        Platform.runLater(() -> settingsButton.getScene().getWindow().setOnCloseRequest(_ -> {
            if (settingsWindow.isShowing()) settingsWindow.close();
        }));

        settingsButton.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                if (!settingsWindow.isShowing()) {
                    settingsWindow.show();
                }
                else {
                    settingsWindow.toFront();
                    //Toolkit.getDefaultToolkit().beep();
                }
            }
        });

        return settingsButton;
    }

    private static Stage getSettingsWindow() {
        Stage stage = new Stage();

        AutoPane root = new AutoPane();
        root.setOnMousePressed(_ -> root.requestFocus());
        stage.setOnShown(_ -> root.requestFocus());
        Scene scene = new Scene(root);
        stage.setScene(scene);

        drawSettingsWindow(root);

        root.setPrefSize(800, 600);
        stage.setMinWidth(400);
        stage.setMinHeight(300);
        stage.setTitle("设置");
        stage.getIcons().add(new Image("textures/icon.png"));

        return stage;
    }

    private static void drawSettingsWindow(AutoPane root) {
        AutoPane windowConfig = new AutoPane();
        {
            AutoPane titleBox = new AutoPane();
            {
                Label head = new Label("窗口");
                head.setFont(FONT_NORMAL);
                head.setTextFill(TEXT_COLOR);

                titleBox.setPosition(head, AutoPane.AlignmentMode.CENTER, AutoPane.LocateMode.RELATIVE, 0.5, 0.5);
                titleBox.getChildren().addAll(head);
            }

            AutoPane themeBox = new AutoPane();
            {
                Label label = new Label("主题");
                label.setFont(FONT_NORMAL);
                label.setTextFill(TEXT_COLOR);


                SmoothSwitch switchButton = new SmoothSwitch()
                        .width(fontSize * 2)
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

                Label valueLabel = new Label(LIGHT_THEME ? "Light" : "Dark♂");
                valueLabel.setFont(FONT_NORMAL);
                valueLabel.setTextFill(TEXT_COLOR);

                switchButton.stateProperty().addListener((o, ov, nv) -> {
                    LIGHT_THEME = nv;
                    valueLabel.setText(LIGHT_THEME ? "Light" : "Dark♂");
                });


                themeBox.setPosition(label, AutoPane.AlignmentMode.LEFT_CENT, AutoPane.LocateMode.RELATIVE, 0, 0.5);
                themeBox.setPosition(switchButton, AutoPane.AlignmentMode.LEFT_CENT, AutoPane.LocateMode.ABSOLUTE, fontSize * 6, fontSize * 0.7);
                themeBox.setPosition(valueLabel, AutoPane.AlignmentMode.LEFT_CENT, AutoPane.LocateMode.ABSOLUTE, fontSize * 8.5, fontSize * 0.7);

                themeBox.getChildren().addAll(label, switchButton, valueLabel);
            }

            AutoPane colorBox = new AutoPane();
            {
                Label label = new Label("主题色");
                label.setFont(FONT_NORMAL);
                label.setTextFill(TEXT_COLOR);

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
                Label label = new Label("背景图片");
                label.setFont(FONT_NORMAL);
                label.setTextFill(TEXT_COLOR);

                SmoothTextField textField = new SmoothTextField()
                        .borderShape(10)
                        .borderColor(THEME_COLOR)
                        .bgColor(MODULE_BG_COLOR)
                        .borderRadius(0.5)
                        .font(FONT_SMALLER)
                        .textColor(TEXT_COLOR)
                        .buttonStyle(buttonStyle)
                        .subLineColor(TEXT_COLOR)
                        .init();
                textField.getTextField().setPromptText("图片url");

                textField.getTextField().textProperty().addListener((o, ov, nv) -> {
                    try {
                        BACKGROUND_IMAGE = Toolkit.loadImage(nv);
                        BACKGROUND_IMAGE_URI = nv;

                        Blend effect = Toolkit.getColorBlend(LIGHT_THEME ? LIGHT_THEME_COLOR : DARK_THEME_COLOR, BACKGROUND_BRIGHTNESS);
                        effect.setBottomInput(new GaussianBlur(BACKGROUND_AMBIGUITY));
                        Toolkit.addBackGroundImage(FrameApp.rootPane, BACKGROUND_IMAGE);
                        Toolkit.setBackGroundImageEffect(FrameApp.rootPane, effect);
                        Toolkit.addBackGroundImage(root, BACKGROUND_IMAGE);
                        Toolkit.setBackGroundImageEffect(root, effect);
                    } catch (IOException _) {}
                });
                textField.getTextField().setText(BACKGROUND_IMAGE_URI);

                bgImageBox.setPosition(label, AutoPane.AlignmentMode.LEFT_CENT, AutoPane.LocateMode.RELATIVE, 0, 0.5);
                bgImageBox.setPosition(textField, false, fontSize * 6, 0, 0, 0);

                bgImageBox.getChildren().addAll(label, textField);
            }

            AutoPane bgImageAmbiguityBox = new AutoPane();
            {
                Label label = new Label("背景模糊度");
                label.setFont(FONT_NORMAL);
                label.setTextFill(TEXT_COLOR);

                Circle thumb = new Circle(10);
                thumb.setStroke(THEME_COLOR);
                thumb.setStrokeWidth(0.5);
                SmoothSlider slider = new SmoothSlider(0, 60, BACKGROUND_AMBIGUITY)
                        .thumb(thumb)
                        .trackColor(SLIDER_BG_COLOR)
                        .trackProgressColor(TRANSPERTANT_THEME_COLOR)
                        .thumbColor(SLIDER_THUMB_COLOR)
                        .hoverColor(SLIDER_THUMB_HOVER_COLOR)
                        .pressedColor(SLIDER_THUMB_PRESSED_COLOR)
                        .init();
                SmoothTextField textField = new SmoothTextField()
                        .borderShape(10)
                        .borderColor(THEME_COLOR)
                        .bgColor(MODULE_BG_COLOR)
                        .borderRadius(0.5)
                        .font(FONT_SMALLER)
                        .textColor(TEXT_COLOR)
                        .buttonStyle(buttonStyle)
                        .subLineColor(TEXT_COLOR)
                        .init();
                textField.getTextField().setText(String.valueOf((int) BACKGROUND_AMBIGUITY));

                bgImageAmbiguityBox.setPosition(label, AutoPane.AlignmentMode.LEFT_CENT, AutoPane.LocateMode.RELATIVE, 0, 0.5);
                bgImageAmbiguityBox.setPosition(textField, false, 40, 0, 0, 0);
                bgImageAmbiguityBox.flipRelativeMode(textField, AutoPane.Motion.LEFT);

                Rectangle sliderClip = new Rectangle(100, 50);
                slider.setClip(sliderClip);
                sliderClip.setLayoutX(10);
                sliderClip.setLayoutY(12);
                sliderClip.setHeight(40);
                bgImageAmbiguityBox.setPosition(slider, false, fontSize * 6 - 25, 30, -15, 0);
                slider.widthProperty().addListener((o, ov, nv) -> sliderClip.setWidth(nv.doubleValue() - 20));
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

                textField.getTextField().textProperty().addListener((o, ov, nv) -> {
                    if (!isUpdating[0]) {

                        isUpdating[0] = true;
                        switch (checkDecimal(nv)) {
                            case 0 -> {
                                double value = Math.max(0, Math.min(slider.getMax(), Double.parseDouble(nv)));
                                slider.setValue(value);
                                if (value !=Double.parseDouble(nv)) {
                                    textField.getTextField().setText(String.valueOf((int) value));
                                }
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
                            textField.getTextField().setText(text.substring(0, text.length() - 2));
                            slider.setValue(Double.parseDouble(text + "0"));
                        }
                        case 2, 3 -> {
                            textField.getTextField().setText(String.valueOf((int) BACKGROUND_AMBIGUITY));
                            slider.setValue(BACKGROUND_AMBIGUITY);
                        }
                    }
                });

                bgImageAmbiguityBox.getChildren().addAll(label, slider, textField);
            }

            AutoPane bgImageBrightnessBox = new AutoPane();
            {
                Label label = new Label(LIGHT_THEME ? "背景亮度" : "背景暗度");
                label.setFont(FONT_NORMAL);
                label.setTextFill(TEXT_COLOR);

                Circle thumb = new Circle(10);
                thumb.setStroke(THEME_COLOR);
                thumb.setStrokeWidth(0.5);
                SmoothSlider slider = new SmoothSlider(0, 1, BACKGROUND_BRIGHTNESS)
                        .thumb(thumb)
                        .trackColor(SLIDER_BG_COLOR)
                        .trackProgressColor(TRANSPERTANT_THEME_COLOR)
                        .thumbColor(SLIDER_THUMB_COLOR)
                        .hoverColor(SLIDER_THUMB_HOVER_COLOR)
                        .pressedColor(SLIDER_THUMB_PRESSED_COLOR)
                        .init();
                SmoothTextField textField = new SmoothTextField()
                        .borderShape(10)
                        .borderColor(THEME_COLOR)
                        .bgColor(MODULE_BG_COLOR)
                        .borderRadius(0.5)
                        .font(FONT_SMALLER)
                        .textColor(TEXT_COLOR)
                        .buttonStyle(buttonStyle)
                        .subLineColor(TEXT_COLOR)
                        .init();
                textField.getTextField().setText(numberFormat.format(BACKGROUND_BRIGHTNESS));

                bgImageBrightnessBox.setPosition(label, AutoPane.AlignmentMode.LEFT_CENT, AutoPane.LocateMode.RELATIVE, 0, 0.5);
                bgImageBrightnessBox.setPosition(textField, false, 40, 0, 0, 0);
                bgImageBrightnessBox.flipRelativeMode(textField, AutoPane.Motion.LEFT);

                Rectangle sliderClip = new Rectangle(100, 50);
                slider.setClip(sliderClip);
                sliderClip.setLayoutX(10);
                sliderClip.setLayoutY(12);
                sliderClip.setHeight(40);
                bgImageBrightnessBox.setPosition(slider, false, fontSize * 6 - 25, 30, -15, 0);
                slider.widthProperty().addListener((o, ov, nv) -> sliderClip.setWidth(nv.doubleValue() - 20));
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

                textField.getTextField().textProperty().addListener((o, ov, nv) -> {
                    if (!isUpdating[0]) {

                        isUpdating[0] = true;
                        switch (checkDecimal(nv)) {
                            case 0 -> {
                                double value = Math.max(0, Math.min(slider.getMax(), Double.parseDouble(nv)));
                                slider.setValue(value);
                                if (value !=Double.parseDouble(nv)) {
                                    textField.getTextField().setText(String.valueOf(value));
                                }
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
                            textField.getTextField().setText(text + "0");
                            slider.setValue(Double.parseDouble(text + "0"));
                        }
                        case 2, 3 -> {
                            textField.getTextField().setText(String.valueOf(BACKGROUND_BRIGHTNESS));
                            slider.setValue(BACKGROUND_BRIGHTNESS);
                        }
                    }
                });

                bgImageBrightnessBox.getChildren().addAll(label, slider, textField);
            }


            windowConfig.setPosition(titleBox, false, 0, 0, 0, fontSize * 1.4);
            windowConfig.flipRelativeMode(titleBox, AutoPane.Motion.BOTTOM);

            windowConfig.setPosition(themeBox, false, 0, 0, fontSize * 1.4 + spacing, fontSize * 2.8 + spacing);
            windowConfig.flipRelativeMode(themeBox, AutoPane.Motion.BOTTOM);

            windowConfig.setPosition(colorBox, false, 0, 0, fontSize * 2.8 + spacing * 2, fontSize * 4.2 + spacing * 2);
            windowConfig.flipRelativeMode(colorBox, AutoPane.Motion.BOTTOM);

            windowConfig.setPosition(bgImageBox, false, 0, 0, fontSize * 4.2 + spacing * 3, fontSize * 5.6 + spacing * 3);
            windowConfig.flipRelativeMode(bgImageBox, AutoPane.Motion.BOTTOM);

            windowConfig.setPosition(bgImageAmbiguityBox, false, 0, 0, fontSize * 5.6 + spacing * 4, fontSize * 7 + spacing * 4);
            windowConfig.flipRelativeMode(bgImageAmbiguityBox, AutoPane.Motion.BOTTOM);

            windowConfig.setPosition(bgImageBrightnessBox, false, 0, 0, fontSize * 7 + spacing * 5, fontSize * 8.4 + spacing * 5);
            windowConfig.flipRelativeMode(bgImageBrightnessBox, AutoPane.Motion.BOTTOM);


            titleBox.setStyle("-fx-background-color: #" + Toolkit.colorToString(TRANSPERTANT_THEME_COLOR));
            //themeBox.setStyle("-fx-background-color: #8f00ff10");
            //colorBox.setStyle("-fx-background-color: #8f00ff10");


            windowConfig.getChildren().addAll(titleBox, themeBox, colorBox, bgImageBox, bgImageAmbiguityBox, bgImageBrightnessBox);
        }

        AutoPane networkConfig = new AutoPane();
        {
            AutoPane titleBox = new AutoPane();
            {
                Label head = new Label("网络");
                head.setFont(FONT_NORMAL);
                head.setTextFill(TEXT_COLOR);

                titleBox.setPosition(head, AutoPane.AlignmentMode.CENTER, AutoPane.LocateMode.RELATIVE, 0.5, 0.5);
                titleBox.getChildren().addAll(head);
            }

            AutoPane threadBox = new AutoPane();
            {
                Label label = new Label("连接线程数");
                label.setFont(FONT_NORMAL);
                label.setTextFill(TEXT_COLOR);

                Circle thumb = new Circle(10);
                thumb.setStroke(THEME_COLOR);
                thumb.setStrokeWidth(0.5);
                SmoothSlider slider = new SmoothSlider(1, 128, maxConnectThread)
                        .thumb(thumb)
                        .trackColor(SLIDER_BG_COLOR)
                        .trackProgressColor(TRANSPERTANT_THEME_COLOR)
                        .thumbColor(SLIDER_THUMB_COLOR)
                        .hoverColor(SLIDER_THUMB_HOVER_COLOR)
                        .pressedColor(SLIDER_THUMB_PRESSED_COLOR)
                        .init();
                SmoothTextField textField = new SmoothTextField()
                        .borderShape(10)
                        .borderColor(THEME_COLOR)
                        .bgColor(MODULE_BG_COLOR)
                        .borderRadius(0.5)
                        .font(FONT_SMALLER)
                        .textColor(TEXT_COLOR)
                        .buttonStyle(buttonStyle)
                        .subLineColor(TEXT_COLOR)
                        .init();
                textField.getTextField().setText(String.valueOf(maxConnectThread));

                threadBox.setPosition(label, AutoPane.AlignmentMode.LEFT_CENT, AutoPane.LocateMode.RELATIVE, 0, 0.5);
                threadBox.setPosition(textField, false, 40, 0, 0, 0);
                threadBox.flipRelativeMode(textField, AutoPane.Motion.LEFT);

                Rectangle sliderClip = new Rectangle(100, 50);
                slider.setClip(sliderClip);
                sliderClip.setLayoutX(10);
                sliderClip.setLayoutY(12);
                sliderClip.setHeight(40);
                threadBox.setPosition(slider, false, fontSize * 6 - 25, 30, -15, 0);
                slider.widthProperty().addListener((o, ov, nv) -> sliderClip.setWidth(nv.doubleValue() - 20));
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

                textField.getTextField().textProperty().addListener((o, ov, nv) -> {
                    if (!isUpdating[0]) {

                        isUpdating[0] = true;
                        switch (checkInteger(nv)) {
                            case 0 -> {
                                double value = Math.max(0, Math.min(slider.getMax(), Integer.parseInt(nv)));
                                slider.setValue(value);
                                if (value != Integer.parseInt(nv)) {
                                    textField.getTextField().setText(String.valueOf((int) value));
                                }
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
                            textField.getTextField().setText(String.valueOf(maxConnectThread));
                            slider.setValue(maxConnectThread);
                        }
                    }
                });

                threadBox.getChildren().addAll(label, slider, textField);
            }
            
            AutoPane retryBox = new AutoPane();
            {
                Label label = new Label("重试次数");
                label.setFont(FONT_NORMAL);
                label.setTextFill(TEXT_COLOR);

                Circle thumb = new Circle(10);
                thumb.setStroke(THEME_COLOR);
                thumb.setStrokeWidth(0.5);
                SmoothSlider slider = new SmoothSlider(1, 5, retryCount)
                        .thumb(thumb)
                        .trackColor(SLIDER_BG_COLOR)
                        .trackProgressColor(TRANSPERTANT_THEME_COLOR)
                        .thumbColor(SLIDER_THUMB_COLOR)
                        .hoverColor(SLIDER_THUMB_HOVER_COLOR)
                        .pressedColor(SLIDER_THUMB_PRESSED_COLOR)
                        .init();
                SmoothTextField textField = new SmoothTextField()
                        .borderShape(10)
                        .borderColor(THEME_COLOR)
                        .bgColor(MODULE_BG_COLOR)
                        .borderRadius(0.5)
                        .font(FONT_SMALLER)
                        .textColor(TEXT_COLOR)
                        .buttonStyle(buttonStyle)
                        .subLineColor(TEXT_COLOR)
                        .init();
                textField.getTextField().setText(String.valueOf(retryCount));

                retryBox.setPosition(label, AutoPane.AlignmentMode.LEFT_CENT, AutoPane.LocateMode.RELATIVE, 0, 0.5);
                retryBox.setPosition(textField, false, 40, 0, 0, 0);
                retryBox.flipRelativeMode(textField, AutoPane.Motion.LEFT);

                Rectangle sliderClip = new Rectangle(100, 50);
                slider.setClip(sliderClip);
                sliderClip.setLayoutX(10);
                sliderClip.setLayoutY(12);
                sliderClip.setHeight(40);
                retryBox.setPosition(slider, false, fontSize * 6 - 25, 30, -15, 0);
                slider.widthProperty().addListener((o, ov, nv) -> sliderClip.setWidth(nv.doubleValue() - 20));
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

                textField.getTextField().textProperty().addListener((o, ov, nv) -> {
                    if (!isUpdating[0]) {

                        isUpdating[0] = true;
                        switch (checkInteger(nv)) {
                            case 0 -> {
                                double value = Math.max(0, Math.min(slider.getMax(), Integer.parseInt(nv)));
                                slider.setValue(value);
                                if (value != Integer.parseInt(nv)) {
                                    textField.getTextField().setText(String.valueOf((int) value));
                                }
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
                            textField.getTextField().setText(String.valueOf(retryCount));
                            slider.setValue(retryCount);
                        }
                    }
                });

                retryBox.getChildren().addAll(label, slider, textField);
            }

            AutoPane timeoutBox = new AutoPane();
            {
                Label label = new Label("超时时间(秒)");
                label.setFont(FONT_NORMAL);
                label.setTextFill(TEXT_COLOR);

                Circle thumb = new Circle(10);
                thumb.setStroke(THEME_COLOR);
                thumb.setStrokeWidth(0.5);
                SmoothSlider slider = new SmoothSlider(1, 60, timeoutSeconds)
                        .thumb(thumb)
                        .trackColor(SLIDER_BG_COLOR)
                        .trackProgressColor(TRANSPERTANT_THEME_COLOR)
                        .thumbColor(SLIDER_THUMB_COLOR)
                        .hoverColor(SLIDER_THUMB_HOVER_COLOR)
                        .pressedColor(SLIDER_THUMB_PRESSED_COLOR)
                        .init();
                SmoothTextField textField = new SmoothTextField()
                        .borderShape(10)
                        .borderColor(THEME_COLOR)
                        .bgColor(MODULE_BG_COLOR)
                        .borderRadius(0.5)
                        .font(FONT_SMALLER)
                        .textColor(TEXT_COLOR)
                        .buttonStyle(buttonStyle)
                        .subLineColor(TEXT_COLOR)
                        .init();
                textField.getTextField().setText(String.valueOf(timeoutSeconds));

                timeoutBox.setPosition(label, AutoPane.AlignmentMode.LEFT_CENT, AutoPane.LocateMode.RELATIVE, 0, 0.5);
                timeoutBox.setPosition(textField, false, 40, 0, 0, 0);
                timeoutBox.flipRelativeMode(textField, AutoPane.Motion.LEFT);

                Rectangle sliderClip = new Rectangle(100, 50);
                slider.setClip(sliderClip);
                sliderClip.setLayoutX(10);
                sliderClip.setLayoutY(12);
                sliderClip.setHeight(40);
                timeoutBox.setPosition(slider, false, fontSize * 6 - 25, 30, -15, 0);
                slider.widthProperty().addListener((o, ov, nv) -> sliderClip.setWidth(nv.doubleValue() - 20));
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

                textField.getTextField().textProperty().addListener((o, ov, nv) -> {
                    if (!isUpdating[0]) {

                        isUpdating[0] = true;
                        switch (checkInteger(nv)) {
                            case 0 -> {
                                double value = Math.max(0, Math.min(slider.getMax(), Integer.parseInt(nv)));
                                slider.setValue(value);
                                if (value != Integer.parseInt(nv)) {
                                    textField.getTextField().setText(String.valueOf((int) value));
                                }
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
                            textField.getTextField().setText(String.valueOf(timeoutSeconds));
                            slider.setValue(timeoutSeconds);
                        }
                    }
                });

                timeoutBox.getChildren().addAll(label, slider, textField);
            }

            networkConfig.setPosition(titleBox, false, 0, 0, 0, fontSize * 1.4);
            networkConfig.flipRelativeMode(titleBox, AutoPane.Motion.BOTTOM);

            networkConfig.setPosition(threadBox, false, 0, 0, fontSize * 1.4 + spacing, fontSize * 2.8 + spacing);
            networkConfig.flipRelativeMode(threadBox, AutoPane.Motion.BOTTOM);

            networkConfig.setPosition(retryBox, false, 0, 0, fontSize * 2.8 + spacing * 2, fontSize * 4.2 + spacing * 2);
            networkConfig.flipRelativeMode(retryBox, AutoPane.Motion.BOTTOM);

            networkConfig.setPosition(timeoutBox, false, 0, 0, fontSize * 4.2 + spacing * 3, fontSize * 5.6 + spacing * 3);
            networkConfig.flipRelativeMode(timeoutBox, AutoPane.Motion.BOTTOM);

            titleBox.setStyle("-fx-background-color: #" + Toolkit.colorToString(TRANSPERTANT_THEME_COLOR));

            networkConfig.getChildren().addAll(titleBox, threadBox, retryBox, timeoutBox);
        }

        AutoPane textConfig = new AutoPane();
        {
            AutoPane titleBox = new AutoPane();
            {
                Label head = new Label("文本");
                head.setFont(FONT_NORMAL);
                head.setTextFill(TEXT_COLOR);

                titleBox.setPosition(head, AutoPane.AlignmentMode.CENTER, AutoPane.LocateMode.RELATIVE, 0.5, 0.5);
                titleBox.getChildren().addAll(head);
            }

            AutoPane pieceBox = new AutoPane();
            {
                Label label = new Label("切片最大长度");
                label.setFont(FONT_NORMAL);
                label.setTextFill(TEXT_COLOR);

                Circle thumb = new Circle(10);
                thumb.setStroke(THEME_COLOR);
                thumb.setStrokeWidth(0.5);
                SmoothSlider slider = new SmoothSlider(1, 512, textPieceSize)
                        .thumb(thumb)
                        .trackColor(SLIDER_BG_COLOR)
                        .trackProgressColor(TRANSPERTANT_THEME_COLOR)
                        .thumbColor(SLIDER_THUMB_COLOR)
                        .hoverColor(SLIDER_THUMB_HOVER_COLOR)
                        .pressedColor(SLIDER_THUMB_PRESSED_COLOR)
                        .init();
                SmoothTextField textField = new SmoothTextField()
                        .borderShape(10)
                        .borderColor(THEME_COLOR)
                        .bgColor(MODULE_BG_COLOR)
                        .borderRadius(0.5)
                        .font(FONT_SMALLER)
                        .textColor(TEXT_COLOR)
                        .buttonStyle(buttonStyle)
                        .subLineColor(TEXT_COLOR)
                        .init();
                textField.getTextField().setText(String.valueOf(textPieceSize));

                pieceBox.setPosition(label, AutoPane.AlignmentMode.LEFT_CENT, AutoPane.LocateMode.RELATIVE, 0, 0.5);
                pieceBox.setPosition(textField, false, 40, 0, 0, 0);
                pieceBox.flipRelativeMode(textField, AutoPane.Motion.LEFT);

                Rectangle sliderClip = new Rectangle(100, 50);
                slider.setClip(sliderClip);
                sliderClip.setLayoutX(10);
                sliderClip.setLayoutY(12);
                sliderClip.setHeight(40);
                pieceBox.setPosition(slider, false, fontSize * 6 - 25, 30, -15, 0);
                slider.widthProperty().addListener((o, ov, nv) -> sliderClip.setWidth(nv.doubleValue() - 20));
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

                textField.getTextField().textProperty().addListener((o, ov, nv) -> {
                    if (!isUpdating[0]) {

                        isUpdating[0] = true;
                        switch (checkInteger(nv)) {
                            case 0 -> {
                                double value = Math.max(0, Math.min(slider.getMax(), Integer.parseInt(nv)));
                                slider.setValue(value);
                                if (value != Integer.parseInt(nv)) {
                                    textField.getTextField().setText(String.valueOf((int) value));
                                }
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
                            textField.getTextField().setText(String.valueOf(textPieceSize));
                            slider.setValue(textPieceSize);
                        }
                    }
                });

                pieceBox.getChildren().addAll(label, slider, textField);
            }

            AutoPane symbolBox = new AutoPane();
            {
                Label label = new Label("切片首选符号");
                label.setFont(FONT_NORMAL);
                label.setTextFill(TEXT_COLOR);

                SmoothTextField textField = new SmoothTextField()
                        .borderShape(10)
                        .borderColor(THEME_COLOR)
                        .bgColor(MODULE_BG_COLOR)
                        .borderRadius(0.5)
                        .font(FONT_SMALLER)
                        .textColor(TEXT_COLOR)
                        .buttonStyle(buttonStyle)
                        .subLineColor(TEXT_COLOR)
                        .init();
                textField.getTextField().setPromptText("图片url");

                textField.getTextField().textProperty().addListener((o, ov, nv) -> {
                    if (Objects.equals(nv, ov)) return;
                    textSplitSymbols.clear();
                    textSplitSymbols.addAll(nv.chars().mapToObj(c -> (char) c).toList());
                });
                textField.getTextField().setText(XCldrStub.Joiner.on("").join(textSplitSymbols));

                symbolBox.setPosition(label, AutoPane.AlignmentMode.LEFT_CENT, AutoPane.LocateMode.RELATIVE, 0, 0.5);
                symbolBox.setPosition(textField, false, fontSize * 6, 0, 0, 0);

                symbolBox.getChildren().addAll(label, textField);
            }

            AutoPane appendNameBox = new AutoPane();
            {
                Label label = new Label("章节名添加卷名");
                label.setFont(FONT_NORMAL);
                label.setTextFill(TEXT_COLOR);


                SmoothSwitch switchButton = new SmoothSwitch()
                        .width(fontSize * 2)
                        .height(fontSize * 1.2)
                        .state(isAppendVolumeName)
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

                Label valueLabel = new Label(isAppendVolumeName ? "启用" : "禁用");
                valueLabel.setFont(FONT_NORMAL);
                valueLabel.setTextFill(TEXT_COLOR);

                switchButton.stateProperty().addListener((o, ov, nv) -> {
                    isAppendVolumeName = nv;
                    valueLabel.setText(isAppendVolumeName ? "启用" : "禁用");
                });


                appendNameBox.setPosition(label, AutoPane.AlignmentMode.LEFT_CENT, AutoPane.LocateMode.RELATIVE, 0, 0.5);
                appendNameBox.setPosition(switchButton, AutoPane.AlignmentMode.LEFT_CENT, AutoPane.LocateMode.ABSOLUTE, fontSize * 6, fontSize * 0.7);
                appendNameBox.setPosition(valueLabel, AutoPane.AlignmentMode.LEFT_CENT, AutoPane.LocateMode.ABSOLUTE, fontSize * 8.5, fontSize * 0.7);

                appendNameBox.getChildren().addAll(label, switchButton, valueLabel);
            }

            AutoPane appendOrdinal = new AutoPane();
            {
                Label label = new Label("文件名添加序号");
                label.setFont(FONT_NORMAL);
                label.setTextFill(TEXT_COLOR);


                SmoothSwitch switchButton = new SmoothSwitch()
                        .width(fontSize * 2)
                        .height(fontSize * 1.2)
                        .state(isAppendOrdinal)
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

                Label valueLabel = new Label(isAppendOrdinal ? "启用" : "禁用");
                valueLabel.setFont(FONT_NORMAL);
                valueLabel.setTextFill(TEXT_COLOR);

                switchButton.stateProperty().addListener((o, ov, nv) -> {
                    isAppendOrdinal = nv;
                    valueLabel.setText(isAppendOrdinal ? "启用" : "禁用");
                });


                appendOrdinal.setPosition(label, AutoPane.AlignmentMode.LEFT_CENT, AutoPane.LocateMode.RELATIVE, 0, 0.5);
                appendOrdinal.setPosition(switchButton, AutoPane.AlignmentMode.LEFT_CENT, AutoPane.LocateMode.ABSOLUTE, fontSize * 6, fontSize * 0.7);
                appendOrdinal.setPosition(valueLabel, AutoPane.AlignmentMode.LEFT_CENT, AutoPane.LocateMode.ABSOLUTE, fontSize * 8.5, fontSize * 0.7);

                appendOrdinal.getChildren().addAll(label, switchButton, valueLabel);
            }

            textConfig.setPosition(titleBox, false, 0, 0, 0, fontSize * 1.4);
            textConfig.flipRelativeMode(titleBox, AutoPane.Motion.BOTTOM);

            textConfig.setPosition(pieceBox, false, 0, 0, fontSize * 1.4 + spacing, fontSize * 2.8 + spacing);
            textConfig.flipRelativeMode(pieceBox, AutoPane.Motion.BOTTOM);

            textConfig.setPosition(symbolBox, false, 0, 0, fontSize * 2.8 + spacing * 2, fontSize * 4.2 + spacing * 2);
            textConfig.flipRelativeMode(symbolBox, AutoPane.Motion.BOTTOM);

            textConfig.setPosition(appendNameBox, false, 0, 0, fontSize * 4.2 + spacing * 3, fontSize * 5.6 + spacing * 3);
            textConfig.flipRelativeMode(appendNameBox, AutoPane.Motion.BOTTOM);

            textConfig.setPosition(appendOrdinal, false, 0, 0, fontSize * 5.6 + spacing * 4, fontSize * 7 + spacing * 4);
            textConfig.flipRelativeMode(appendOrdinal, AutoPane.Motion.BOTTOM);

            titleBox.setStyle("-fx-background-color: #" + Toolkit.colorToString(TRANSPERTANT_THEME_COLOR));

            textConfig.getChildren().addAll(titleBox, pieceBox, symbolBox, appendNameBox, appendOrdinal);
        }

        AutoPane rootBox = new AutoPane();

        rootBox.setPosition(windowConfig, false, false, false, false, 0, 0, 0, fontSize * 8.4 + spacing * 6);
        rootBox.setPosition(networkConfig, false, false, false, false, 0, 0, fontSize * 8.4 + spacing * 9, fontSize * 14 + spacing * 12);
        rootBox.setPosition(textConfig, false, false, false, false, 0, 0, fontSize * 14 + spacing * 15, fontSize * 21 + spacing * 20);
        rootBox.setMaxHeight(fontSize * 21 + spacing * 20);
        rootBox.setMinHeight(fontSize * 21 + spacing * 20);
        rootBox.flipRelativeMode(windowConfig, AutoPane.Motion.BOTTOM, true);
        rootBox.flipRelativeMode(networkConfig, AutoPane.Motion.BOTTOM, true);
        rootBox.flipRelativeMode(textConfig, AutoPane.Motion.BOTTOM, true);

        rootBox.getChildren().addAll(windowConfig, networkConfig, textConfig);
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
            if (nv) {
                rootBox.setPosition(windowConfig, false, true, false, false, 0, 0.6866666666, 0, 0);
                rootBox.setPosition(networkConfig, true, true, false, false, 0.3433333333, 0.3433333333, 0, 0);
                rootBox.setPosition(textConfig, true, false, false, false, 0.6866666666, 0, 0, 0);
                rootBox.setMaxHeight(fontSize * 8.4 + spacing * 6);
                rootBox.setMinHeight(fontSize * 8.4 + spacing * 6);
                rootBox.flipRelativeMode(windowConfig, AutoPane.Motion.BOTTOM, false);
                rootBox.flipRelativeMode(networkConfig, AutoPane.Motion.BOTTOM, false);
                rootBox.flipRelativeMode(textConfig, AutoPane.Motion.BOTTOM, false);
            } else {
                rootBox.setPosition(windowConfig, false, false, false, false, 0, 0, 0, fontSize * 8.4 + spacing * 6);
                rootBox.setPosition(networkConfig, false, false, false, false, 0, 0, fontSize * 8.4 + spacing * 9, fontSize * 14 + spacing * 12);
                rootBox.setPosition(textConfig, false, false, false, false, 0, 0, fontSize * 14 + spacing * 15, fontSize * 21 + spacing * 20);
                rootBox.setMaxHeight(fontSize * 21 + spacing * 20);
                rootBox.setMinHeight(fontSize * 21 + spacing * 20);
                rootBox.flipRelativeMode(windowConfig, AutoPane.Motion.BOTTOM, true);
                rootBox.flipRelativeMode(networkConfig, AutoPane.Motion.BOTTOM, true);
                rootBox.flipRelativeMode(textConfig, AutoPane.Motion.BOTTOM, true);
            }
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

    private static String formatDuration(long milliseconds) {

        long h = milliseconds / 3600000;
        long m = (milliseconds % 3600000) / 60000;
        long s = ((milliseconds % 3600000) % 60000) / 1000;
        long S = milliseconds % 1000;

        StringBuilder sb = new StringBuilder();

        if (h > 0) sb.append(h).append("h ");
        if (m > 0) sb.append(m).append("m ");
        if (s > 0 || S > 0) {
            sb.append(s);
            if (S > 0) sb.append(".").append(S / 100);
            sb.append("s");
        }

        return sb.toString();
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