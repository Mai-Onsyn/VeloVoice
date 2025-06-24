package mai_onsyn.AnimeFX2.Module;

import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Region;
import javafx.util.Duration;
import mai_onsyn.AnimeFX2.Localizable;
import mai_onsyn.AnimeFX2.ResourceManager;
import mai_onsyn.AnimeFX2.Styles.AXInlineTextAreaStyle;
import mai_onsyn.AnimeFX2.Styles.DefaultAXInlineTextAreaStyle;
import mai_onsyn.AnimeFX2.Utls.Toolkit;
import mai_onsyn.AnimeFX2.layout.AXContextPane;
import org.fxmisc.flowless.Virtualized;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.InlineCssTextArea;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

public class AXInlineTextArea extends AXBase implements Localizable {
    private AXInlineTextAreaStyle style = new DefaultAXInlineTextAreaStyle();

    private final InlineCssTextArea textArea = new InlineCssTextArea();
    private final VirtualizedScrollPane<InlineCssTextArea> virtualizedScrollPane = new VirtualizedScrollPane<>(textArea);

    private final AXContextPane contextMenu = new AXContextPane();
    private final AXButton copy = contextMenu.createItem();
    private final AXButton paste = contextMenu.createItem();
    private final AXButton cut = contextMenu.createItem();
    private final AXButton undo = contextMenu.createItem();
    private final AXButton selectAll = contextMenu.createItem();
    private final AXButton clear = contextMenu.createItem();
    private final ChangeListener<Boolean> focusListener = (o, ov, nv) -> {
        super.fireEvent(new MouseEvent(nv ? MouseEvent.MOUSE_ENTERED : MouseEvent.MOUSE_EXITED, 0, 0, 0, 0, MouseButton.PRIMARY, 1, false, false, false, false, false, false, false, false, false, false, null));
    };
    private final EventHandler<MouseEvent> exitHandlerOverwrite = event -> {
        if (textArea.isFocused()) event.consume();
    };

    public AXInlineTextArea() {
        super();
        super.setTheme(style);
        contextMenu.setTheme(style.getContextMenuStyle());
        textArea.setWrapText(true);
        textArea.getStylesheets().add("style.css");

        double itemHeight = style.getContextMenuStyle().getItemHeight();
        AXContextPane.setupContextMenuItem(copy, ResourceManager.copy, "Copy", "Ctrl+C", itemHeight);
        AXContextPane.setupContextMenuItem(paste, ResourceManager.paste, "Paste", "Ctrl+V", itemHeight);
        AXContextPane.setupContextMenuItem(cut, ResourceManager.cut, "Cut", "Ctrl+X", itemHeight);
        AXContextPane.setupContextMenuItem(undo, ResourceManager.undo, "Undo", "Ctrl+Z", itemHeight);
        AXContextPane.setupContextMenuItem(selectAll, ResourceManager.selectAll, "Select All", "Ctrl+A", itemHeight);
        AXContextPane.setupContextMenuItem(clear, ResourceManager.clear, "Clear", "", itemHeight);

        copy.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.PRIMARY) textArea.copy();
        });
        paste.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.PRIMARY) textArea.paste();
        });
        cut.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.PRIMARY) textArea.cut();
        });
        undo.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.PRIMARY) textArea.undo();
        });
        selectAll.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.PRIMARY) textArea.selectAll();
        });
        clear.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.PRIMARY) textArea.clear();
        });

        contextMenu.getScrollPane().setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        super.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            textArea.requestFocus();

            if (event.getButton() == MouseButton.SECONDARY) {
                contextMenu.show(this.getScene().getWindow(), event.getScreenX(), event.getScreenY());
            }
        });
        textArea.addEventFilter(MouseEvent.MOUSE_PRESSED, super::fireEvent);
        textArea.addEventFilter(MouseEvent.MOUSE_RELEASED, super::fireEvent);

        textArea.focusedProperty().addListener(focusListener);
        super.addEventFilter(MouseEvent.MOUSE_EXITED, exitHandlerOverwrite);

        textArea.setContextMenu(new ContextMenu());


