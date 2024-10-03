package mai_onsyn.AnimeFX2.Module;

import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import mai_onsyn.AnimeFX2.LanguageSwitchable;
import mai_onsyn.AnimeFX2.Styles.AXTextAreaStyle;
import mai_onsyn.AnimeFX2.Styles.DefaultAXTextAreaStyle;
import mai_onsyn.AnimeFX2.layout.AXContextPane;
import mai_onsyn.AnimeFX2.Utls.Toolkit;

import java.io.IOException;
import java.util.Map;

public class AXTextArea extends AXBase implements LanguageSwitchable {

    private static final WritableImage copyIcon;
    private static final WritableImage pasteIcon;
    private static final WritableImage cutIcon;
    private static final WritableImage selectAllIcon;
    private static final WritableImage clearIcon;
    private static final WritableImage undoIcon;

    static {
        try {
            copyIcon = new WritableImage(Toolkit.loadImage("textures/icons/copy.png").getPixelReader(), 512, 512);
            pasteIcon = new WritableImage(Toolkit.loadImage("textures/icons/paste.png").getPixelReader(), 512, 512);
            cutIcon = new WritableImage(Toolkit.loadImage("textures/icons/cut.png").getPixelReader(), 512, 512);
            selectAllIcon = new WritableImage(Toolkit.loadImage("textures/icons/select_all.png").getPixelReader(), 512, 512);
            clearIcon = new WritableImage(Toolkit.loadImage("textures/icons/clear.png").getPixelReader(), 512, 512);
            undoIcon = new WritableImage(Toolkit.loadImage("textures/icons/undo.png").getPixelReader(), 512, 512);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private AXTextAreaStyle style = new DefaultAXTextAreaStyle();

    private final TextArea textArea = new TextArea();
    private final AXContextPane contextMenu = new AXContextPane();
    private final AXButton copy = contextMenu.createItem();
    private final AXButton paste = contextMenu.createItem();
    private final AXButton cut = contextMenu.createItem();
    private final AXButton undo = contextMenu.createItem();
    private final AXButton selectAll = contextMenu.createItem();
    private final AXButton clear = contextMenu.createItem();

    public AXTextArea() {
        super();
        super.setTheme(style);
        textArea.setWrapText(true);
        textArea.getStylesheets().add("style.css");

        double itemHeight = contextMenu.getStyle().getItemHeight();
        setupContextMenuItem(copy, copyIcon, "Copy", "Ctrl+C", itemHeight);
        setupContextMenuItem(paste, pasteIcon, "Paste", "Ctrl+V", itemHeight);
        setupContextMenuItem(cut, cutIcon, "Cut", "Ctrl+X", itemHeight);
        setupContextMenuItem(undo, undoIcon, "Undo", "Ctrl+Z", itemHeight);
        setupContextMenuItem(selectAll, selectAllIcon, "Select All", "Ctrl+A", itemHeight);
        setupContextMenuItem(clear, clearIcon, "Clear", "", itemHeight);

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
        textArea.focusedProperty().addListener((o, ov, nv) -> {super.fireEvent(new MouseEvent(nv ? MouseEvent.MOUSE_ENTERED : MouseEvent.MOUSE_EXITED, 0, 0, 0, 0, MouseButton.PRIMARY, 1, false, false, false, false, false, false, false, false, false, false, null));});
        super.addEventFilter(MouseEvent.MOUSE_EXITED, event -> {
            if (textArea.isFocused()) event.consume();
        });

        textArea.setContextMenu(new ContextMenu());


        Platform.runLater(() -> {
            ScrollPane scrollPane = (ScrollPane) textArea.lookup(".scroll-pane");
            if (scrollPane == null) return;

            Toolkit.addSmoothScrolling(scrollPane);
        });

        super.getChildren().add(textArea);
        this.update();
    }

    private void setupContextMenuItem(AXButton button, WritableImage icon, String text, String shortcut, double itemHeight) {
        ImageView imageView = new ImageView(icon);
        Label name = (Label) button.getChildren().getLast();
        name.setText(text);
        name.setFont(new Font(name.getFont().getName(), name.getFont().getSize()));  // 默认字体

        // 设置快捷键提示
        Label prompt = new Label(shortcut);
        prompt.setFont(new Font(name.getFont().getName(), name.getFont().getSize() * 0.8));
        prompt.setTextFill(Color.GRAY);

        // 将图标、名称、快捷键提示添加到按钮
        button.getChildren().addAll(imageView, prompt);

        // 设置各个组件的位置和布局
        button.setPosition(imageView, false, itemHeight * 0.1, itemHeight * 0.9, itemHeight * 0.1, itemHeight * 0.1);
        button.flipRelativeMode(imageView, Motion.RIGHT);

        Toolkit.adjustImageColor(icon, button.style().getTextColor());  // 调整图标颜色

        button.setPosition(name, AlignmentMode.LEFT_CENTER, LocateMode.ABSOLUTE, itemHeight * 1.2, itemHeight / 2);
        button.setPosition(prompt, AlignmentMode.RIGHT_CENTER, LocateMode.ABSOLUTE, -itemHeight * 0.1, itemHeight / 2);
    }


    @Override
    public void update() {
        textArea.setStyle(String.format(
            """
            -fx-text-fill: #%s;
            -fx-highlight-fill: #%s;
            -fx-highlight-text-fill: #%s;
            """,
            Toolkit.colorToString(style.getTextColor()),
            Toolkit.colorToString(style.getTextSelectedBGColor()),
            Toolkit.colorToString(style.getTextSelectedColor())
        ));
        textArea.setFont(style.getTextFont());
        super.setPosition(textArea, false, style.getAreaInsets(), style.getAreaInsets(), style.getAreaInsets(), style.getAreaInsets());
        super.update();
    }

    public void setTheme(AXTextAreaStyle style) {
        this.style = style;
        super.setTheme(style);
    }

    @Override
    public void switchLanguage(String str) {}

    @Override
    public Map<String, LanguageSwitchable> getLanguageElements() {
        return Map.of(
                "copy", copy,
                "cut", cut,
                "paste", paste,
                "undo", undo,
                "select_all", selectAll,
                "clear", clear
        );
    }


    public void appendText(String s) {
        textArea.appendText(s);
    }
    public void clear() {
        textArea.clear();
    }
    public void setText(String s) {
        textArea.setText(s);
    }
    public void setPromptText(String s) {
        textArea.setPromptText(s);
    }
    public void setWrapText(boolean b) {
        textArea.setWrapText(b);
    }
    public void setEditable(boolean b) {
        if (b && !textArea.isEditable()) {
            contextMenu.showItem(paste, 1);
            contextMenu.showItem(cut, 2);
            contextMenu.showItem(undo, 3);
        }
        else if (!b && textArea.isEditable()) {
            contextMenu.removeItem(paste);
            contextMenu.removeItem(cut);
            contextMenu.removeItem(undo);
        }
        textArea.setEditable(b);
    }
    public String getText() {
        return textArea.getText();
    }
    public String getText(int start, int end) {
        return textArea.getText(start, end);
    }
    public String getPromptText() {
        return textArea.getPromptText();
    }
    public boolean isWrapText() {
        return textArea.isWrapText();
    }
    public boolean isEditable() {
        return textArea.isEditable();
    }
    public StringProperty textProperty() {
        return textArea.textProperty();
    }

    public TextArea textArea() {
        return textArea;
    }
}
