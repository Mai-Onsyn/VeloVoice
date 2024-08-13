package mai_onsyn.VeloVoice;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import mai_onsyn.AnimeFX.Frame.Layout.AutoPane;
import mai_onsyn.AnimeFX.Frame.Layout.NamePopup;
import mai_onsyn.AnimeFX.Frame.Module.DiffusionButton;

import static mai_onsyn.VeloVoice.App.Runtime.*;

public class FrameApp extends Application {

    public static AutoPane rootPane;
    @Override
    public void start(Stage stage) {
        AutoPane root = new AutoPane();
        rootPane = root;
        root.setOnMousePressed(_ -> root.requestFocus());
        stage.setOnShown(_ -> root.requestFocus());
        Scene scene = new Scene(root);
        stage.setScene(scene);
        NamePopup.setStage(stage);



        drawRoot(root);



        root.setPrefSize(1280, 720);
        stage.setMinWidth(960);
        stage.setMinHeight(540);
        stage.setTitle("VeloVoice");
        stage.getIcons().add(new Image("textures/icon.png"));
        stage.show();

        logger.prompt("Application launched!");
    }

    private void drawRoot(AutoPane root) {

        AutoPane treeArea = FrameFactory.getTreeArea();
        AutoPane textArea = FrameFactory.getTextArea();
        AutoPane operationArea = FrameFactory.getOperationArea();
        DiffusionButton settingButton = FrameFactory.getSettingsButton();
        FrameFactory.drawProgressArea();

        root.setPosition(treeArea, false, true, false, false, 50, 0.75, 50, 50);
        root.setPosition(textArea, true, true, false, false, 0.28, 0.35, 50, 50);
        root.setPosition(operationArea, true, false, false, false, 0.68, 50, 50, 50);
        root.setPosition(settingButton, false, false, false, false, 5, 45, 5, 45);
        root.setPosition(progressPane, false, false, false, false, 50, 50, 40, 10);
        root.flipRelativeMode(settingButton, AutoPane.Motion.LEFT);
        root.flipRelativeMode(settingButton, AutoPane.Motion.BOTTOM);
        root.flipRelativeMode(progressPane, AutoPane.Motion.TOP);
        progressPane.setVisible(false);

        root.getChildren().addAll(treeArea, textArea, operationArea, settingButton, progressPane);
    }

}