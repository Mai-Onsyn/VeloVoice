package mai_onsyn.AnimeFX.Styles;

public interface AXSliderStyle {
    AXProgressBarStyle getTrackStyle();
    AXBaseStyle getThumbStyle();
    double getThumbWidth();
    double getThumbHeight();
    double getTrackHeight();

    double getHoveredScale();
    double getClickedScale();
    double getPressedScale();

    double getAnimeRate();
}
