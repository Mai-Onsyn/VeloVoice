package mai_onsyn.AnimeFX2.Styles;

import javafx.scene.paint.Color;

public class DefaultAXBackGroundStyle implements AXBackGroundStyle{

    private Color bgShadow = Color.rgb(0, 0, 0, 0.5);
    private double animeRate = 1.0;
    private double bgOpacity = 1.0;

    @Override
    public Color getBGShadow() {
        return bgShadow;
    }

    @Override
    public double getAnimeRate() {
        return animeRate;
    }

    @Override
    public double getBGOpacity() {
        return bgOpacity;
    }

    public void setBgShadow(Color bgShadow) {
        this.bgShadow = bgShadow;
    }

    public void setAnimeRate(double animeRate) {
        this.animeRate = animeRate;
    }

    public void setBgOpacity(double bgOpacity) {
        this.bgOpacity = bgOpacity;
    }
}
