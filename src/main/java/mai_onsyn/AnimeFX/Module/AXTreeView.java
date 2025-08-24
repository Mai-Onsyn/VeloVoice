package mai_onsyn.AnimeFX.Module;

import javafx.application.Platform;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import mai_onsyn.AnimeFX.I18N;
import mai_onsyn.AnimeFX.Localizable;
import mai_onsyn.AnimeFX.ResourceManager;
import mai_onsyn.AnimeFX.Styles.AXTreeViewStyle;
import mai_onsyn.AnimeFX.Styles.DefaultAXTreeViewStyle;
import mai_onsyn.AnimeFX.Utls.*;
import mai_onsyn.AnimeFX.layout.AXContextPane;
import mai_onsyn.AnimeFX.layout.AXTextInputPopup;

import java.util.*;

import static mai_onsyn.AnimeFX.Utls.Toolkit.defaultToolkit;

public class AXTreeView<T> extends AXBase implements Localizable {
    private AXTreeViewStyle style = new DefaultAXTreeViewStyle();
    private final AXTreeItem root = new AXTreeItem("ROOT");
    private final AXDatableButtonGroup<AXTreeItem> group = new AXDatableButtonGroup<>(root.getButton(), root);
    private final TreeItemClipBoard clipBoard;

    private final AXTextInputPopup textInputPopup = new AXTextInputPopup();

    private final AXContextPane rootContextMenu = new AXContextPane();
    private final AXContextPane folderContextMenu = new AXContextPane();
    private final AXContextPane fileContextMenu = new AXContextPane();

    private final List<Localizable> langList = new ArrayList<>();
    private final AXTreeItemDataCreator<T> dataCreator;
    final AXDataTreeItem.AXTreeviewCopyRule<T> copyRule;


    public AXTreeView(AXTreeItemDataCreator<T> dataCreator, AXDataTreeItem.AXTreeviewCopyRule<T> copyRule) {
        this.dataCreator = dataCreator;
        this.copyRule = copyRule;
        clipBoard = new TreeItemClipBoard(copyRule);
        ScrollPane scrollPane = new ScrollPane(root);
        Toolkit.addSmoothScrolling(scrollPane);
        scrollPane.getStylesheets().add("style.css");
        scrollPane.addEventFilter(MouseEvent.MOUSE_PRESSED, super::fireEvent);
        scrollPane.addEventFilter(MouseEvent.MOUSE_RELEASED, super::fireEvent);

        root.setAttribution(this);
        root.setTheme(style.getRootItemStyle());
        root.update(1);

        rootContextMenu.setTheme(style.getContextMenuStyle());
        folderContextMenu.setTheme(style.getContextMenuStyle());
        fileContextMenu.setTheme(style.getContextMenuStyle());

        rootContextMenu.getScrollPane().setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        folderContextMenu.getScrollPane().setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        fileContextMenu.getScrollPane().setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);


