package mai_onsyn.AnimeFX2.Styles;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class DefaultAXButtonStyle extends DefaultAXBaseStyle implements AXButtonStyle {

    private Color textColor = Color.BLACK;
    private Color textHoverColor = Color.RED;
    private Color fillColor = Color.rgb(128, 128, 255, 0.5);
    private Font textFont = new Font(12);

    @Override
    public Color getTextColor() {
        return textColor;
    }

    @Override
    public Color getTextHoverColor() {
        return textHoverColor;
    }

    @Override
    public Color getFillColor() {
        return fillColor;
    }

    @Override
    public Font getTextFont() {
        return textFont;
    }


    public void setTextColor(Color color) {
        this.textColor = color;
    }

    public void setTextHoverColor(Color color) {
        this.textHoverColor = color;
    }

    public void setTextFont(Font font) {
        this.textFont = font;
    }

    public void setFillColor(Color color) {
        this.fillColor = color;
    }
}
