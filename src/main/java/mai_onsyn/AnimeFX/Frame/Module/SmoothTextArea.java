package mai_onsyn.AnimeFX.Frame.Module;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import mai_onsyn.AnimeFX.Frame.Layout.AutoPane;
import mai_onsyn.AnimeFX.Frame.Layout.PopupMenu;
import mai_onsyn.AnimeFX.Frame.Styles.ButtonStyle;
import mai_onsyn.AnimeFX.Frame.Styles.DefaultButtonStyle;
import mai_onsyn.AnimeFX.Frame.Utils.Toolkit;

public class SmoothTextArea extends AutoPane {

    private Font font = new Font(14);
    private double width = 100;
    private double height = 100;
    private final Rectangle border = new Rectangle();
    private final Rectangle borderClip = new Rectangle();
    //private int animeDuration = 200;
    private Color bgColor = new Color(1, 1, 1, 0.5);
    private Color textColor = new Color(0, 0, 0, 0.8);
    private Color textHighlightColor = new Color(0.9, 0.9, 0.9, 0.8);
    private Color textHighlightBGColor = new Color(0, 0.3, 0.66, 0.5);
    private ButtonStyle buttonStyle = new DefaultButtonStyle();

    public SmoothTextArea borderRadius(double r) {
        this.border.setStrokeWidth(r);
        this.borderClip.setStrokeWidth(r);
        return this;
    }
    public SmoothTextArea borderColor(Color color) {
        this.border.setStroke(color);
        this.borderClip.setStroke(color);
        return this;
    }
    public SmoothTextArea borderShape(double v1) {
        border.setArcWidth(v1);
        border.setArcHeight(v1);
        borderClip.setArcWidth(v1);
        borderClip.setArcHeight(v1);
        return this;
    }
    public SmoothTextArea font(Font font) {
        this.font = font;
        return this;
    }
    public SmoothTextArea width(double width) {
        this.width = width;
        return this;
    }
    public SmoothTextArea height(double height) {
        this.height = height;
        return this;
    }
    public SmoothTextArea bgColor(Color bgColor) {
        this.bgColor = bgColor;
        return this;
    }
    public SmoothTextArea animeDuration(int animeDuration) {
        //this.animeDuration = animeDuration;
        return this;
    }
    public SmoothTextArea textHighlightBGColor(Color textHighlightBGColor) {
        this.textHighlightBGColor = textHighlightBGColor;
        return this;
    }
    public SmoothTextArea textHighlightColor(Color textHighlightColor) {
        this.textHighlightColor = textHighlightColor;
        return this;
    }
    public SmoothTextArea textColor(Color textColor) {
        this.textColor = textColor;
        return this;
    }
    public SmoothTextArea buttonStyle(ButtonStyle style) {
        this.buttonStyle = style;
        return this;
    }

    private final ToolArea textArea = new ToolArea();

