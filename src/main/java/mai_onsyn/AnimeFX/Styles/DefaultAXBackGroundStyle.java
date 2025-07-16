package mai_onsyn.AnimeFX.Styles;

import javafx.scene.paint.Color;

public class DefaultAXBackGroundStyle implements AXBackGroundStyle{

    private Color bgShadow = Color.rgb(0, 0, 0, 0.5);
    private double animeRate = 1.0;
    private double bgOpacity = 1.0;
    private double bgBlur = 10.0;

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

    @Override
    public double getBGBlurStrength() {
        return bgBlur;
    }

    @Override
    public void setBGShadow(Color color) {
        this.bgShadow = color;
    }

    @Override
    public void setBGBlurStrength(double strength) {
        this.bgBlur = strength;
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
