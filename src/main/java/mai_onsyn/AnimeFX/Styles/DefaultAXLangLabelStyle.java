package mai_onsyn.AnimeFX.Styles;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class DefaultAXLangLabelStyle implements AXLangLabelStyle {

    private Color color = Color.BLACK;
    private double animeRate = 1;


    @Override
    public Font getFont() {
        return new Font(12);
    }

    @Override
    public Color getFill() {
        return color;
    }

    @Override
    public double getAnimeRate() {
        return animeRate;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setAnimeRate(double animeRate) {
        this.animeRate = animeRate;
    }
}