        double itemHeight = style.getContextMenuStyle().getItemHeight();
        //RootContextMenu
        {
            AXButton newFile = rootContextMenu.createItem();
            AXButton newFolder = rootContextMenu.createItem();
            AXButton paste = rootContextMenu.createItem();
            AXButton rename = rootContextMenu.createItem();
            AXButton expandAll = rootContextMenu.createItem();
            AXButton clear = rootContextMenu.createItem();

            AXContextPane.setupContextMenuItem(newFile, ResourceManager.fileNew, "New File", "Ctrl+N", itemHeight);
            AXContextPane.setupContextMenuItem(newFolder, ResourceManager.folderNew, "New Folder", "Ctrl+F", itemHeight);
            AXContextPane.setupContextMenuItem(paste, ResourceManager.paste, "Paste", "Ctrl+V", itemHeight);
            AXContextPane.setupContextMenuItem(rename, ResourceManager.rename, "Rename", "F2", itemHeight);
            AXContextPane.setupContextMenuItem(expandAll, ResourceManager.expand, "Expand All", "", itemHeight);
            AXContextPane.setupContextMenuItem(clear, ResourceManager.clear, "Clear", "", itemHeight);

            langList.addAll(List.of(newFile, newFolder, paste, rename, expandAll, clear));

            newFile.addEventHandler(MouseEvent.MOUSE_CLICKED, _ -> newFile());
            newFolder.addEventHandler(MouseEvent.MOUSE_CLICKED, _ -> newFolder());
            paste.addEventHandler(MouseEvent.MOUSE_CLICKED, _ -> paste());
            rename.addEventHandler(MouseEvent.MOUSE_CLICKED, _ -> rename());
            expandAll.addEventHandler(MouseEvent.MOUSE_CLICKED, _ -> expandAll());
            clear.addEventHandler(MouseEvent.MOUSE_CLICKED, _ -> clear());

            root.getButton().addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                if (event.getButton() == MouseButton.SECONDARY) {
                    rootContextMenu.show(this.getScene().getWindow(), event.getScreenX(), event.getScreenY());
                }
            });
        }

        //FolderContextMenu
        {
            AXButton newFile = folderContextMenu.createItem();
            AXButton newFolder = folderContextMenu.createItem();
            AXButton copy = folderContextMenu.createItem();
            AXButton cut = folderContextMenu.createItem();
            AXButton paste = folderContextMenu.createItem();
            AXButton pasteAppend = folderContextMenu.createItem();
            AXButton rename = folderContextMenu.createItem();
            AXButton moveUp = folderContextMenu.createItem();
            AXButton moveDown = folderContextMenu.createItem();
            AXButton expandAll = folderContextMenu.createItem();
            AXButton clear = folderContextMenu.createItem();
            AXButton delete = folderContextMenu.createItem();

            AXContextPane.setupContextMenuItem(newFile, ResourceManager.fileNew, "New File", "Ctrl+N", itemHeight);
            AXContextPane.setupContextMenuItem(newFolder, ResourceManager.folderNew, "New Folder", "Ctrl+F", itemHeight);
            AXContextPane.setupContextMenuItem(copy, ResourceManager.copy, "Copy", "Ctrl+C", itemHeight);
            AXContextPane.setupContextMenuItem(cut, ResourceManager.cut, "Cut", "Ctrl+X", itemHeight);
            AXContextPane.setupContextMenuItem(paste, ResourceManager.paste, "Paste", "Ctrl+V", itemHeight);
            AXContextPane.setupContextMenuItem(pasteAppend, ResourceManager.pasteAppend, "Append Paste", "Ctrl+Shift+V", itemHeight);
            AXContextPane.setupContextMenuItem(rename, ResourceManager.rename, "Rename", "F2", itemHeight);
            AXContextPane.setupContextMenuItem(moveUp, ResourceManager.up, "Move Up", "Ctrl+↑", itemHeight);
            AXContextPane.setupContextMenuItem(moveDown, ResourceManager.down, "Move Down", "Ctrl+↓", itemHeight);
            AXContextPane.setupContextMenuItem(expandAll, ResourceManager.expand, "Expand All", "", itemHeight);
            AXContextPane.setupContextMenuItem(clear, ResourceManager.clear, "Clear", "", itemHeight);
            AXContextPane.setupContextMenuItem(delete, ResourceManager.delete, "Delete", "Ctrl+D", itemHeight);

            langList.addAll(List.of(newFile, newFolder, copy, cut, paste, pasteAppend, rename, moveUp, moveDown, expandAll, clear, delete));

            newFile.addEventHandler(MouseEvent.MOUSE_CLICKED, _ -> newFile());
            newFolder.addEventHandler(MouseEvent.MOUSE_CLICKED, _ -> newFolder());
            copy.addEventHandler(MouseEvent.MOUSE_CLICKED, _ -> copy());
            cut.addEventHandler(MouseEvent.MOUSE_CLICKED, _ -> cut());
            paste.addEventHandler(MouseEvent.MOUSE_CLICKED, _ -> paste());
            pasteAppend.addEventHandler(MouseEvent.MOUSE_CLICKED, _ -> pasteAppend());
            rename.addEventHandler(MouseEvent.MOUSE_CLICKED, _ -> rename());
            moveUp.addEventHandler(MouseEvent.MOUSE_CLICKED, _ -> moveUp());
            moveDown.addEventHandler(MouseEvent.MOUSE_CLICKED, _ -> moveDown());
            expandAll.addEventHandler(MouseEvent.MOUSE_CLICKED, _ -> expandAll());
            clear.addEventHandler(MouseEvent.MOUSE_CLICKED, _ -> clear());
            delete.addEventHandler(MouseEvent.MOUSE_CLICKED, _ -> delete());
        }

        //FileContextMenu
        {
            AXButton copy = fileContextMenu.createItem();
            AXButton cut = fileContextMenu.createItem();
            AXButton pasteAppend = fileContextMenu.createItem();
            AXButton rename = fileContextMenu.createItem();
            AXButton moveUp = fileContextMenu.createItem();
            AXButton moveDown = fileContextMenu.createItem();
            AXButton delete = fileContextMenu.createItem();

            AXContextPane.setupContextMenuItem(copy, ResourceManager.copy, "Copy", "Ctrl+C", itemHeight);
            AXContextPane.setupContextMenuItem(cut, ResourceManager.cut, "Cut", "Ctrl+X", itemHeight);
            AXContextPane.setupContextMenuItem(pasteAppend, ResourceManager.pasteAppend, "Append Paste", "Ctrl+V", itemHeight);
            AXContextPane.setupContextMenuItem(rename, ResourceManager.rename, "Rename", "F2", itemHeight);
            AXContextPane.setupContextMenuItem(moveUp, ResourceManager.up, "Move Up", "Ctrl+↑", itemHeight);
            AXContextPane.setupContextMenuItem(moveDown, ResourceManager.down, "Move Down", "Ctrl+↓", itemHeight);
            AXContextPane.setupContextMenuItem(delete, ResourceManager.delete, "Delete", "Ctrl+D", itemHeight);

            langList.addAll(List.of(copy, cut, pasteAppend, rename, moveUp, moveDown, delete));

            copy.addEventHandler(MouseEvent.MOUSE_CLICKED, _ -> copy());
            cut.addEventHandler(MouseEvent.MOUSE_CLICKED, _ -> cut());
            pasteAppend.addEventHandler(MouseEvent.MOUSE_CLICKED, _ -> pasteAppend());
            rename.addEventHandler(MouseEvent.MOUSE_CLICKED, _ -> rename());
            moveUp.addEventHandler(MouseEvent.MOUSE_CLICKED, _ -> moveUp());
            moveDown.addEventHandler(MouseEvent.MOUSE_CLICKED, _ -> moveDown());
            delete.addEventHandler(MouseEvent.MOUSE_CLICKED, _ -> delete());
        }

        //ShortCut Key
        {
            super.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
                if (event.getCode() == KeyCode.F2) rename();

                boolean isFile = group.getData(group.getSelectedButton()) instanceof AXDataTreeItem<?>;
                boolean isRoot = group.getSelectedButton() == root.getButton();
                if (event.isControlDown()) {
                    if (isRoot) {
                        switch (event.getCode()) {
                            case N -> newFile();
                            case F -> newFolder();
                            case V -> paste();
                            case C, UP, DOWN, X, DELETE -> defaultToolkit.beep();
                        }
                    } else if (!isFile) {
                        if (event.isShiftDown() && event.getCode() == KeyCode.V) pasteAppend();
                        else switch (event.getCode()) {
                            case N -> newFile();
                            case F -> newFolder();
                            case C -> copy();
                            case X -> cut();
                            case V -> paste();
                            case UP -> moveUp();
                            case DOWN -> moveDown();
                            case D -> delete();
                        }
                    } else {
                        switch (event.getCode()) {
                            case C -> copy();
                            case X -> cut();
                            case UP -> moveUp();
                            case DOWN -> moveDown();
                            case D -> delete();
                            case V -> pasteAppend();
                            case N, F -> defaultToolkit.beep();
                        }
                    }
                }
            });
        }

        super.setPosition(scrollPane, false, style.getContentInsets(), style.getContentInsets(), style.getContentInsets(), style.getContentInsets());
        super.getChildren().add(scrollPane);

        setTheme(style);
    }

    public AXTreeItem getRoot() {
        return root;
    }

    public AXDataTreeItem<T> createFileItem(String name, Object data) {
        AXDataTreeItem<T> dataTreeItem = new AXDataTreeItem<>(name, (T) data, copyRule);
        dataTreeItem.setTheme(style.getFileItemStyle());
        Platform.runLater(() -> dataTreeItem.update(1));
        dataTreeItem.setAttribution(this);

        return dataTreeItem;
    }

    public AXTreeItem createFolderItem(String name) {
        AXTreeItem treeItem = new AXTreeItem(name);
        treeItem.setTheme(style.getFolderItemStyle());
        Platform.runLater(() -> treeItem.update(1));
        treeItem.setAttribution(this);

        return treeItem;
    }

    public void add(AXTreeItem parent, AXTreeItem... item) {
        parent.addToChildrenBox(item);
        for (AXTreeItem treeItem : item) {
            register(treeItem);
            treeItem.setAttribution(this);
        }
    }

    public void register(AXTreeItem treeItem) {
        group.register(treeItem.getButton(), treeItem);

        treeItem.getButton().addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.SECONDARY) {

                if (treeItem instanceof AXDataTreeItem<?>) {
                    fileContextMenu.show(this.getScene().getWindow(), event.getScreenX(), event.getScreenY());
                }

                else {
                    folderContextMenu.show(this.getScene().getWindow(), event.getScreenX(), event.getScreenY());
                }
            }
        });

        for (AXTreeItem child : treeItem.getChildrenAsItem()) {
            //System.out.println(child);
            register(child);
        }
    }

    public void setTheme(AXTreeViewStyle style) {
        this.style = style;
        super.setTheme(style);
        group.setFreeStyle(style.getRootItemStyle());
        group.setSelectedStyle(style.getSelectedButtonStyle());

        rootContextMenu.setTheme(style.getContextMenuStyle());
        folderContextMenu.setTheme(style.getContextMenuStyle());
        fileContextMenu.setTheme(style.getContextMenuStyle());

        textInputPopup.setTheme(style.getTextInputPopupStyle());
    }

    public AXDatableButtonGroup<AXTreeItem> getGroup() {
        return group;
    }

    @Override
    public void update() {
        super.update();
        root.update();
        group.update();

        rootContextMenu.update();
        folderContextMenu.update();
        fileContextMenu.update();

        textInputPopup.update();
    }

    @Override
    public String getI18NKey() {
        return "";
    }

    @Override
    public List<Localizable> getChildrenLocalizable() {
        return langList;
    }

    @Override
    public void setI18NKey(String key) {}

    @Override
    public void setChildrenI18NKeys(Map<String, String> keyMap) {
        langList.get(0).setI18NKey(keyMap.get("new file"));
        langList.get(1).setI18NKey(keyMap.get("new folder"));
        langList.get(2).setI18NKey(keyMap.get("paste"));
        langList.get(3).setI18NKey(keyMap.get("rename"));
        langList.get(4).setI18NKey(keyMap.get("expand all"));
        langList.get(5).setI18NKey(keyMap.get("clear"));
        langList.get(6).setI18NKey(keyMap.get("new file"));
        langList.get(7).setI18NKey(keyMap.get("new folder"));
        langList.get(8).setI18NKey(keyMap.get("copy"));
        langList.get(9).setI18NKey(keyMap.get("cut"));
        langList.get(10).setI18NKey(keyMap.get("paste"));
        langList.get(11).setI18NKey(keyMap.get("paste append"));
        langList.get(12).setI18NKey(keyMap.get("rename"));
        langList.get(13).setI18NKey(keyMap.get("move up"));
        langList.get(14).setI18NKey(keyMap.get("move down"));
        langList.get(15).setI18NKey(keyMap.get("expand all"));
        langList.get(16).setI18NKey(keyMap.get("clear"));
        langList.get(17).setI18NKey(keyMap.get("delete"));
        langList.get(18).setI18NKey(keyMap.get("copy"));
        langList.get(19).setI18NKey(keyMap.get("cut"));
        langList.get(20).setI18NKey(keyMap.get("paste append"));
        langList.get(21).setI18NKey(keyMap.get("rename"));
        langList.get(22).setI18NKey(keyMap.get("move up"));
        langList.get(23).setI18NKey(keyMap.get("move down"));
        langList.get(24).setI18NKey(keyMap.get("delete"));
    }

    @Override
    public void localize(String str) {}

    private static class TreeItemClipBoard {
        private AXTreeItem item;
        private final AXDataTreeItem.AXTreeviewCopyRule copyRule;

        private TreeItemClipBoard(AXDataTreeItem.AXTreeviewCopyRule copyRule) {
            this.copyRule = copyRule;
        }

        public void setCip(AXTreeItem item) {
            this.item = item;
        }

        public AXTreeItem getCip() {
            return clone(item);
        }

        private AXTreeItem clone(AXTreeItem item) {

            if (item == null) return null;

            AXTreeItem clonedItem;
            if (item instanceof AXDataTreeItem<?> dataItem) {
                clonedItem = new AXDataTreeItem<>(dataItem.getHeadName(), dataItem.getCopiedData(), copyRule);
            } else {
                clonedItem = new AXTreeItem(item.getHeadName());
            }
            clonedItem.setTheme(item.style());
            clonedItem.update(1);

            for (AXTreeItem child : item.getChildrenAsItem()) {
                clonedItem.add(clone(child));
            }

            return clonedItem;
        }
    }

    public interface AXTreeItemDataCreator<T> {
        T create();
    }


    public AXTreeItemDataCreator<T> getDataCreator() {
        return dataCreator;
    }


    private void newFile() {
        if (group.getSelectedButton() != null) {
            textInputPopup.clear();
            textInputPopup.showOnCenter(I18N.getCurrentValue("context.menu.new_file"), this.getScene().getWindow());
            textInputPopup.setOnTextAvailable((o, ov, nv) -> {
                this.add(group.getData(group.getSelectedButton()), createFileItem(nv, dataCreator.create()));
            });
        }
    }

    private void newFolder() {
        if (group.getSelectedButton() != null) {
            textInputPopup.clear();
            textInputPopup.showOnCenter(I18N.getCurrentValue("context.menu.new_folder"), this.getScene().getWindow());
            textInputPopup.setOnTextAvailable((o, ov, nv) -> {
                this.add(group.getData(group.getSelectedButton()), createFolderItem(nv));
            });
        }
    }

    private void copy() {
        if (group.getSelectedButton() != null) {
            clipBoard.setCip(group.getData(group.getSelectedButton()));
        }
    }

    private void cut() {
        if (group.getSelectedButton() != null) {
            AXTreeItem item = group.getData(group.getSelectedButton());
            clipBoard.setCip(item);

            AXTreeItem parentItem = item.getParentItem();
            if (parentItem != null) parentItem.remove(item);
        }
    }

    private void paste() {
        if (group.getSelectedButton() != null) {
            if (clipBoard.getCip() != null) {
                this.add(group.getData(group.getSelectedButton()), clipBoard.getCip());
            }
            else defaultToolkit.beep();
        }
    }

    private void pasteAppend() {
        if (group.getSelectedButton() != null) {
            AXTreeItem clip = clipBoard.getCip();
            if (clip != null) {
                AXTreeItem item = group.getData(group.getSelectedButton());
                AXTreeItem parentItem = item.getParentItem();
                this.add(parentItem, clip);
                parentItem.getChildrenItem().remove(clip);
                parentItem.getChildrenItem().add(parentItem.getChildrenItem().indexOf(item) + 1, clip);
            }
            else defaultToolkit.beep();
        }
    }

    private void rename() {
        if (group.getSelectedButton() != null) {
            AXTreeItem item = group.getData(group.getSelectedButton());
            textInputPopup.showOnCenter(I18N.getCurrentValue("context.menu.rename"), item.getHeadName(), this.getScene().getWindow());
            textInputPopup.setOnTextAvailable((o, ov, nv) -> {
                item.rename(nv);
            });
        }
    }

    private void moveUp() {
        if (group.getSelectedButton() != null) {
            AXTreeItem item = group.getData(group.getSelectedButton());
            AXTreeItem parent = item.getParentItem();

            if (parent != null) {
                int index = parent.getChildrenItem().indexOf(item);
                if (index == 0) java.awt.Toolkit.getDefaultToolkit().beep();
                else {
                    parent.getChildrenItem().remove(index);
                    parent.getChildrenItem().add(index - 1, item);
                }
            }
        }
    }

    private void moveDown() {
        if (group.getSelectedButton() != null) {
            AXTreeItem item = group.getData(group.getSelectedButton());
            AXTreeItem parent = item.getParentItem();

            if (parent != null) {
                int index = parent.getChildrenItem().indexOf(item);
                if (index == parent.getChildrenItem().size() - 1) java.awt.Toolkit.getDefaultToolkit().beep();
                else {
                    parent.getChildrenItem().remove(index);
                    parent.getChildrenItem().add(index + 1, item);
                }
            }
        }
    }

    private void expandAll() {
        if (group.getSelectedButton() != null) {
            group.getData(group.getSelectedButton()).expandAll();
        }
    }

    private void clear() {
        if (group.getSelectedButton() != null) {
            AXTreeItem clearRoot = group.getData(group.getSelectedButton());
            removeInGroup(clearRoot);
            clearRoot.clear();
            group.register(clearRoot.getButton(), clearRoot);
        }
    }

    private void delete() {
        if (group.getSelectedButton() != null) {
            AXTreeItem item = group.getData(group.getSelectedButton());
            AXTreeItem parentItem = item.getParentItem();
            if (parentItem != null) parentItem.remove(item);
            removeInGroup(item);
        }
    }

    private void removeInGroup(AXTreeItem treeItem) {
        if (!(treeItem instanceof AXDataTreeItem<?>)) {
            for (AXTreeItem child : treeItem.getChildrenAsItem()) {
                removeInGroup(child);
            }
        }
        group.remove(treeItem.getButton());
        //System.out.println(group.getButtonList().size());
    }
}
