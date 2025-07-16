package mai_onsyn.AnimeFX.Styles;


import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class DefaultAXButtonGroupStyle extends DefaultAXButtonStyle {


    private Color bgColor = Color.rgb(25, 69, 94, 0.5);
    private Color hoverShadow = Color.rgb(128, 128, 128, 0.5);
    private Color pressedShadow = Color.rgb(64, 64, 64, 0.5);
    private Color borderColor = Color.CORNFLOWERBLUE;
    private double animeRate = 1.0;
    private double borderRadius = 1.0;
    private double borderArcSize = 10.0;


    private Color textColor = Color.WHITE;
    private Color textHoverColor = Color.LIGHTBLUE;
    private Color fillColor = Color.rgb(255, 128, 128, 0.5);
    private Font textFont = new Font(12);



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
