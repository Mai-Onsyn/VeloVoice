package mai_onsyn.VeloVoice2.FrameFactory;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import mai_onsyn.AnimeFX2.Module.AXLogger;
import mai_onsyn.AnimeFX2.Module.AXProgressBar;
import mai_onsyn.AnimeFX2.layout.AutoPane;

import static mai_onsyn.VeloVoice2.App.Runtime.*;

public class LogFactory {

    public static final Stage logStage = new Stage();
    public static final AXLogger logger = new AXLogger();

    public static final AutoPane root = new AutoPane();

    public static final AXProgressBar totalProgressBar = new AXProgressBar();
    public static final AXProgressBar currentProgressBar = new AXProgressBar();

    static {
        totalFinished.addListener((o, ov, nv) -> {
            totalProgressBar.setProgress(totalFinished.doubleValue() / totalCount);
        });
        currentFinished.addListener((o, ov, nv) -> {
            currentProgressBar.setProgress(currentFinished.doubleValue() / currentTotalCount);
        });
    }

    public static void drawLogFrame() {
        Scene scene = new Scene(root, 600, 450);
        logStage.setScene(scene);
        logStage.setMinWidth(400);
        logStage.setMinHeight(300);
        logStage.getIcons().add(new Image("textures/icon.png"));
        logStage.setTitle("Log");

        root.getChildren().addAll(logger, totalProgressBar, currentProgressBar);

        root.setPosition(logger, false, 0, 0, 0, 50);
        root.setPosition(totalProgressBar, false, 0, 0, 50, 25);
        root.setPosition(currentProgressBar, false, 0, 0, 25, 0);
        root.flipRelativeMode(totalProgressBar, AutoPane.Motion.TOP);
        root.flipRelativeMode(currentProgressBar, AutoPane.Motion.TOP);
    }
}
