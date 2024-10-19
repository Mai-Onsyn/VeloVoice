package mai_onsyn.AnimeFX2.Styles;

import javafx.scene.image.Image;

public interface AXTreeItemStyle extends AXButtonStyle{

    Image getIcon();
    double getIconInsets();
    double getTextLeftInsets();
    double getTextRightInsets();
    double getChildrenInsets();
    double getItemHeight();
}
