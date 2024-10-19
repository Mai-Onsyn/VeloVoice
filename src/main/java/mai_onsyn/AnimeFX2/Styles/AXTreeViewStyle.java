package mai_onsyn.AnimeFX2.Styles;

public interface AXTreeViewStyle extends AXBaseStyle {
    AXTreeItemStyle getRootItemStyle();
    AXTreeItemStyle getFolderItemStyle();
    AXTreeItemStyle getFileItemStyle();

    AXContextPaneStyle getContextMenuStyle();

    double getContentInsets();
}
