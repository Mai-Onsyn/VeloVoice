package mai_onsyn.AnimeFX.Styles;

import javafx.scene.paint.Color;

public class DefaultAXSwitchStyle implements AXSwitchStyle{

    private double thumbInsetsScale = 0.1;
    private double hoveredScale = 1.1;
    private double clickedScale = 0.8;
    private double pressedScale = 0.9;
    private double borderArcSize = 20;

    private double animeRate = 1.0;


    @Override
    public AXBaseStyle getThumbStyle() {
        DefaultAXBaseStyle style = new DefaultAXBaseStyle();
        style.setBorderArcSize(borderArcSize);
        style.setBGColor(Color.rgb(25, 69, 94, 0.8));
        return style;
    }

    @Override
    public AXBaseStyle getSwitchedThumbStyle() {
        DefaultAXBaseStyle style = new DefaultAXBaseStyle();
        style.setBorderArcSize(borderArcSize);
        style.setBGColor(Color.rgb(255, 255, 255, 0.8));
        return style;
    }

    @Override
    public AXBaseStyle getTrackStyle() {
        DefaultAXBaseStyle style = new DefaultAXBaseStyle();
        style.setBorderArcSize(borderArcSize);
        return style;
    }

    @Override
    public AXBaseStyle getSwitchedTrackStyle() {
        DefaultAXBaseStyle style = new DefaultAXBaseStyle();
        style.setBorderArcSize(borderArcSize);
        style.setBGColor(Color.rgb(25, 69, 94, 0.5));
        return style;
    }

    @Override
    public double getThumbInsetsScale() {
        return thumbInsetsScale;
    }

    @Override
    public double getHoveredScale() {
        return hoveredScale;
    }

    @Override
    public double getClickedScale() {
        return clickedScale;
    }

    @Override
    public double getPressedScale() {
        return pressedScale;
    }

    @Override
    public double getAnimeRate() {
        return animeRate;
    }
}
