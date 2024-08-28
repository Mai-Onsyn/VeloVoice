package mai_onsyn.AnimeFX.Frame.Module;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import mai_onsyn.AnimeFX.Frame.Layout.AutoPane;
import mai_onsyn.AnimeFX.Frame.Layout.NamePopup;
import mai_onsyn.AnimeFX.Frame.Layout.PopupMenu;
import mai_onsyn.AnimeFX.Frame.Module.Assistant.*;
import mai_onsyn.AnimeFX.Frame.Styles.*;
import mai_onsyn.AnimeFX.Frame.Utils.DiffusionButtonGroup;
import mai_onsyn.AnimeFX.Frame.Utils.Toolkit;

import java.util.*;

public class SmoothTreeView extends AutoPane {

    private double width = 300;
    private double height = 500;
    private int animeDuration = 100;
    private Color bgColor = new Color(1, 1, 1, 0.5);
    private Color highlightTextFocusColor = new Color(0.9, 0.6, 0.6, 0.8);
    private Color highlightBGColor = new Color(0.1, 0.2, 1, 0.5);
    private Color highlightTextColor = new Color(0.5, 1, 0.5, 0.5);
    private Color highlightBGFocusColor = new Color(0.39215687f, 0.58431375f, 0.92941177f, 0.5);
    private ButtonStyle popupButtonStyle = new DefaultButtonStyle();
    private NamePopupStyle namePopupStyle = new DefaultNamePopupStyle();

    private final Rectangle border = new Rectangle(0, 0, new Color(0, 0, 0, 0));
    private final Rectangle borderClip = new Rectangle();

    public SmoothTreeView borderRadius(double r) {
        this.border.setStrokeWidth(r);
        this.borderClip.setStrokeWidth(r);
        return this;
    }
    public SmoothTreeView borderColor(Color color) {
        this.border.setStroke(color);
        this.borderClip.setStroke(color);
        return this;
    }
    public SmoothTreeView borderShape(double v1) {
        border.setArcWidth(v1);
        border.setArcHeight(v1);
        borderClip.setArcWidth(v1);
        borderClip.setArcHeight(v1);
        return this;
    }
    public SmoothTreeView width(double width) {
        this.width = width;
        return this;
    }
    public SmoothTreeView height(double height) {
        this.height = height;
        return this;
    }
    public SmoothTreeView animeDuration(int animeDuration) {
        this.animeDuration = animeDuration;
        return this;
    }
    public SmoothTreeView highlightBGColor(Color highlightBGColor) {
        this.highlightBGColor = highlightBGColor;
        return this;
    }
    public SmoothTreeView highlightTextFocusColor(Color highlightTextFocusColor) {
        this.highlightTextFocusColor = highlightTextFocusColor;
        return this;
    }
    public SmoothTreeView highlightBGFocusColor(Color highlightBGFocusColor) {
        this.highlightBGFocusColor = highlightBGFocusColor;
        return this;
    }
    public SmoothTreeView highlightTextColor(Color highlightTextColor) {
        this.highlightTextColor = highlightTextColor;
        return this;
    }
    public SmoothTreeView bgColor(Color bgColor) {
        this.bgColor = bgColor;
        return this;
    }
    public SmoothTreeView popupButtonStyle(ButtonStyle style) {
        this.popupButtonStyle = style;
        return this;
    }
    public SmoothTreeView namePopupStyle(NamePopupStyle style) {
        this.namePopupStyle = style;
        return this;
    }

    private final DiffusionButtonGroup group = new DiffusionButtonGroup();
    private final Item<Cell> root;
    private final VBox box = new VBox();
    private final ScrollPane scrollPane = new ScrollPane(box);
    private final CellStyle cellStyle;

    public SmoothTreeView(CellStyle style) {
        this.cellStyle = style;
        Cell rootCell = style.createRootCell("ROOT", this);
        root = new Item<>(rootCell);
        group.add(rootCell);
    }

