package mai_onsyn.AnimeFX2.Module;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Line;
import javafx.util.Duration;
import mai_onsyn.AnimeFX2.Localizable;
import mai_onsyn.AnimeFX2.ResourceManager;
import mai_onsyn.AnimeFX2.Styles.AXTextFieldStyle;
import mai_onsyn.AnimeFX2.Styles.DefaultAXTextFieldStyle;
import mai_onsyn.AnimeFX2.layout.AXContextPane;
import mai_onsyn.AnimeFX2.Utls.Toolkit;

import java.util.List;
import java.util.Map;

public class AXTextField extends AXBase implements Localizable {

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
        contextMenu.setTheme(style.getContextPaneStyle());
        textField.getStylesheets().add("style.css");

        double itemHeight = style.getContextPaneStyle().getItemHeight();
        AXContextPane.setupContextMenuItem(copy, ResourceManager.copy, "Copy", "Ctrl+C", itemHeight);
        AXContextPane.setupContextMenuItem(paste, ResourceManager.paste, "Paste", "Ctrl+V", itemHeight);
        AXContextPane.setupContextMenuItem(cut, ResourceManager.cut, "Cut", "Ctrl+X", itemHeight);
        AXContextPane.setupContextMenuItem(undo, ResourceManager.undo, "Undo", "Ctrl+Z", itemHeight);
        AXContextPane.setupContextMenuItem(selectAll, ResourceManager.selectAll, "Select All", "Ctrl+A", itemHeight);
        AXContextPane.setupContextMenuItem(clear, ResourceManager.clear, "Clear", "", itemHeight);

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


    @Override
    public void update() {
        textField.setStyle(String.format(
                """
                -fx-text-fill: %s;
                -fx-highlight-fill: %s;
                -fx-highlight-text-fill: %s;
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

    private String promptI18NKey = "";

    @Override
    public String getI18NKey() {
        return promptI18NKey;
    }

    @Override
    public void setI18NKey(String key) {
        promptI18NKey = key;
    }

    @Override
    public void localize(String str) {
        textField.setPromptText(str);
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
