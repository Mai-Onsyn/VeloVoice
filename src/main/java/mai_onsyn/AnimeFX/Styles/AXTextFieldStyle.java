package mai_onsyn.AnimeFX.Styles;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public interface AXTextFieldStyle extends AXBaseStyle{

    AXContextPaneStyle getContextMenuStyle();

    Color getLineColor();
    Color getTextColor();
    Color getTextSelectedColor();
    Color getTextSelectedBGColor();
    Font getTextFont();
    double getAreaInsets();
    double getLineWeight();
    double getLineInsets();
}
