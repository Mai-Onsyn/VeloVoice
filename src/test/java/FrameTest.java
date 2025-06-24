import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import mai_onsyn.AnimeFX2.Module.AXLog4j2;
import mai_onsyn.AnimeFX2.layout.AutoPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fxmisc.flowless.VirtualizedScrollPane;

public class FrameTest extends Application {
    private static final Logger log = LogManager.getLogger(FrameTest.class);

    @Override
    public void start(Stage stage) throws Exception {

        AutoPane root = new AutoPane();
        root.setOnMousePressed(_ -> root.requestFocus());

        AXLog4j2 textArea = new AXLog4j2();

        root.getChildren().add(textArea);
        root.setPosition(textArea, false, 0, 0, 0, 0);

        Scene scene = new Scene(root, 1280, 720);
        stage.setScene(scene);
        stage.show();

        Platform.runLater(() -> {
            Thread.ofVirtual().name("Test Thread").start(() -> {
                for (int i = 0; i < 128; i++) {
                    log.info("Test " + i);
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {

                    }
                }
            });
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