    public SmoothTextArea init() {
        textArea.getStylesheets().add("styles/text-area.css");
        textArea.setFont(font);
        textArea.setWrapText(true);
        textArea.setStyle(String.format(
                """
                -fx-text-fill: #%s;
                -fx-highlight-fill: #%s;
                -fx-highlight-text-fill: #%s;
                """,
                Toolkit.colorToString(textColor),
                Toolkit.colorToString(textHighlightBGColor),
                Toolkit.colorToString(textHighlightColor)
                ));
        {
            DiffusionButton copyButton = buttonStyle.createButton("复制");
            HBox copyMenuItem = new HBox(copyButton);
            copyMenuItem.setOnMouseClicked(_ -> textArea.copy());


            DiffusionButton pasteButton = buttonStyle.createButton("粘贴");
            HBox pasteMenuItem = new HBox(pasteButton);
            pasteMenuItem.setOnMouseClicked(_ -> textArea.paste());

            DiffusionButton cutButton = buttonStyle.createButton("剪切");
            HBox cutMenuItem = new HBox(cutButton);
            cutMenuItem.setOnMouseClicked(_ -> textArea.cut());

            DiffusionButton clearButton = buttonStyle.createButton("清空");
            HBox clearMenuItem = new HBox(clearButton);
            clearButton.setOnMouseClicked(_ -> textArea.clear());

            if (textArea.isEditable()) {
                new PopupMenu(50, copyMenuItem, pasteMenuItem, cutMenuItem, clearMenuItem)
                        .borderShape(20)
                        .bind(textArea);
            }
            else {
                new PopupMenu(50, copyMenuItem, clearMenuItem)
                        .borderShape(20)
                        .bind(textArea);
            }
            textArea.setContextMenu(new ContextMenu());
        }
        //else textArea.setContextMenu(null);


        border.setFill(bgColor);
        super.setClip(borderClip);
        super.setPosition(textArea, false, border.getArcWidth() / 3, border.getArcWidth() / 3, border.getArcHeight() / 3, border.getArcHeight() / 3);

        super.widthProperty().addListener((o, ov, nv) -> {
            this.width = nv.doubleValue();

            border.setWidth(width);
            borderClip.setWidth(width);

        });

        super.heightProperty().addListener((o, ov, nv) -> {
            this.height = nv.doubleValue();

            border.setHeight(height);
            borderClip.setHeight(height);
        });

//        double[] scroll = new double[1];
//        Timeline[] anime = new Timeline[1];
//        textArea.addEventFilter(ScrollEvent.SCROLL, event -> {
//            scroll[0] -= event.getDeltaY() * 2;
//            scroll[0] = Math.min(scroll[0], textArea.getMaxScrollTop());
//            scroll[0] = Math.max(scroll[0], 0);
//
//            if (anime[0] != null) anime[0].stop();
//            anime[0] = new Timeline(new KeyFrame(Duration.millis(animeDuration), new KeyValue(textArea.scrollTopProperty(), scroll[0])));
//            anime[0].play();
//        });
//        textArea.setOnMouseDraggingScroll(e -> {
//            scroll[0] = e;
//        });

        super.getChildren().addAll(border, textArea);
        return this;
    }

    public TextArea getTextArea() {
        return this.textArea;
    }
}

class ToolArea extends TextArea {

    private ScrollPane scrollPane;
    private boolean textChanging;

    public ToolArea() {
        super.setWrapText(true);
        Platform.runLater(() -> {
            scrollPane = (ScrollPane) this.lookup(".scroll-pane");
            SimpleBooleanProperty property = new SimpleBooleanProperty(false);
            scrollPane.setUserData(property);

            Toolkit.addSmoothScrolling(scrollPane);

            super.textProperty().addListener((o, ov, nv) -> textChanging = true);

            scrollPane.vvalueProperty().addListener((o, ov, nv) -> {
                if (textChanging) {
                    property.setValue(true);
                    property.setValue(false);
                    textChanging = false;
                }
            });
        });
    }

    public double getMaxScrollTop() {
        ScrollPane scrollPane = (ScrollPane) this.lookup(".scroll-pane");
        if (scrollPane != null) {
            return scrollPane.getContent().getBoundsInLocal().getHeight() - scrollPane.getViewportBounds().getHeight();
        }
        return 0;
    }

    private boolean isDragging = false;
    public void setOnMouseDraggingScroll(ScrollListener listener) {
        Platform.runLater(() -> {
            //ScrollPane scrollPane = (ScrollPane) this.lookup(".scroll-pane");
            if (scrollPane != null) {
                // 监听鼠标按下事件
                scrollPane.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> isDragging = true);

                // 监听鼠标释放事件
                scrollPane.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> isDragging = false);

                scrollPane.vvalueProperty().addListener((observable, oldValue, newValue) -> {
                    if (isDragging) {
                        listener.onScroll(newValue.doubleValue() * getMaxScrollTop());
                    }
                });
            }
        });
    }

    public interface ScrollListener {
        void onScroll(double scrollTop);
    }
}