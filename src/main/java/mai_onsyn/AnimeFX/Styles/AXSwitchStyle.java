package mai_onsyn.AnimeFX.Styles;

public interface AXSwitchStyle{

    AXBaseStyle getThumbStyle();
    AXBaseStyle getSwitchedThumbStyle();
    AXBaseStyle getTrackStyle();
    AXBaseStyle getSwitchedTrackStyle();

    double getThumbInsetsScale();
    double getHoveredScale();
    double getClickedScale();
    double getPressedScale();

    double getAnimeRate();
}
