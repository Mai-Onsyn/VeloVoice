package mai_onsyn.AnimeFX2.Styles;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public interface AXTextAreaStyle extends AXBaseStyle {
    Color getTextColor();
    Color getTextSelectedColor();
    Color getTextSelectedBGColor();
    Font getTextFont();
    double getAreaInsets();
}
