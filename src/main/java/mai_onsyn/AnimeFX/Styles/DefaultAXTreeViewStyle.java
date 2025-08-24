package mai_onsyn.AnimeFX.Styles;

import javafx.scene.paint.Color;
import mai_onsyn.AnimeFX.ResourceManager;

public class DefaultAXTreeViewStyle extends DefaultAXBaseStyle implements AXTreeViewStyle {
    @Override
    public AXTreeItemStyle getRootItemStyle() {
        DefaultAXTreeItemStyle style = new DefaultAXTreeItemStyle();
        style.setIcon(ResourceManager.fork);
        style.setBorderRadius(0);
        style.setBorderColor(Color.TRANSPARENT);
        style.setBGColor(Color.TRANSPARENT);
        return style;
    }

    @Override
    public AXTreeItemStyle getFolderItemStyle() {
        DefaultAXTreeItemStyle style = new DefaultAXTreeItemStyle();
        style.setIcon(ResourceManager.folder);
        style.setBorderRadius(0);
        style.setBorderColor(Color.TRANSPARENT);
        style.setBGColor(Color.TRANSPARENT);
        return style;
    }

    @Override
    public AXTreeItemStyle getFileItemStyle() {
        DefaultAXTreeItemStyle style = new DefaultAXTreeItemStyle();
        style.setIcon(ResourceManager.file);
        style.setBorderRadius(0);
        style.setBorderColor(Color.TRANSPARENT);
        style.setBGColor(Color.TRANSPARENT);
        return style;
    }

    @Override
    public AXButtonStyle getSelectedButtonStyle() {
        return new DefaultAXButtonGroupStyle();
    }

    @Override
    public AXContextPaneStyle getContextMenuStyle() {
        return new DefaultAXContextPaneStyle();
    }

    @Override
    public AXTextInputPopupStyle getTextInputPopupStyle() {
        return new DefaultAXTextInputPopupStyle();
    }

    @Override
    public double getContentInsets() {
        return 10;
    }

    @Override
    public Color getHoverShadow() {
        return Color.TRANSPARENT;
    }

    @Override
    public Color getPressedShadow() {
        return Color.TRANSPARENT;
    }
}
