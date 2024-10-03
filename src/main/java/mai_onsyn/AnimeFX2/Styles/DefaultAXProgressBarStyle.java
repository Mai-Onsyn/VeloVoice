package mai_onsyn.AnimeFX2.Styles;

import javafx.scene.paint.Color;

public class DefaultAXProgressBarStyle extends DefaultAXBaseStyle implements AXProgressBarStyle {


    private Color innerBGColor = Color.rgb(25, 69, 94, 0.5);
    private Color innerBorderColor = Color.TRANSPARENT;
    private double innerBorderRadius = 0;
    private double innerBorderArcSize = 10.0;
    private double borderInsets = 3;



    @Override
    public Color getInnerBGColor() {
        return innerBGColor;
    }

    @Override
    public Color getInnerBorderColor() {
        return innerBorderColor;
    }

    @Override
    public double getInnerBorderRadius() {
        return innerBorderRadius;
    }

    @Override
    public double getInnerBorderArcSize() {
        return innerBorderArcSize;
    }

    @Override
    public double getBorderInsets() {
        return borderInsets;
    }


    public void setInnerBGColor(Color innerBGColor) {
        this.innerBGColor = innerBGColor;
    }

    public void setInnerBorderColor(Color innerBorderColor) {
        this.innerBorderColor = innerBorderColor;
    }

    public void setInnerBorderRadius(double innerBorderRadius) {
        this.innerBorderRadius = innerBorderRadius;
    }

    public void setInnerBorderArcSize(double innerBorderArcSize) {
        this.innerBorderArcSize = innerBorderArcSize;
    }

    public void setBorderInsets(double borderInsets) {
        this.borderInsets = borderInsets;
    }
}
