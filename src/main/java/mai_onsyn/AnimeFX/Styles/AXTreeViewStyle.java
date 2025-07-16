package mai_onsyn.AnimeFX.Styles;

public interface AXTreeViewStyle extends AXBaseStyle {
    AXTreeItemStyle getRootItemStyle();
    AXTreeItemStyle getFolderItemStyle();
    AXTreeItemStyle getFileItemStyle();

    AXButtonStyle getSelectedItemStyle();

    AXContextPaneStyle getContextMenuStyle();
    AXTextInputPopupStyle getTextInputPopupStyle();

    double getContentInsets();
}
