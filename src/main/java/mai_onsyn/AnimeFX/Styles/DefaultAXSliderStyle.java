package mai_onsyn.AnimeFX.Styles;

import javafx.scene.paint.Color;

public class DefaultAXSliderStyle implements AXSliderStyle {

    private double thumbWidth = 25;
    private double thumbHeight = 25;
    private double trackHeight = 7.5;
    private double hoveredScale = 1.2;
    private double clickedScale = 0.9;
    private double pressedScale = 1.1;
    private double animeRate = 1.0;


    @Override
    public AXProgressBarStyle getTrackStyle() {
        DefaultAXProgressBarStyle style = new DefaultAXProgressBarStyle();
        style.setBorderColor(Color.TRANSPARENT);
        style.setBorderInsets(1);
        style.setAnimeRate(animeRate);
        return style;
    }

    @Override
    public AXBaseStyle getThumbStyle() {
        DefaultAXBaseStyle style = new DefaultAXBaseStyle();
        style.setBorderArcSize(thumbWidth);
        style.setBGColor(Color.WHITE);
        return style;
    }

    @Override
    public double getThumbWidth() {
        return thumbWidth;
    }

    @Override
    public double getThumbHeight() {
        return thumbHeight;
    }

    @Override
    public double getTrackHeight() {
        return trackHeight;
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
