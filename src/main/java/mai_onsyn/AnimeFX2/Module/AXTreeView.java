package mai_onsyn.AnimeFX2.Module;

import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import mai_onsyn.AnimeFX2.LanguageSwitchable;
import mai_onsyn.AnimeFX2.ResourceManager;
import mai_onsyn.AnimeFX2.Styles.AXTreeViewStyle;
import mai_onsyn.AnimeFX2.Styles.DefaultAXTreeViewStyle;
import mai_onsyn.AnimeFX2.Utls.*;
import mai_onsyn.AnimeFX2.layout.AXContextPane;
import mai_onsyn.AnimeFX2.layout.AXTextInputPopup;

import java.util.HashMap;
import java.util.Map;

import static mai_onsyn.AnimeFX2.Utls.Toolkit.defaultToolkit;

public class AXTreeView<T> extends AXBase implements LanguageSwitchable {
    private AXTreeViewStyle style = new DefaultAXTreeViewStyle();
    private final AXTreeItem root = new AXTreeItem("ROOT");
    private final AXDatableButtonGroup<AXTreeItem> group = new AXDatableButtonGroup<>(root.getButton(), root);
    private final TreeItemClipBoard clipBoard = new TreeItemClipBoard();
    private final AXTextInputPopup textInputPopup = new AXTextInputPopup();

    private final AXContextPane rootContextMenu = new AXContextPane();
    private final AXContextPane folderContextMenu = new AXContextPane();
    private final AXContextPane fileContextMenu = new AXContextPane();

    private final Map<String, LanguageSwitchable> langMap = new HashMap<>();
    private final AXTreeItemDataCreator<T> dataCreator;


    public AXTreeView(AXTreeItemDataCreator<T> dataCreator) {
        this.dataCreator = dataCreator;
        ScrollPane scrollPane = new ScrollPane(root);
        Toolkit.addSmoothScrolling(scrollPane);
        scrollPane.getStylesheets().add("style.css");
        scrollPane.addEventFilter(MouseEvent.MOUSE_PRESSED, super::fireEvent);
        scrollPane.addEventFilter(MouseEvent.MOUSE_RELEASED, super::fireEvent);
        group.setFreeStyle(style.getRootItemStyle());

        root.setTheme(style.getRootItemStyle());
        root.update();

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

            langMap.put("root.new_file", newFile);
            langMap.put("root.new_folder", newFolder);
            langMap.put("root.paste", paste);
            langMap.put("root.rename", rename);
            langMap.put("root.expand_all", expandAll);
            langMap.put("root.clear", clear);

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

            langMap.put("folder.new_file", newFile);
            langMap.put("folder.new_folder", newFolder);
            langMap.put("folder.copy", copy);
            langMap.put("folder.cut", cut);
            langMap.put("folder.paste", paste);
            langMap.put("folder.paste_append", pasteAppend);
            langMap.put("folder.rename", rename);
            langMap.put("folder.move_up", moveUp);
            langMap.put("folder.move_down", moveDown);
            langMap.put("folder.expand_all", expandAll);
            langMap.put("folder.clear", clear);
            langMap.put("folder.delete", delete);

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

            langMap.put("file.copy", copy);
            langMap.put("file.cut", cut);
            langMap.put("file.paste_append", pasteAppend);
            langMap.put("file.rename", rename);
            langMap.put("file.move_up", moveUp);
            langMap.put("file.move_down", moveDown);
            langMap.put("file.delete", delete);

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
                        if (event.isShiftDown() && event.getCode() == KeyCode.V) pasteAppend();
                        else switch (event.getCode()) {
                            case C -> copy();
                            case X -> cut();
                            case UP -> moveUp();
                            case DOWN -> moveDown();
                            case D -> delete();
                            case N, F, V -> defaultToolkit.beep();
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

    public AXDataTreeItem<T> createFileItem(String name, T data) {
        AXDataTreeItem<T> dataTreeItem = new AXDataTreeItem<>(name, data);
        dataTreeItem.setTheme(style.getFileItemStyle());
        dataTreeItem.update();

        return dataTreeItem;
    }

    public AXTreeItem createFolderItem(String name) {
        AXTreeItem treeItem = new AXTreeItem(name);
        treeItem.setTheme(style.getFolderItemStyle());
        treeItem.update();

        return treeItem;
    }

    public void add(AXTreeItem parent, AXTreeItem... item) {
        parent.add(item);
        for (AXTreeItem treeItem : item) {
            register(treeItem);
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
            System.out.println(child);
            register(child);
        }
    }

    public void setTheme(AXTreeViewStyle style) {
        this.style = style;
        super.setTheme(style);
    }

    public AXDatableButtonGroup<AXTreeItem> getGroup() {
        return group;
    }

    @Override
    public void update() {
        super.update();
        root.update();
    }

    @Override
    public void switchLanguage(String str) {}

    @Override
    public Map<String, LanguageSwitchable> getLanguageElements() {
        return langMap;
    }

    private static class TreeItemClipBoard {
        private AXTreeItem item;

        public void setCip(AXTreeItem item) {
            this.item = item;
        }

        public AXTreeItem getCip() {
            return AXTreeItem.clone(item);
        }
    }

    public interface AXTreeItemDataCreator<T> {
        T create();
    }


    private void newFile() {
        if (group.getSelectedButton() != null) {
            textInputPopup.clear();
            textInputPopup.showOnCenter("New File", this.getScene().getWindow());
            textInputPopup.setOnTextAvailable((o, ov, nv) -> {
                this.add(group.getData(group.getSelectedButton()), createFileItem(nv, dataCreator.create()));
            });
        }
    }

    private void newFolder() {
        if (group.getSelectedButton() != null) {
            textInputPopup.clear();
            textInputPopup.showOnCenter("New Folder", this.getScene().getWindow());
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
            textInputPopup.showOnCenter("Rename", item.getButton().getTextLabel().getText(), this.getScene().getWindow());
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
            group.getData(group.getSelectedButton()).clear();
        }
    }

    private void delete() {
        if (group.getSelectedButton() != null) {
            AXTreeItem item = group.getData(group.getSelectedButton());
            AXTreeItem parentItem = item.getParentItem();
            if (parentItem != null) parentItem.remove(item);
        }
    }
}
