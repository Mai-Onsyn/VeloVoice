package mai_onsyn.AnimeFX2.Styles;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public interface AXTextInputPopupStyle extends AXBaseStyle{
    AXTextFieldStyle getTextFieldStyle();

    Font getTextFont();
    Color getTextColor();
    double getWidth();
    double getHeight();

    double getTextFieldHeight();
}
