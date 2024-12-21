package mai_onsyn.AnimeFX2.Styles;

import javafx.scene.paint.Color;

public interface AXBackGroundStyle {

    Color getBGShadow();
    double getAnimeRate();
    double getBGOpacity();
    double getBGBlurStrength();

    void setBGShadow(Color color);
    void setBGBlurStrength(double strength);
}
