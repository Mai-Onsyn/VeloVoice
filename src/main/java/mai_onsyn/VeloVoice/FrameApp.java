package mai_onsyn.VeloVoice;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import mai_onsyn.AnimeFX.I18N;
import mai_onsyn.VeloVoice.App.WindowManager;
import mai_onsyn.AnimeFX.layout.AutoPane;
import mai_onsyn.VeloVoice.App.ResourceManager;
import mai_onsyn.VeloVoice.FrameFactory.FrameThemes;
import mai_onsyn.VeloVoice.FrameFactory.MainFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static mai_onsyn.VeloVoice.App.Runtime.themeManager;
import static mai_onsyn.VeloVoice.App.Runtime.windowConfig;

public class FrameApp extends Application {
    private static final Logger log = LogManager.getLogger(FrameApp.class);

    @Override
    public void start(Stage stage) throws Exception {

        AutoPane root = new AutoPane();
        root.setOnMousePressed(_ -> root.requestFocus());

        FrameThemes.setDarkMode(windowConfig.getBoolean("DarkMode"));//init static variables for custom themes
        String initialThemeColor = windowConfig.getString("ThemeColor");
        try {
            FrameThemes.setThemeColor(Color.valueOf(initialThemeColor));
        } catch (IllegalArgumentException | NullPointerException e) {
            log.warn(I18N.getCurrentValue("log.runtime.warn.color_parse_failed"), initialThemeColor);
        }

        I18N.addOnChangedAction(() -> stage.setTitle(I18N.getCurrentValue("stage.main.title")));

        //The topic method to draw whole window
        MainFactory.drawMainFrame(root);

        I18N.setLanguage(windowConfig.getString("Language"));
        themeManager.flushAll();

        Scene scene = new Scene(root, 1280, 720);
        stage.setMinWidth(900);
        stage.setMinHeight(500);
        stage.getIcons().add(ResourceManager.icon);
        stage.setTitle("VeloVoice");
        stage.setScene(scene);

        log.debug("Application started");
        root.requestFocus();

        WindowManager.register(stage);
        WindowManager.initialize(stage);

        stage.show();
    }
}