    public SmoothTreeView init() {
        super.setClip(borderClip);

        border.setFill(bgColor);

        group.setSwitchedColor(highlightBGColor);
        group.setSwitchedFocusColor(highlightBGFocusColor);
        group.setSwitchedTextColor(highlightTextColor);
        group.setSwitchedTextFocusColor(highlightTextFocusColor);


        super.widthProperty().addListener((o, ov, nv) -> {
            width = nv.doubleValue();

            border.setWidth(width);
            borderClip.setWidth(width);
        });

        super.heightProperty().addListener((o, ov, nv) -> {
            height = nv.doubleValue();

            border.setHeight(height);
            borderClip.setHeight(height);
        });

        root.getData().unfold();
        updateTree();
        initCell(root.getData());

        scrollPane.getStylesheets().add("styles/scroll-pane.css");
        Toolkit.addSmoothScrolling(scrollPane);

        super.setPosition(scrollPane, false, border.getArcWidth() / 3, border.getArcWidth() / 3, border.getArcHeight() / 3, border.getArcHeight() / 3);
        super.getChildren().addAll(border, scrollPane);

        return this;
    }

    private final Map<Cell, VBox> treeChildrenMap = new HashMap<>();        //当前的Cell所在Pane同级的装有子项的VBox
    private final Map<Cell, Pane> treeParentBoxMap = new HashMap<>();       //当前的Cell所在的Pane
    private final Map<Pane, VBox> treeParentPaneMap = new HashMap<>();      //装有该Pane的上级VBox
    private final Map<VBox, Pane> treeParentBoxPaneMap = new HashMap<>();   //当前VBox所在的Pane
    private void updateTree() {
        box.getChildren().clear();
        treeChildrenMap.clear();
        treeParentBoxMap.clear();
        treeParentPaneMap.clear();
        treeParentBoxPaneMap.clear();
        layoutTree(root, box);
    }
    private void layoutTree(Item<Cell> item, VBox parent) {
        parent.setLayoutX(root.getData().getSize());
        parent.setLayoutY(root.getData().getSize());
        Cell brunch = item.getData();
        Pane trunk = new Pane();
        VBox fruit = new VBox();
        if (brunch.isFold()) {
            fruit.setVisible(false);
            fruit.setSpacing(-root.getData().getSize());
            fruit.setLayoutY(0);
        }
        parent.getChildren().addAll(trunk);
        trunk.getChildren().addAll(brunch, fruit);
        treeChildrenMap.put(brunch, fruit);
        treeParentBoxMap.put(brunch, trunk);
        treeParentPaneMap.put(trunk, parent);
        treeParentBoxPaneMap.put(fruit, trunk);

        for (int i = 0; i < item.getChildren().size(); i++) {
            layoutTree(item.getChildren().get(i), fruit);
        }
    }

    private VBox lookupChildrenCell(Cell cell) {
        return treeChildrenMap.get(cell);
    }
    private VBox lookupParentBoxCell(Pane pane) {
        return treeParentPaneMap.get(pane);
    }
    private Pane lookupParentPaneCell(VBox vBox) {
        return treeParentBoxPaneMap.get(vBox);
    }
    private Pane lookupParentPaneCell(Cell cell) {
        return treeParentBoxMap.get(cell);
    }

