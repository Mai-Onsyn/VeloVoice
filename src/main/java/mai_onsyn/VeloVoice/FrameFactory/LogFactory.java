package mai_onsyn.VeloVoice.FrameFactory;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.stage.Stage;
import mai_onsyn.AnimeFX.I18N;
import mai_onsyn.AnimeFX.Module.AXLog4j2;
import mai_onsyn.AnimeFX.Module.AXProgressBar;
import mai_onsyn.AnimeFX.Utls.AXLangLabel;
import mai_onsyn.VeloVoice.App.WindowManager;
import mai_onsyn.AnimeFX.layout.AutoPane;
import mai_onsyn.VeloVoice.App.ResourceManager;

import java.text.SimpleDateFormat;

import static mai_onsyn.VeloVoice.App.Runtime.*;
import static mai_onsyn.VeloVoice.FrameFactory.FrameThemes.*;
import static mai_onsyn.VeloVoice.Text.TextUtil.formatMillisToTime;

public class LogFactory {

    public static final Stage logStage = new Stage();
    public static final AXLog4j2 logger = new AXLog4j2();

    public static final AutoPane root = new AutoPane();

    public static final AXProgressBar totalProgressBar = new AXProgressBar();
    public static final AXProgressBar currentProgressBar = new AXProgressBar();

    public static final AXLangLabel totalInfo = new AXLangLabel(I18N.getCurrentValue("log.progress.total") + ": 0 / 0");
    public static final AXLangLabel currentInfo = new AXLangLabel(I18N.getCurrentValue("log.progress.current") + ": 0 / 0");
    public static final AXLangLabel totalTime = new AXLangLabel("00:00:00 | 00:00:00");
    public static final AXLangLabel currentTime = new AXLangLabel("00:00:00 | 00:00:00");

    static {
        totalFinished.addListener((o, ov, nv) -> {
            totalProgressBar.setProgress(totalFinished.doubleValue() / totalCount);
        });
        currentFinished.addListener((o, ov, nv) -> {
            currentProgressBar.setProgress(currentFinished.doubleValue() / currentTotalCount);
            Platform.runLater(() -> {
                totalInfo.setText(String.format("%s: %.2f%% [%d/%d]", I18N.getCurrentValue("log.progress.total"), 100 * totalFinished.doubleValue() / totalCount, totalFinished.intValue(), totalCount));
                currentInfo.setText(String.format("%s: %.2f%% [%d/%d] (%s)", I18N.getCurrentValue("log.progress.current"), 100 * currentFinished.doubleValue() / currentTotalCount, currentFinished.intValue(), currentTotalCount, currentFileName));
            });
        });
        cycleTasks.add(() -> Platform.runLater(() -> {
            if (totalStartTime != 0 && totalProgressBar.getProgress() != 0) {
                long totalPassed = System.currentTimeMillis() - totalStartTime;
                totalTime.setText(String.format("%s | %s", formatMillisToTime(totalPassed), formatMillisToTime((long) (totalPassed / totalProgressBar.getProgress() - totalPassed))));
            }

            if (currentStartTime != 0 && currentProgressBar.getProgress() != 0) {
                long currentPassed = System.currentTimeMillis() - currentStartTime;
                currentTime.setText(String.format("%s | %s", formatMillisToTime(currentPassed), formatMillisToTime((long) (currentPassed / currentProgressBar.getProgress() - currentPassed))));
            }
        }));

        I18N.addOnChangedAction(() -> {
            if (!isTTSRunning.get()) Platform.runLater(() -> {
                totalInfo.setText(I18N.getCurrentValue("log.progress.total") + ": 0% [0/0]");
                currentInfo.setText(I18N.getCurrentValue("log.progress.current") + ": 0% [0/0] (" + I18N.getCurrentValue("log.progress.initializing") + ")");
                currentFileName = I18N.getCurrentValue("log.progress.initializing");
            });
        });
    }

    static void drawLogFrame() {
        Scene scene = new Scene(root, 600, 450);
        logStage.setScene(scene);
        logStage.setMinWidth(400);
        logStage.setMinHeight(300);
        logStage.getIcons().add(ResourceManager.icon);
        logStage.setTitle("Logs");
        WindowManager.register(logStage);
        I18N.addOnChangedAction(() -> logStage.setTitle(I18N.getCurrentValue("stage.log.title")));

        totalTime.setAlignment(Pos.BOTTOM_RIGHT);
        currentTime.setAlignment(Pos.BOTTOM_RIGHT);
        totalInfo.setAlignment(Pos.BOTTOM_LEFT);
        currentInfo.setAlignment(Pos.BOTTOM_LEFT);


        AutoPane infoPane = new AutoPane();

        root.getChildren().addAll(logger, infoPane);
        root.setPosition(logger, false, 20, 20, 20, 20);
        root.setPosition(infoPane, false, 20, 20, 80, 20);
        root.flipRelativeMode(infoPane, AutoPane.Motion.TOP);
        infoPane.setVisible(false);

        logger.setTheme(CSS_TEXT_AREA);
        themeManager.register(logger);

        infoPane.getChildren().addAll(totalProgressBar, currentProgressBar, totalInfo, currentInfo, totalTime, currentTime);

        infoPane.setPosition(totalInfo, true, 0, 0, 0, 0.6);
        infoPane.setPosition(totalTime, true, 0, 0, 0, 0.6);
        infoPane.setPosition(totalProgressBar, true, 0, 0, 0.4, 0.5);
        infoPane.setPosition(currentInfo, true, 0, 0, 0.5, 0.1);
        infoPane.setPosition(currentTime, true, 0, 0, 0.5, 0.1);
        infoPane.setPosition(currentProgressBar, true, 0, 0, 0.9, 0);

        totalProgressBar.setTheme(SMALL_PROGRESS_BAR);
        currentProgressBar.setTheme(SMALL_PROGRESS_BAR);
        themeManager.register(totalProgressBar, currentProgressBar);

        totalInfo.setTheme(STANDARD_LABEL);
        currentInfo.setTheme(STANDARD_LABEL);
        totalTime.setTheme(STANDARD_LABEL);
        currentTime.setTheme(STANDARD_LABEL);
        themeManager.register(totalInfo, currentInfo, totalTime, currentTime);


        isTTSRunning.addListener((o, ov, nv) -> {
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
