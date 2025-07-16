package mai_onsyn.AnimeFX.Styles;

import javafx.scene.image.Image;

public interface AXChoiceBoxStyle extends AXButtonStyle {

    double getSignalRelateX();
    double getSignalScale();
    RelativePosition getSignalRelate();
    Image getSignalImage();

    AXContextPaneStyle getContextPaneStyle();
    AXButtonStyle getFreeButtonStyle();
    AXButtonStyle getSelectedButtonStyle();


    enum RelativePosition {
        LEFT, RIGHT
    }

}
