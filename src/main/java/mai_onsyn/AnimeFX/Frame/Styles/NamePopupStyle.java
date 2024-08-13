package mai_onsyn.AnimeFX.Frame.Styles;

import javafx.scene.control.Label;
import javafx.scene.shape.Rectangle;
import mai_onsyn.AnimeFX.Frame.Layout.NamePopup;
import mai_onsyn.AnimeFX.Frame.Module.SmoothTextField;

public interface NamePopupStyle {

    SmoothTextField createTextField();

    Label createDescription(String description);

    Rectangle createBackground(double width, double height);

}