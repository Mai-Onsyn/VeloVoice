package mai_onsyn.AnimeFX.Styles;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class DefaultAXTextFieldStyle extends DefaultAXBaseStyle implements AXTextFieldStyle {

    private Color lineColor = Color.GREEN;
    private Color textColor = Color.BLACK;
    private Color textSelectedColor = Color.HOTPINK;
    private Color textSelectedBGColor = Color.LIGHTBLUE;
    private Font textFont = new Font(12);
    private double areaInsets = 5.0;
    private double lineWeight = 1.0;
    private double lineInsets = 2.5;

    @Override
    public AXContextPaneStyle getContextMenuStyle() {
        return new DefaultAXContextPaneStyle();
    }

    @Override
    public Color getLineColor() {
        return lineColor;
    }

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

    @Override
    public double getLineWeight() {
        return lineWeight;
    }

    @Override
    public double getLineInsets() {
        return lineInsets;
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

    public void setLineColor(Color lineColor) {
        this.lineColor = lineColor;
    }

    public void setLineWeight(double lineWeight) {
        this.lineWeight = lineWeight;
    }

    public void setLineInsets(double lineInsets) {
        this.lineInsets = lineInsets;
    }
}
