package mai_onsyn.VeloVoice2;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import mai_onsyn.AnimeFX2.layout.AutoPane;
import mai_onsyn.VeloVoice2.App.Runtime;
import mai_onsyn.VeloVoice2.FrameFactory.LogFactory;
import mai_onsyn.VeloVoice2.FrameFactory.MainFactory;

import static mai_onsyn.VeloVoice2.FrameFactory.LogFactory.logger;

public class FrameApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {

        AutoPane root = new AutoPane();
        root.setOnMousePressed(_ -> root.requestFocus());

        MainFactory.drawMainFrame(root);
        LogFactory.drawLogFrame();
        Runtime.languageManager.switchLanguage("en_us");

        Scene scene = new Scene(root, 1280, 720);
        stage.setMinWidth(960);
        stage.setMinHeight(540);
        stage.getIcons().add(new Image("textures/icon.png"));
        stage.setTitle("VeloVoice");
        stage.setScene(scene);
        stage.show();

        logger.info("Application started");
        root.requestFocus();
    }
}
