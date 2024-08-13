package mai_onsyn.AnimeFX.Frame.Styles;

import mai_onsyn.AnimeFX.Frame.Module.Assistant.Cell;
import mai_onsyn.AnimeFX.Frame.Module.SmoothTreeView;

public interface CellStyle {
    Cell createFileCell(String name, SmoothTreeView treeView);
    Cell createFolderCell(String name, SmoothTreeView treeView);
    Cell createRootCell(String name, SmoothTreeView treeView);
}
