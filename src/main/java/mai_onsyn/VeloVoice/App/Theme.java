package mai_onsyn.VeloVoice.App;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import mai_onsyn.AnimeFX.Frame.Utils.Toolkit;

import java.util.List;

public class Theme {
    public static List<Color> ALTERNATE_THEMES = List.of(
            Color.web("#dc143c", 1),
            Color.web("#ff1493", 1),
            Color.web("#ff00ff", 1),
            Color.web("#8b008b", 1),
            Color.web("#9400d3", 1),
            Color.web("#4b0082", 1),
            Color.web("#7b68ee", 1),
            Color.web("#000080", 1),
            Color.web("#6495ed", 1),
            Color.web("#4682b4", 1),
            Color.web("#00bfff", 1),
            Color.web("#00ced1", 1),
            Color.web("#008b8b", 1),
            Color.web("#00fa9a", 1),
            Color.web("#3cb371", 1),
            Color.web("#556b2f", 1),
            Color.web("#bdb76b", 1),
            Color.web("#daa520", 1),
            Color.web("#cd853f", 1),
            Color.web("#a0522d", 1),
            Color.web("#fa8072", 1),
            Color.web("#b22222", 1)
    );

    public static boolean LIGHT_THEME = ConfigListener.light_theme_cache;
    public static double BACKGROUND_AMBIGUITY = 25;
    public static double BACKGROUND_BRIGHTNESS = 0.75;
    public static String BACKGROUND_IMAGE_URI = "textures/bg.png";

    public static Image BACKGROUND_IMAGE;
    public static final Font FONT_NORMAL = new Font(14);
    public static final Font FONT_SMALLER = new Font(12.25);

    public static final Color DARK_THEME_COLOR = new Color(0.1, 0.1, 0.1, 0.75);
    public static final Color LIGHT_THEME_COLOR = new Color(0.97, 0.97, 0.97, 0.75);

    public static final int BUTTON_ANIME_DURATION = 300;


    public static Color THEME_COLOR = ConfigListener.theme_color_cache;
    public static final Color MODULE_BG_COLOR;
    public static final Color MODULE_BG_TRANSPERTANT_COLOR;

    public static final Color BUTTON_FOCUS_COLOR;
    public static final Color BUTTON_PRESSED_COLOR;
    public static final Color TEXT_COLOR;
    public static final Color BUTTON_TEXT_FOCUS_COLOR;

    public static final Color SLIDER_BG_COLOR;
    public static final Color TRANSPERTANT_THEME_COLOR;
    public static final Color SLIDER_THUMB_COLOR;
    public static final Color SLIDER_THUMB_HOVER_COLOR;
    public static final Color SLIDER_THUMB_PRESSED_COLOR;

    public static final Color TREE_SWITCHED_BACKGROUND_COLOR;
    public static final Color TREE_SWITCHED_BACKGROUND_FOCUS_COLOR;
    public static final Color TREE_SWITCHED_TEXT_COLOR;
    public static final Color TREE_SWITCHED_TEXT_FOCUS_COLOR;

    static {
        MODULE_BG_COLOR = LIGHT_THEME ? Toolkit.adjustOpacity(LIGHT_THEME_COLOR, 0.3) : Toolkit.adjustOpacity(DARK_THEME_COLOR, 0.3);
        MODULE_BG_TRANSPERTANT_COLOR = Toolkit.adjustOpacity(MODULE_BG_COLOR, 0);

        BUTTON_FOCUS_COLOR = new Color(0.6, 0.6, 0.6, 0.5);
        BUTTON_PRESSED_COLOR = Toolkit.adjustBrightness(BUTTON_FOCUS_COLOR, LIGHT_THEME ? 0.4 : 0.8);
        TEXT_COLOR = LIGHT_THEME ? new Color(0, 0, 0, 1) : new Color(1, 1, 1, 1);
        BUTTON_TEXT_FOCUS_COLOR = THEME_COLOR;

        SLIDER_BG_COLOR = new Color(1, 1, 1, 0.3);
        TRANSPERTANT_THEME_COLOR = new Color(THEME_COLOR.getRed(), THEME_COLOR.getGreen(), THEME_COLOR.getBlue(), 0.3);
        SLIDER_THUMB_COLOR = new Color(1, 1, 1, 1);
        SLIDER_THUMB_HOVER_COLOR = new Color(0.9, 0.9, 0.9, 1);
        SLIDER_THUMB_PRESSED_COLOR = new Color(0.8, 0.8, 0.8, 1);

        TREE_SWITCHED_BACKGROUND_COLOR = Toolkit.adjustBrightness(THEME_COLOR, THEME_COLOR.getBrightness() - 0.2, 0.3);
        TREE_SWITCHED_BACKGROUND_FOCUS_COLOR = Toolkit.adjustBrightness(THEME_COLOR, THEME_COLOR.getBrightness() + 0.2, 0.3);
        TREE_SWITCHED_TEXT_COLOR = Toolkit.adjustHue(TREE_SWITCHED_BACKGROUND_COLOR, TREE_SWITCHED_BACKGROUND_COLOR.getHue() + 180, 1);
        TREE_SWITCHED_TEXT_FOCUS_COLOR = Toolkit.adjustHue(TREE_SWITCHED_BACKGROUND_FOCUS_COLOR, TREE_SWITCHED_BACKGROUND_FOCUS_COLOR.getHue() + 180, 1);
    }
}