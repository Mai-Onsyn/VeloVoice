package mai_onsyn.VeloVoice;

import com.kieferlam.javafxblur.Blur;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import mai_onsyn.AnimeFX.Frame.Layout.AutoPane;
import mai_onsyn.AnimeFX.Frame.Layout.NamePopup;
import mai_onsyn.AnimeFX.Frame.Module.DiffusionButton;
import mai_onsyn.VeloVoice.App.Theme;
import mai_onsyn.VeloVoice.Utils.Util;

import static mai_onsyn.VeloVoice.App.AppConfig.*;
import static mai_onsyn.VeloVoice.App.Runtime.*;

public class FrameApp extends Application {

    public static AutoPane rootPane;
    public static Stage STAGE;
    @Override
    public void start(Stage stage) {
        stage.setTitle("VeloVoice");
        STAGE = stage;
        AutoPane root = new AutoPane();
        rootPane = root;
        root.setOnMousePressed(_ -> root.requestFocus());
        stage.setOnShown(_ -> root.requestFocus());
        Scene scene = new Scene(root);
        stage.setScene(scene);
        if (isWindowSupport) {
            scene.setFill(Color.TRANSPARENT);
            root.setBackground(Background.EMPTY);
            stage.initStyle(StageStyle.TRANSPARENT);
        }
        NamePopup.setStage(stage);



        drawRoot(root);



        root.setPrefSize(1280, 720);
        stage.setMinWidth(960);
        stage.setMinHeight(540);
        stage.getIcons().add(new Image("textures/icon.png"));
        stage.show();

        if (isWindowSupport) Blur.applyBlur(stage, Theme.blurMode);

        logger.prompt("Application launched!");
    }

    private void drawRoot(AutoPane root) {
        AutoPane contentPane = new AutoPane();

        AutoPane treeArea = FrameFactory.getTreeArea();
        AutoPane textArea = FrameFactory.getTextArea();
        AutoPane operationArea = FrameFactory.getOperationArea();
        DiffusionButton settingButton = FrameFactory.getSettingsButton();
        FrameFactory.drawProgressArea();

        contentPane.setPosition(treeArea, false, true, false, false, 50, 0.75, 50, 50);
        contentPane.setPosition(textArea, true, true, false, false, 0.28, 0.35, 50, 50);
        contentPane.setPosition(operationArea, true, false, false, false, 0.68, 50, 50, 50);
        contentPane.setPosition(settingButton, false, false, false, false, 5, 45, 5, 45);
        contentPane.setPosition(progressPane, false, false, false, false, 50, 50, 40, 10);
        contentPane.flipRelativeMode(settingButton, AutoPane.Motion.LEFT);
        contentPane.flipRelativeMode(settingButton, AutoPane.Motion.BOTTOM);
        contentPane.flipRelativeMode(progressPane, AutoPane.Motion.TOP);
        progressPane.setVisible(false);

        contentPane.getChildren().addAll(treeArea, textArea, operationArea, settingButton, progressPane);
        if (isWindowSupport) {
            //window shadow
            Rectangle WINDOW_SHADOW = new Rectangle();
            WINDOW_SHADOW.setFill(Theme.LIGHT_THEME ? Theme.LIGHT_THEME_COLOR : Theme.DARK_THEME_COLOR);
            WINDOW_SHADOW.setOpacity(0.01);
            root.setPosition(WINDOW_SHADOW, false, 0, 0, 0, 0);
            AutoPane windowBar = FrameFactory.getWindowModule(STAGE);
            root.setPosition(windowBar, false, 0, 0, 0, 28);
            root.setPosition(contentPane, false, 0, 0, 28, 0);
            root.flipRelativeMode(windowBar, AutoPane.Motion.BOTTOM);
            root.getChildren().addAll(WINDOW_SHADOW, windowBar, contentPane);
            Util.DrawUtil.addDrawFunc(STAGE, root);
        }
        else {
            root.setPosition(contentPane, false, 0, 0, 0, 0);
            root.getChildren().add(contentPane);
        }
    }

}