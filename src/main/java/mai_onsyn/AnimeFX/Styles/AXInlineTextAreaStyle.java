package mai_onsyn.AnimeFX.Styles;

import javafx.scene.paint.Color;

public interface AXInlineTextAreaStyle extends AXBaseStyle {

    AXContextPaneStyle getContextMenuStyle();

    Color getDefaultTextColor();
    Color getTextSelectedBGColor();
    double getAreaInsets();
}
