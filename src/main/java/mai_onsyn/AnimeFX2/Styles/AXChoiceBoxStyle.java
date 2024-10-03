package mai_onsyn.AnimeFX2.Styles;

import javafx.scene.image.Image;

public interface AXChoiceBoxStyle extends AXBaseStyle {

    double getSignalRelateX();
    double getSignalScale();
    RelativePosition getSignalRelate();
    Image getSignalImage();


    enum RelativePosition {
        LEFT, RIGHT
    }

}
