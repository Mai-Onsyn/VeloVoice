package mai_onsyn.AnimeFX.Styles;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class DefaultAXTextInputPopupStyle extends DefaultAXBaseStyle implements AXTextInputPopupStyle {
    @Override
    public AXTextFieldStyle getTextFieldStyle() {
        return new DefaultAXTextFieldStyle();
    }

    @Override
    public Font getTextFont() {
        return new Font(13);
    }

    @Override
    public Color getTextColor() {
        return Color.BLACK;
    }

    @Override
    public double getWidth() {
        return 350;
    }

    @Override
    public double getHeight() {
        return 150;
    }

    @Override
    public double getTextFieldHeight() {
        return 25;
    }

    @Override
    public Color getHoverShadow() {
        return Color.TRANSPARENT;
    }

    @Override
    public Color getPressedShadow() {
        return Color.TRANSPARENT;
    }
}
