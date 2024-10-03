package mai_onsyn.AnimeFX2.Styles;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public interface AXTextFieldStyle extends AXBaseStyle{
    Color getLineColor();
    Color getTextColor();
    Color getTextSelectedColor();
    Color getTextSelectedBGColor();
    Font getTextFont();
    double getAreaInsets();
    double getLineWeight();
    double getLineInsets();
}
