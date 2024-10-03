package mai_onsyn.AnimeFX2.Module;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.util.Duration;
import mai_onsyn.AnimeFX2.LanguageSwitchable;
import mai_onsyn.AnimeFX2.Styles.AXTextFieldStyle;
import mai_onsyn.AnimeFX2.Styles.DefaultAXTextFieldStyle;
import mai_onsyn.AnimeFX2.layout.AXContextPane;
import mai_onsyn.AnimeFX2.Utls.Toolkit;

import java.io.IOException;
import java.util.Map;

public class AXTextField extends AXBase implements LanguageSwitchable {

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

    private AXTextFieldStyle style = new DefaultAXTextFieldStyle();

    private final TextField textField = new TextField();
    private final Line line = new Line();
    private final SimpleDoubleProperty lineInsetProperty = new SimpleDoubleProperty(0);
    private final AXContextPane contextMenu = new AXContextPane();
    private Timeline lineAnimation = new Timeline();

    private final AXButton copy = contextMenu.createItem();
    private final AXButton paste = contextMenu.createItem();
    private final AXButton cut = contextMenu.createItem();
    private final AXButton undo = contextMenu.createItem();
    private final AXButton selectAll = contextMenu.createItem();
    private final AXButton clear = contextMenu.createItem();

    public AXTextField() {
        super();
        super.setTheme(style);
        textField.getStylesheets().add("style.css");

        double itemHeight = contextMenu.getStyle().getItemHeight();
        setupContextMenuItem(copy, copyIcon, "Copy", "Ctrl+C", itemHeight);
        setupContextMenuItem(paste, pasteIcon, "Paste", "Ctrl+V", itemHeight);
        setupContextMenuItem(cut, cutIcon, "Cut", "Ctrl+X", itemHeight);
        setupContextMenuItem(undo, undoIcon, "Undo", "Ctrl+Z", itemHeight);
        setupContextMenuItem(selectAll, selectAllIcon, "Select All", "Ctrl+A", itemHeight);
        setupContextMenuItem(clear, clearIcon, "Clear", "", itemHeight);

        copy.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.PRIMARY) textField.copy();
        });
        paste.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.PRIMARY) textField.paste();
        });
        cut.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.PRIMARY) textField.cut();
        });
        undo.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.PRIMARY) textField.undo();
        });
        selectAll.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.PRIMARY) textField.selectAll();
        });
        clear.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.PRIMARY) textField.clear();
        });

        contextMenu.getScrollPane().setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        super.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            textField.requestFocus();

            if (event.getButton() == MouseButton.SECONDARY) {
                contextMenu.show(this.getScene().getWindow(), event.getScreenX(), event.getScreenY());
            }
        });
        textField.addEventFilter(MouseEvent.MOUSE_PRESSED, super::fireEvent);
        textField.addEventFilter(MouseEvent.MOUSE_RELEASED, super::fireEvent);
        textField.focusedProperty().addListener((o, ov, nv) -> {
            if (nv) {
                line.setVisible(true);

                lineAnimation.stop();
                lineAnimation = new Timeline(new KeyFrame(Duration.millis(200 * style.getAnimeRate()),
                        new KeyValue(line.startXProperty(), style.getAreaInsets()),
                        new KeyValue(line.endXProperty(), super.getLayoutBounds().getWidth() - style.getAreaInsets())
                ));
                lineAnimation.play();
            }

            else {
                lineAnimation.stop();
                lineAnimation = new Timeline(new KeyFrame(Duration.millis(200 * style.getAnimeRate()),
                        new KeyValue(line.startXProperty(), super.getLayoutBounds().getWidth() / 2),
                        new KeyValue(line.endXProperty(), super.getLayoutBounds().getWidth() / 2)
                ));
                lineAnimation.setOnFinished(_ -> line.setVisible(true));
                lineAnimation.play();

                super.fireEvent(new MouseEvent(MouseEvent.MOUSE_EXITED, 0, 0, 0, 0, MouseButton.PRIMARY, 1, false, false, false, false, false, false, false, false, false, false, null));
            }
        });
        super.addEventFilter(MouseEvent.MOUSE_EXITED, event -> {
            if (textField.isFocused()) event.consume();
        });

        textField.setContextMenu(new ContextMenu());

        line.setStroke(style.getLineColor());
        Platform.runLater(() -> {
            line.setStartX(super.getLayoutBounds().getWidth() / 2);
            line.setEndX(super.getLayoutBounds().getWidth() / 2);
        });
        line.startYProperty().bind(Bindings.subtract(super.heightProperty(), lineInsetProperty));
        line.endYProperty().bind(Bindings.subtract(super.heightProperty(), lineInsetProperty));

        super.getChildren().addAll(textField, line);
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
        textField.setStyle(String.format(
                """
                -fx-text-fill: #%s;
                -fx-highlight-fill: #%s;
                -fx-highlight-text-fill: #%s;
                """,
                Toolkit.colorToString(style.getTextColor()),
                Toolkit.colorToString(style.getTextSelectedBGColor()),
                Toolkit.colorToString(style.getTextSelectedColor())
        ));
        textField.setFont(style.getTextFont());

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(200 * style.getAnimeRate()),
                new KeyValue(line.strokeProperty(), style.getLineColor()),
                new KeyValue(lineInsetProperty, style.getLineInsets()),
                new KeyValue(line.strokeWidthProperty(), style.getLineWeight())
        ));
        timeline.play();

        super.setPosition(textField, false, style.getAreaInsets(), style.getAreaInsets(), style.getAreaInsets(), style.getAreaInsets());
        super.update();
    }

    public void setTheme(AXTextFieldStyle style) {
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
        textField.appendText(s);
    }
    public void clear() {
        textField.clear();
    }
    public void setText(String s) {
        textField.setText(s);
    }
    public void setPromptText(String s) {
        textField.setPromptText(s);
    }
    public void setEditable(boolean b) {
        if (b && !textField.isEditable()) {
            contextMenu.showItem(paste, 1);
            contextMenu.showItem(cut, 2);
            contextMenu.showItem(undo, 3);
        }
        else if (!b && textField.isEditable()) {
            contextMenu.removeItem(paste);
            contextMenu.removeItem(cut);
            contextMenu.removeItem(undo);
        }
        textField.setEditable(b);
    }
    public String getText() {
        return textField.getText();
    }
    public String getText(int start, int end) {
        return textField.getText(start, end);
    }
    public String getPromptText() {
        return textField.getPromptText();
    }
    public boolean isEditable() {
        return textField.isEditable();
    }
    public StringProperty textProperty() {
        return textField.textProperty();
    }

    public TextField textField() {
        return textField;
    }
}