    private void addFoldListener(Cell cell) {
        cell.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getClickCount() == 2) {
                updateFold(cell, false);
            }
        });
    }

    private void updateFold(Cell cell, boolean fold) {
        VBox vBox = lookupChildrenCell(cell);
        if (fold || !cell.isFold()) {
            Timeline spacingTrans = new Timeline(new KeyFrame(Duration.millis(animeDuration), new KeyValue(vBox.spacingProperty(), -root.getData().getSize())));
            Timeline layoutTrans = new Timeline(new KeyFrame(Duration.millis(animeDuration), new KeyValue(vBox.layoutYProperty(), 0)));
            Timeline opacityTrans = new Timeline(new KeyFrame(Duration.millis(animeDuration), new KeyValue(vBox.opacityProperty(), 0)));
            spacingTrans.play();
            layoutTrans.play();
            opacityTrans.play();
            spacingTrans.setOnFinished(e -> {
                vBox.setVisible(false);
                cell.fold();
            });
        }
        else {
            vBox.setVisible(true);
            Timeline spacingTrans = new Timeline(new KeyFrame(Duration.millis(animeDuration), new KeyValue(vBox.spacingProperty(), 0)));
            Timeline layoutTrans = new Timeline(new KeyFrame(Duration.millis(animeDuration), new KeyValue(vBox.layoutYProperty(), root.getData().getSize())));
            Timeline opacityTrans = new Timeline(new KeyFrame(Duration.millis(animeDuration), new KeyValue(vBox.opacityProperty(), 1)));
            spacingTrans.play();
            layoutTrans.play();
            opacityTrans.play();
            spacingTrans.setOnFinished(e -> cell.unfold());
        }
        for (Node child : vBox.getChildren()) {
            if (((Pane) child).getChildren().getFirst() instanceof Cell childCell) {
                if (!childCell.isFold()) updateFold(childCell, true);
            }
        }
    }

    public void addItem(Cell target, Cell children) {
        Item<Cell> item = root.lookup(target);
        if (item == null) return;

        item.add(new Item<>(children));
        group.add(children);

        add(target, children);
        initCell(children);
    }

    private void add(Cell target, Cell cell) {
        VBox children = lookupChildrenCell(target);
//        if (true) {
//            Random r = new Random();
//            children.setStyle("-fx-background-color: #" + Toolkit.colorToString(new Color(r.nextDouble(), r.nextDouble(), r.nextDouble(), 0.5)));
//        }

        Pane trunk = new Pane();
        VBox fruit = new VBox();
        children.setLayoutX(root.getData().getSize());
        children.setLayoutY(root.getData().getSize());
        children.getChildren().addAll(trunk);
        trunk.getChildren().addAll(cell, fruit);

        treeChildrenMap.put(cell, fruit);
        treeParentBoxMap.put(cell, trunk);
        treeParentPaneMap.put(trunk, children);
        treeParentBoxPaneMap.put(fruit, trunk);
    }

    private void initCell(Cell cell) {
        addFoldListener(cell);
        {
            PopupMenu menu = new PopupMenu(50)
                    .borderShape(20);

            if (!cell.isFile()) {
                DiffusionButton createDirButton = popupButtonStyle.createButton("新建目录");
                createDirButton.setOnMouseClicked(event -> {
                    NamePopup namePopup = new NamePopup("新建目录", 200, 100)
                            .borderShape(20)
                            .popupStyle(namePopupStyle)
                            .init();
                    namePopup.showOnCenter();
                    menu.hide();
                    namePopup.setOnTextAvailable((o, ov, text) -> {
                        if (cell.isFold()) {
                            updateFold(cell, false);
                        }
                        Cell brunch = cellStyle.createFolderCell(text, this);
                        brunch.setType(false);
                        add(cell, brunch);
                        group.add(brunch);
                        root.lookup(cell).add(new Item<>(brunch));
                        addFoldListener(brunch);
                        initCell(brunch);
                    });
                });

                DiffusionButton createFileButton = popupButtonStyle.createButton("新建文件");
                createFileButton.setOnMouseClicked(event -> {
                    NamePopup namePopup = new NamePopup("新建文件", 200, 100)
                            .borderShape(20)
                            .popupStyle(namePopupStyle)
                            .init();
                    namePopup.showOnCenter();
                    menu.hide();
                    namePopup.setOnTextAvailable((o, ov, text) -> {
                        if (cell.isFold()) {
                            updateFold(cell, false);
                        }
                        Cell brunch = cellStyle.createFileCell(text, this);
                        brunch.setType(true);
                        add(cell, brunch);
                        group.add(brunch);
                        root.lookup(cell).add(new Item<>(brunch));
                        addFoldListener(brunch);
                        initCell(brunch);
                    });
                });

                menu.addItem(new HBox(createFileButton), new HBox(createDirButton));
            }

            DiffusionButton renameButton = popupButtonStyle.createButton("重命名");
            renameButton.setOnMouseClicked(event -> {
                NamePopup namePopup = new NamePopup("重命名", 200, 100)
                        .borderShape(20)
                        .popupStyle(namePopupStyle)
                        .init();

                namePopup.getTextField().setText(cell.getText());
                namePopup.getTextField().selectAll();
                namePopup.showOnCenter();
                menu.hide();
                namePopup.setOnTextAvailable((o, ov, text) -> {
                    cell.setText(text);
                });
            });
            menu.addItem(new HBox(renameButton));

            DiffusionButton deleteButton;
            if (cell != root.getData()) {
                deleteButton = popupButtonStyle.createButton("删除");

                deleteButton.setOnMouseClicked(event -> {
                    root.lookup(cell).getChildren().clear();
                    delete(cell);
                    menu.hide();
                    System.gc();
//                    System.out.println(treeChildrenMap.size());
//                    System.out.println(treeParentPaneMap.size());
//                    System.out.println(treeParentBoxMap.size());
//                    System.out.println(treeParentBoxPaneMap.size());
                });
            }
            else {
                deleteButton = popupButtonStyle.createButton("清空列表");

                deleteButton.setOnMouseClicked(event -> {
                    List<Item<Cell>> itemsToDelete = new ArrayList<>(root.getChildren());
                    for (Item<Cell> item : itemsToDelete) {
                        delete(item.getData());
                    }
                    root.getChildren().clear(); // 清空root的子项
                    menu.hide();
                    System.gc();
//                    System.out.println(treeChildrenMap.size());
//                    System.out.println(treeParentPaneMap.size());
//                    System.out.println(treeParentBoxMap.size());
//                    System.out.println(treeParentBoxPaneMap.size());
                });
            }
            menu.addItem(new HBox(deleteButton));

            menu.bind(cell);
        }
    }

    //ChatGPT写的
    public void delete(Cell cell) {
        // 获取当前Cell的子项容器
        VBox childrenVBox = lookupChildrenCell(cell);

        if (childrenVBox != null) {
            // 用一个临时列表来保存需要删除的子Pane
            List<Pane> childPanesToDelete = new ArrayList<>();

            // 收集所有子Pane
            for (Node childPane : childrenVBox.getChildren()) {
                if (childPane instanceof Pane) {
                    childPanesToDelete.add((Pane) childPane);
                }
            }

            // 在循环外删除所有子Pane对应的Cell
            for (Pane childPane : childPanesToDelete) {
                Cell childCell = (Cell) childPane.getChildren().get(0);
                delete(childCell);  // 递归删除子项
            }
        }

        // 从group中移除当前Cell
        group.remove(cell);

        // 从父级Pane中移除当前Cell的Pane
        Pane parentPane = lookupParentPaneCell(cell);
        VBox parentVBox = lookupParentBoxCell(parentPane);

        if (parentVBox != null && parentPane != null) {
            parentVBox.getChildren().remove(parentPane);
        }

        // 从树结构中移除
        if (parentVBox != null) {
            Pane grandParentPane = lookupParentPaneCell(parentVBox);
            if (grandParentPane != null) {
                Cell parent = (Cell) grandParentPane.getChildren().get(0);
                Item<Cell> parentItem = root.lookup(parent);
                if (parentItem != null) {
                    parentItem.getChildren().remove(new Item<>(cell));
                }
            }
        }

        // 清理四个Map中的引用
        treeChildrenMap.remove(cell);
        treeParentBoxMap.remove(cell);
        treeParentPaneMap.remove(parentPane);
        treeParentBoxPaneMap.remove(childrenVBox);


        // 从root中移除该项
        //root.getChildren().removeIf(item -> item.getData() == cell);
        Item.delete(root, root.lookup(cell));
    }
    //我写的
//    public void delete(Cell cell) {
//        group.remove(cell);
//        lookupParentBoxCell(lookupParentPaneCell(cell)).getChildren().remove(lookupParentPaneCell(cell));
//        Cell parent = (Cell) lookupParentPaneCell(lookupParentBoxCell(lookupParentPaneCell(cell))).getChildren().getFirst();
//        root.lookup(parent).getChildren().remove(new Item<>(cell));
//
//        lookupParentBoxCell(lookupParentPaneCell(cell)).getChildren().remove(cell);
//    }

    public Item<Cell> getRoot() {
        return root;
    }

    public CellStyle getCellStyle() {
        return cellStyle;
    }

    public DiffusionButtonGroup getGroup() {
        return group;
    }
}