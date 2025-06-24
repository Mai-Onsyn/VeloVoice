package mai_onsyn.AnimeFX2.Styles;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class DefaultAXInlineTextAreaStyle extends DefaultAXBaseStyle implements AXInlineTextAreaStyle {

    private Color textSelectedBGColor = Color.LIGHTBLUE;
    private double areaInsets = 5.0;
    private Color hoverShadow = Color.rgb(128, 128, 128, 0);
    private Color pressedShadow = Color.rgb(64, 64, 64, 0);

    @Override
    public AXContextPaneStyle getContextMenuStyle() {
        return new DefaultAXContextPaneStyle();
    }

    @Override
    public Color getTextSelectedBGColor() {
        return textSelectedBGColor;
    }

    @Override
    public double getAreaInsets() {
        return areaInsets;
    }

    @Override
    public Color getHoverShadow() {
        return hoverShadow;
    }

    @Override
    public Color getPressedShadow() {
        return pressedShadow;
    }

    public void setTextSelectedBGColor(Color textSelectedBGColor) {
        this.textSelectedBGColor = textSelectedBGColor;
    }

    public void setAreaInsets(double areaInsets) {
        this.areaInsets = areaInsets;
    }

    public void setHoverShadow(Color color) {
        this.hoverShadow = color;
    }

    public void setPressedShadow(Color color) {
        this.pressedShadow = color;
    }
}