//        Platform.runLater(() -> {
//            ScrollPane scrollPane = (ScrollPane) textArea.lookup(".scroll-pane");
//            System.out.println(scrollPane == null);
//            if (scrollPane == null) return;
//
//            Toolkit.addSmoothScrolling(scrollPane);
//        });

        virtualizedScrollPane.getStylesheets().add("style.css");

        super.getChildren().add(virtualizedScrollPane);
        this.update();
    }

//    private void addSmoothScrolling(VirtualizedScrollPane<?> scrollPane) {
//        ScrollBar scrollBar = getVerticalScrollBar(scrollPane);
//
//        this.addEventFilter(ScrollEvent.SCROLL, event -> {
//            double scrollValue = event.getDeltaY();
//
//            scrollPane.scrollYBy(scrollValue * 2);
//            scrollPane.scrollYBy(scrollValue * 2);
//            scrollPane.scrollYBy(scrollValue * 2);
//            scrollPane.scrollYBy(scrollValue * 2);
//            //scrollBar.setValue(scrollBar.getValue() + 100);
//        });
//    }
//
//    private ScrollBar getVerticalScrollBar(VirtualizedScrollPane<?> scrollPane) {
//        try {
//            // 获取私有字段
//            Field vbarField = VirtualizedScrollPane.class.getDeclaredField("vbar");
//            vbarField.setAccessible(true); // 设置可访问
//
//            // 获取实例中的值
//            return (ScrollBar) vbarField.get(scrollPane);
//        } catch (NoSuchFieldException | IllegalAccessException e) {
//            throw new RuntimeException("Failed to access vertical scrollbar", e);
//        }
//    }


    @Override
    public void update() {
        textArea.setStyle(String.format(
                """
                -fx-highlight-fill: %s;
                """,
                Toolkit.colorToString(style.getTextSelectedBGColor())
        ));
        super.setPosition(virtualizedScrollPane, false, style.getAreaInsets(), style.getAreaInsets(), style.getAreaInsets(), style.getAreaInsets());
        super.update();
    }

    public void setTheme(AXInlineTextAreaStyle style) {
        this.style = style;
        super.setTheme(style);
    }

    @Override
    public String getI18NKey() {
        return "";
    }

    @Override
    public void setI18NKey(String key) {
    }

    @Override
    public void localize(String str) {
    }

    @Override
    public List<Localizable> getChildrenLocalizable() {
        return List.of(copy, cut, paste, undo, selectAll, clear);
    }

    @Override
    public void setChildrenI18NKeys(Map<String, String> keyMap) {
        copy.setI18NKey(keyMap.get("copy"));
        cut.setI18NKey(keyMap.get("cut"));
        paste.setI18NKey(keyMap.get("paste"));
        undo.setI18NKey(keyMap.get("undo"));
        selectAll.setI18NKey(keyMap.get("select all"));
        clear.setI18NKey(keyMap.get("clear"));
    }


    public void clear() {
        textArea.clear();
    }
    public void replaceText(String s) {
        textArea.replaceText(s);
    }
    public void appendText(String s) {
        textArea.appendText(s);
    }
    public void deleteText(int start, int end) {
        textArea.deleteText(start, end);
    }
    public void insertText(int index, String s) {
        textArea.insertText(index, s);
    }
    public void setWrapText(boolean b) {
        textArea.setWrapText(b);
    }
    public void setEditable(boolean b) {
        if (b && !textArea.isEditable()) {
            contextMenu.showItem(paste, 1);
            contextMenu.showItem(cut, 2);
            contextMenu.showItem(undo, 3);
            textArea.focusedProperty().addListener(focusListener);
            super.addEventFilter(MouseEvent.MOUSE_EXITED, exitHandlerOverwrite);
        }
        else if (!b && textArea.isEditable()) {
            contextMenu.removeItem(paste);
            contextMenu.removeItem(cut);
            contextMenu.removeItem(undo);
            textArea.focusedProperty().removeListener(focusListener);
            super.removeEventFilter(MouseEvent.MOUSE_EXITED, exitHandlerOverwrite);
        }
        textArea.setEditable(b);
    }
    public String getText() {
        return textArea.getText();
    }
    public String getText(int start, int end) {
        return textArea.getText(start, end);
    }
    public boolean isWrapText() {
        return textArea.isWrapText();
    }
    public boolean isEditable() {
        return textArea.isEditable();
    }

    public InlineCssTextArea textArea() {
        return textArea;
    }
}
