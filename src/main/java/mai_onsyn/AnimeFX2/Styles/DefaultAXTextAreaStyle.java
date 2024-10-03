package mai_onsyn.AnimeFX2.Styles;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class DefaultAXTextAreaStyle extends DefaultAXBaseStyle implements AXTextAreaStyle {

    private Color textColor = Color.BLACK;
    private Color textSelectedColor = Color.HOTPINK;
    private Color textSelectedBGColor = Color.LIGHTBLUE;
    private Font textFont = new Font(12);
    private double areaInsets = 5.0;

    @Override
    public Color getTextColor() {
        return textColor;
    }

    @Override
    public Color getTextSelectedColor() {
        return textSelectedColor;
    }

    @Override
    public Color getTextSelectedBGColor() {
        return textSelectedBGColor;
    }

    @Override
    public Font getTextFont() {
        return textFont;
    }

    @Override
    public double getAreaInsets() {
        return areaInsets;
    }

    public void setTextColor(Color textColor) {
        this.textColor = textColor;
    }

    public void setTextSelectedColor(Color textSelectedColor) {
        this.textSelectedColor = textSelectedColor;
    }

    public void setTextSelectedBGColor(Color textSelectedBGColor) {
        this.textSelectedBGColor = textSelectedBGColor;
    }

    public void setAreaInsets(double areaInsets) {
        this.areaInsets = areaInsets;
    }

    public void setTextFont(Font textFont) {
        this.textFont = textFont;
    }
}
