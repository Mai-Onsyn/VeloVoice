package mai_onsyn.AnimeFX2.Styles;

import javafx.scene.paint.Color;

public class DefaultAXBaseStyle implements AXBaseStyle{


    private Color bgColor = Color.rgb(192, 192, 192, 0.5);
    private Color hoverShadow = Color.rgb(128, 128, 128, 0.5);
    private Color pressedShadow = Color.rgb(64, 64, 64, 0.5);
    private Color borderColor = Color.CORNFLOWERBLUE;
    private double animeRate = 1.0;
    private double borderRadius = 1.0;
    private double borderArcSize = 10.0;

    @Override
    public Color getBGColor() {
        return bgColor;
    }

    @Override
    public Color getHoverShadow() {
        return hoverShadow;
    }

    @Override
    public Color getPressedShadow() {
        return pressedShadow;
    }

    @Override
    public Color getBorderColor() {
        return borderColor;
    }

    @Override
    public double getAnimeRate() {
        return animeRate;
    }

    @Override
    public double getBorderRadius() {
        return borderRadius;
    }

    @Override
    public double getBorderArcSize() {
        return borderArcSize;
    }



    public void setBGColor(Color color) {
        this.bgColor = color;
    }

    public void setHoverShadow(Color color) {
        this.hoverShadow = color;
    }

    public void setPressedShadow(Color color) {
        this.pressedShadow = color;
    }

    public void setBorderColor(Color color) {
        this.borderColor = color;
    }

    public void setAnimeRate(double rate) {
        this.animeRate = rate;
    }

    public void setBorderRadius(double radius) {
        this.borderRadius = radius;
    }

    public void setBorderArcSize(double arcSize) {
        this.borderArcSize = arcSize;
    }
}
