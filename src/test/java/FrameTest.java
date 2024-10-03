import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import mai_onsyn.AnimeFX2.LanguageManager;
import mai_onsyn.AnimeFX2.Module.AXButton;
import mai_onsyn.AnimeFX2.Module.AXChoiceBox;
import mai_onsyn.AnimeFX2.Module.AXProgressBar;
import mai_onsyn.AnimeFX2.Module.AXTextArea;
import mai_onsyn.AnimeFX2.ThemeManager;
import mai_onsyn.AnimeFX2.Utls.AXButtonGroup;
import mai_onsyn.AnimeFX2.layout.AutoPane;

import java.io.File;

public class FrameTest extends Application {
    @Override
    public void start(Stage stage) throws Exception {

        AutoPane root = new AutoPane();
        root.setOnMousePressed(_ -> root.requestFocus());

        AXButton button = new AXButton("Test");
        AXTextArea textArea = new AXTextArea();
        AXChoiceBox choiceBox = new AXChoiceBox();
        textArea.setEditable(true);
        for (int i = 0; i < 20; i++) {
            choiceBox.createItem().setText("Item " + i);
        }

        ThemeManager manager = new ThemeManager();
        manager.register(button, textArea);

        LanguageManager languageManager = new LanguageManager(new File("D:\\Users\\Desktop\\Test\\lang"));
        languageManager.register("test.button", button);
        languageManager.register("test.text_area", textArea);
        languageManager.switchLanguage("zh_cn");

        Thread.ofVirtual().start(() -> {
            try {
                while (true) {
                    Thread.sleep(3000);
                    Platform.runLater(() -> languageManager.switchLanguage("en_us"));
                    Thread.sleep(3000);
                    Platform.runLater(() -> languageManager.switchLanguage("zh_cn"));
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        AXProgressBar progressBar = new AXProgressBar();

        Thread.ofVirtual().start(() -> {
            try {
                double i = 0;
                while (true) {
                    Thread.sleep(80);

                    i += 0.01;

                    progressBar.setProgress(i % 1.0);
                }
            } catch (InterruptedException _) {}
        });

        AXButton b1 = new AXButton("B1");
        AXButton b2 = new AXButton("B2");
        AXButton b3 = new AXButton("B3");

        AXButtonGroup group = new AXButtonGroup(b1, b2, b3);

        root.setPosition(b1, true, 0, 0.9, 0, 0.9);
        root.setPosition(b2, true, 0.1, 0.8, 0, 0.9);
        root.setPosition(b3, true, 0.2, 0.7, 0, 0.9);

        root.getChildren().addAll(button, textArea, choiceBox, b1, b2, b3, progressBar);
        root.setPosition(button, true, true, true, true, 0.1, 0.8, 0.1, 0.8);
        root.setPosition(textArea, true, true, true, true, 0.3, 0.2, 0.3, 0.2);
        root.setPosition(choiceBox, true, true, true, true, 0.8, 0.05, 0.05, 0.1);
        root.setPosition(progressBar, false, 50, 50, 70, 50);
        root.flipRelativeMode(choiceBox, AutoPane.Motion.BOTTOM);
        root.flipRelativeMode(progressBar, AutoPane.Motion.TOP);

        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
