package mai_onsyn.VeloVoice.App;

import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.Window;
import mai_onsyn.AnimeFX.I18N;
import mai_onsyn.AnimeFX.Module.AXBackGround;
import mai_onsyn.AnimeFX.Utls.Toolkit;
import mai_onsyn.AnimeFX.layout.AutoPane;
import mai_onsyn.VeloVoice.FrameFactory.FrameThemes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static mai_onsyn.VeloVoice.App.Runtime.windowConfig;

public class WindowManager {

    private static final List<Window> windows = new ArrayList<>();
    public static final List<AXBackGround> backgrounds = new ArrayList<>();
    private static final Logger log = LogManager.getLogger(WindowManager.class);

    public static void register(Window window) {
        windows.add(window);
    }

    public static void initialize(Window main) {
        setMainWindow(main);
        log.debug("Initialized window count = {}", windows.size());
        Image image = null;
        if (!windowConfig.getString("BackgroundImage").isBlank()) try {
            image = Toolkit.loadImage(windowConfig.getString("BackgroundImage"));
        } catch (IOException e) {
            log.warn(I18N.getCurrentValue("log.window_manager.warn.load_image_failed"), windowConfig.getString("BackgroundImage"));
        }
        for (Window window : windows) {
            Parent parent = window.getScene().getRoot();
            if (parent instanceof AutoPane root) {
                AXBackGround backGround = new AXBackGround(image, Color.gray(windowConfig.getBoolean("DarkMode") ? FrameThemes.DARK_GRAY : FrameThemes.LIGHT_GRAY, windowConfig.getDouble("BackgroundOpacity")), windowConfig.getDouble("BackgroundScale"), windowConfig.getDouble("BackgroundBlur"));
                backgrounds.add(backGround);
                root.getChildren().addFirst(backGround);
                root.setPosition(backGround, false, 0, 0, 0, 0);
            }
        }
    }

    public static void setBackgroundImage(Image image) {
        for (AXBackGround backGround : backgrounds) {
            backGround.setImage(image);
        }
    }

    public static void setBackgroundShadowColor(Color color) {
        for (AXBackGround backGround : backgrounds) {
            backGround.setShadowColor(color);
        }
    }

    public static void setBackgroundBlur(double blur) {
        for (AXBackGround backGround : backgrounds) {
            backGround.setBlurStrength(blur);
        }
    }

    public static void setBackgroundScale(double scale) {
        for (AXBackGround backGround : backgrounds) {
            backGround.setScale(scale);
        }
    }

    private static void setMainWindow(Window window) {
        if (windows.contains(window)) {
            window.setOnCloseRequest(e -> {
                for (Window w : windows) {
                    if (w != window && w instanceof Stage stage) stage.close();
                }
            });
            log.debug("Set main window to {}", window.toString());
        } else throw new IllegalArgumentException("Window is not registered");
    }

}
