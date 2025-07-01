package mai_onsyn.VeloVoice2.FrameFactory;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import mai_onsyn.AnimeFX2.Styles.*;

public class FrameThemes {

    public static Font STANDARD_FONT = Font.font(12);

    public static Color MAIN_TEXT_COLOR = Color.BLACK;
    public static Color MAIN_TEXT_HOVER_COLOR = Color.RED;

    public static Color MAIN_BASE_COLOR = Color.web("c0c0c080");
    public static Color MAIN_BASE_HOVER_COLOR = Color.web("80808080");
    public static Color MAIN_BASE_PRESSED_COLOR = Color.web("40404080");
    public static Color THEME_COLOR = Color.CORNFLOWERBLUE;

    public static Color MAIN_BUTTON_FILL_COLOR = Color.web("8080ff80");

    public static double MAIN_BORDER_ARC_SIZE = 10.0;

    public static final AXButtonStyle TRANSPARENT_BUTTON = new AXButtonStyle() {

        @Override
        public Color getBGColor() {
            return Color.TRANSPARENT;
        }

        @Override
        public Color getHoverShadow() {
            return MAIN_BASE_HOVER_COLOR;
        }

        @Override
        public Color getPressedShadow() {
            return MAIN_BASE_PRESSED_COLOR;
        }

        @Override
        public Color getBorderColor() {
            return Color.TRANSPARENT;
        }

        @Override
        public double getAnimeRate() {
            return 1;
        }

        @Override
        public double getBorderRadius() {
            return 0;
        }

        @Override
        public double getBorderArcSize() {
            return MAIN_BORDER_ARC_SIZE;
        }

        @Override
        public Color getTextColor() {
            return MAIN_TEXT_COLOR;
        }

        @Override
        public Color getTextHoverColor() {
            return MAIN_TEXT_HOVER_COLOR;
        }

        @Override
        public Color getFillColor() {
            return MAIN_BUTTON_FILL_COLOR;
        }

        @Override
        public Font getTextFont() {
            return STANDARD_FONT;
        }
    };

    public static final AXButtonStyle NORMAL_BUTTON = new AXButtonStyle() {

        @Override
        public Color getBGColor() {
            return MAIN_BASE_COLOR;
        }

        @Override
        public Color getHoverShadow() {
            return MAIN_BASE_HOVER_COLOR;
        }

        @Override
        public Color getPressedShadow() {
            return MAIN_BASE_PRESSED_COLOR;
        }

        @Override
        public Color getBorderColor() {
            return THEME_COLOR;
        }

        @Override
        public double getAnimeRate() {
            return 1;
        }

        @Override
        public double getBorderRadius() {
            return 1;
        }

        @Override
        public double getBorderArcSize() {
            return MAIN_BORDER_ARC_SIZE;
        }

        @Override
        public Color getTextColor() {
            return MAIN_TEXT_COLOR;
        }

        @Override
        public Color getTextHoverColor() {
            return MAIN_TEXT_HOVER_COLOR;
        }

        @Override
        public Color getFillColor() {
            return MAIN_BUTTON_FILL_COLOR;
        }

        @Override
        public Font getTextFont() {
            return STANDARD_FONT;
        }
    };

    public static final AXContextPaneStyle CONTEXT_PANE = new DefaultAXContextPaneStyle();

    public static final AXButtonStyle SELECTED_BUTTON = new DefaultAXButtonGroupStyle();

    public static final AXTextFieldStyle TRANSPARENT_TEXT_FIELD = new AXTextFieldStyle() {
        @Override
        public AXContextPaneStyle getContextPaneStyle() {
            return CONTEXT_PANE;
        }

        @Override
        public Color getLineColor() {
            return THEME_COLOR;
        }

        @Override
        public Color getTextColor() {
            return MAIN_TEXT_COLOR;
        }

        @Override
        public Color getTextSelectedColor() {
            return Color.LIGHTBLUE;
        }

        @Override
        public Color getTextSelectedBGColor() {
            return Color.BLUE;
        }

        @Override
        public Font getTextFont() {
            return STANDARD_FONT;
        }

        @Override
        public double getAreaInsets() {
            return 5.0;
        }

        @Override
        public double getLineWeight() {
            return 1.0;
        }

        @Override
        public double getLineInsets() {
            return 2.5;
        }

        @Override
        public Color getBGColor() {
            return Color.TRANSPARENT;
        }

        @Override
        public Color getHoverShadow() {
            return Color.TRANSPARENT;
        }

        @Override
        public Color getPressedShadow() {
            return Color.TRANSPARENT;
        }

        @Override
        public Color getBorderColor() {
            return Color.TRANSPARENT;
        }

        @Override
        public double getAnimeRate() {
            return 1.0;
        }

        @Override
        public double getBorderRadius() {
            return 1.0;
        }

        @Override
        public double getBorderArcSize() {
            return 10.0;
        }
    };

}
