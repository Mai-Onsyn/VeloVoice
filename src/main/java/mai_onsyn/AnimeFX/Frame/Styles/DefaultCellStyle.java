package mai_onsyn.AnimeFX.Frame.Styles;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import mai_onsyn.AnimeFX.Frame.Module.Assistant.Cell;
import mai_onsyn.AnimeFX.Frame.Module.SmoothTreeView;

public class DefaultCellStyle implements CellStyle {
    @Override
    public Cell createFileCell(String name, SmoothTreeView treeView) {
        Cell cell = new Cell(20, 15, new Image("textures/file_light.png"), name)
                .bgColor(Color.TRANSPARENT)
                .borderShape(20)
                .init();
        cell.setType(true);
        return cell;
    }

    @Override
    public Cell createFolderCell(String name, SmoothTreeView treeView) {
        return new Cell(20, 15, new Image("textures/folder_light.png"), name)
                .bgColor(Color.TRANSPARENT)
                .borderShape(20)
                .init();
    }

    @Override
    public Cell createRootCell(String name, SmoothTreeView treeView) {
        return new Cell(20, 15, new Image("textures/root_light.png"), name)
                .bgColor(Color.TRANSPARENT)
                .borderShape(20)
                .init();
    }
}
