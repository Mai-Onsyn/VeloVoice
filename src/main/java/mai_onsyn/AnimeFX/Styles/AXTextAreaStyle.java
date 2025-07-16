package mai_onsyn.AnimeFX.Styles;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public interface AXTextAreaStyle extends AXBaseStyle {

    AXContextPaneStyle getContextMenuStyle();

    Color getTextColor();
    Color getTextSelectedColor();
    Color getTextSelectedBGColor();
    Font getTextFont();
    double getAreaInsets();
}
