package mai_onsyn.VeloVoice2.FrameFactory;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import mai_onsyn.AnimeFX2.Module.AXLog4j2;
import mai_onsyn.AnimeFX2.Module.AXProgressBar;
import mai_onsyn.AnimeFX2.Styles.DefaultAXProgressBarStyle;
import mai_onsyn.AnimeFX2.Utls.AXLangLabel;
import mai_onsyn.AnimeFX2.layout.AutoPane;

import static mai_onsyn.VeloVoice2.App.Runtime.*;

public class LogFactory {

    public static final Stage logStage = new Stage();
    public static final AXLog4j2 logger = new AXLog4j2();

    public static final AutoPane root = new AutoPane();

    public static final AXProgressBar totalProgressBar = new AXProgressBar();
    public static final AXProgressBar currentProgressBar = new AXProgressBar();

    public static final AXLangLabel totalInfo = new AXLangLabel("Total: 0/0");
    public static final AXLangLabel currentInfo = new AXLangLabel("Current: 0/0");
    public static final AXLangLabel totalTime = new AXLangLabel("00:00:00 | 00:00:00");
    public static final AXLangLabel currentTime = new AXLangLabel("00:00:00 | 00:00:00");

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
        logStage.setTitle("Logs");

        totalTime.setAlignment(Pos.BOTTOM_RIGHT);
        currentTime.setAlignment(Pos.BOTTOM_RIGHT);
        totalInfo.setAlignment(Pos.BOTTOM_LEFT);
        currentInfo.setAlignment(Pos.BOTTOM_LEFT);

        //Temporary
        {
            DefaultAXProgressBarStyle style = new DefaultAXProgressBarStyle();
            style.setBorderInsets(0);
            totalProgressBar.setTheme(style);
            currentProgressBar.setTheme(style);
            totalProgressBar.update();
            currentProgressBar.update();
        }

        AutoPane infoPane = new AutoPane();

        root.getChildren().addAll(logger, infoPane);
        root.setPosition(logger, false, 20, 20, 20, 20);
        root.setPosition(infoPane, false, 20, 20, 80, 20);
        root.flipRelativeMode(infoPane, AutoPane.Motion.TOP);
        infoPane.setVisible(false);

        infoPane.getChildren().addAll(totalProgressBar, currentProgressBar, totalInfo, currentInfo, totalTime, currentTime);

        infoPane.setPosition(totalInfo, true, 0, 0, 0, 0.6);
        infoPane.setPosition(totalTime, true, 0, 0, 0, 0.6);
        infoPane.setPosition(totalProgressBar, true, 0, 0, 0.4, 0.5);
        infoPane.setPosition(currentInfo, true, 0, 0, 0.5, 0.1);
        infoPane.setPosition(currentTime, true, 0, 0, 0.5, 0.1);
        infoPane.setPosition(currentProgressBar, true, 0, 0, 0.9, 0);


        isRunning.addListener((o, ov, nv) -> {
            if (nv) {
                infoPane.setVisible(true);
                root.setPosition(logger, false, 20, 20, 20, 80);
            } else {
                infoPane.setVisible(false);
                root.setPosition(logger, false, 20, 20, 20, 20);
            }
        });
    }
}
