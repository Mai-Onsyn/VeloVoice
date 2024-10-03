package Frames;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import mai_onsyn.AnimeFX2.Styles.AXButtonStyle;
import mai_onsyn.AnimeFX2.Utls.Toolkit;

public class TestButtonStyle implements AXButtonStyle {
    @Override
    public Color getBGColor() {
        return Color.TRANSPARENT;
    }

    @Override
    public Color getHoverShadow() {
        return Toolkit.adjustOpacity(Color.CORNFLOWERBLUE, 0.2);
    }

    @Override
    public Color getPressedShadow() {
        return Toolkit.adjustOpacity(Color.CORNFLOWERBLUE, 0.5);
    }

    @Override
    public Color getTextColor() {
        return Color.BLACK;
    }

    @Override
    public Color getTextHoverColor() {
        return Color.GRAY;
    }

    @Override
    public Color getBorderColor() {
        return Color.GREEN;
    }

    @Override
    public Color getFillColor() {
        return Color.LIGHTGREEN;
    }

    @Override
    public Font getTextFont() {
        return new Font(16);
    }

    @Override
    public double getAnimeRate() {
        return 1;
    }

    @Override
    public double getBorderRadius() {
        return 3;
    }

    @Override
    public double getBorderArcSize() {
        return 40;
    }
}
