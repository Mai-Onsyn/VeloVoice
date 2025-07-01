package mai_onsyn.AnimeFX2.Styles;

import javafx.scene.image.Image;

public interface AXChoiceBoxStyle extends AXBaseStyle {

    double getSignalRelateX();
    double getSignalScale();
    RelativePosition getSignalRelate();
    Image getSignalImage();

    AXContextPaneStyle getContextPaneStyle();


    enum RelativePosition {
        LEFT, RIGHT
    }

}
