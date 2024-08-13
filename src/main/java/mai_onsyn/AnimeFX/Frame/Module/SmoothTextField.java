package mai_onsyn.AnimeFX.Frame.Module;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;
import mai_onsyn.AnimeFX.Frame.Layout.AutoPane;
import mai_onsyn.AnimeFX.Frame.Layout.PopupMenu;
import mai_onsyn.AnimeFX.Frame.Styles.ButtonStyle;
import mai_onsyn.AnimeFX.Frame.Styles.DefaultButtonStyle;
import mai_onsyn.AnimeFX.Frame.Utils.Toolkit;

public class SmoothTextField extends AutoPane {

    private Font font = new Font(14);
    private double width = 100;
    private double height = 0;
    private final Rectangle border = new Rectangle();
    private final Rectangle borderClip = new Rectangle();
    private int animeDuration = 200;
    private Color bgColor = new Color(1, 1, 1, 0.5);
    private Color subLineColor = new Color(0, 0, 0, 0.5);
    private Color textColor = new Color(0, 0, 0, 0.8);
    private Color textHighlightColor = new Color(0.9, 0.9, 0.9, 0.8);
    private Color textHighlightBGColor = new Color(0, 0.3, 0.66, 0.5);
    private ButtonStyle buttonStyle = new DefaultButtonStyle();

    public SmoothTextField borderRadius(double r) {
        this.border.setStrokeWidth(r);
        this.borderClip.setStrokeWidth(r);
        return this;
    }
    public SmoothTextField borderColor(Color color) {
        this.border.setStroke(color);
        this.borderClip.setStroke(color);
        return this;
    }
    public SmoothTextField borderShape(double v1) {
        border.setArcWidth(v1);
        border.setArcHeight(v1);
        borderClip.setArcWidth(v1);
        borderClip.setArcHeight(v1);
        return this;
    }
    public SmoothTextField font(Font font) {
        this.font = font;
        return this;
    }
    public SmoothTextField width(double width) {
        this.width = width;
        return this;
    }
    public SmoothTextField bgColor(Color bgColor) {
        this.bgColor = bgColor;
        return this;
    }
    public SmoothTextField animeDuration(int animeDuration) {
        this.animeDuration = animeDuration;
        return this;
    }
    public SmoothTextField textHighlightBGColor(Color textHighlightBGColor) {
        this.textHighlightBGColor = textHighlightBGColor;
        return this;
    }
    public SmoothTextField textHighlightColor(Color textHighlightColor) {
        this.textHighlightColor = textHighlightColor;
        return this;
    }
    public SmoothTextField textColor(Color textColor) {
        this.textColor = textColor;
        return this;
    }
    public SmoothTextField subLineColor(Color subLineColor) {
        this.subLineColor = subLineColor;
        return this;
    }
    public SmoothTextField buttonStyle(ButtonStyle style) {
        this.buttonStyle = style;
        return this;
    }

    private final TextField textField = new TextField();

    private boolean isFocused;

    public SmoothTextField init() {
        textField.getStylesheets().add("styles/text-field.css");
        textField.setFont(font);
        textField.setStyle(String.format(
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
            copyMenuItem.setOnMouseClicked(_ -> textField.copy());


            DiffusionButton pasteButton = buttonStyle.createButton("粘贴");
            HBox pasteMenuItem = new HBox(pasteButton);
            pasteMenuItem.setOnMouseClicked(_ -> textField.paste());

            DiffusionButton cutButton = buttonStyle.createButton("剪切");
            HBox cutMenuItem = new HBox(cutButton);
            cutMenuItem.setOnMouseClicked(_ -> textField.cut());

            new PopupMenu(50, copyMenuItem, pasteMenuItem, cutMenuItem)
                    .borderShape(20)
                    .bind(textField);
            textField.setContextMenu(new ContextMenu());
        }


        border.setFill(bgColor);
        super.setClip(borderClip);
        super.setPosition(textField, false, border.getArcWidth() / 2, border.getArcWidth() / 2, 5 * font.getSize() / 15, 5 * font.getSize() / 15);

        Line line = new Line();
        line.setStrokeWidth(0.7 * font.getSize() / 15);
        line.setStroke(subLineColor);

        super.widthProperty().addListener((o, ov, nv) -> {
            this.width = nv.doubleValue();

            border.setWidth(width);
            borderClip.setWidth(width);

            Platform.runLater(() -> {
                if (isFocused) {
                    line.setEndX(width - border.getArcWidth() / 2);
                }
                else {
                    line.setStartX(width / 2);
                    line.setEndX(width / 2);
                }
            });
        });


        Platform.runLater(() -> {
            height = Toolkit.textFieldSizeToHeight(textField) + font.getSize() / 15;
            super.setMaxHeight(height);
            super.setMinHeight(height);
            border.setHeight(height);
            borderClip.setHeight(height);
            line.setStartY(height - 3.5 * font.getSize() / 15);
            line.setEndY(height - 3.5 * font.getSize() / 15);
            line.setStartX(width / 2);
            line.setEndX(width / 2);

            textField.focusedProperty().addListener((o, ov, nv) -> {
                isFocused = nv;
                Timeline startTimeline;
                Timeline endTimeline;
                if (nv) {
                    startTimeline = new Timeline(new KeyFrame(Duration.millis(animeDuration), new KeyValue(line.startXProperty(), border.getArcWidth() / 2)));
                    endTimeline = new Timeline(new KeyFrame(Duration.millis(animeDuration), new KeyValue(line.endXProperty(), width - border.getArcWidth() / 2)));
                }
                else {
                    startTimeline = new Timeline(new KeyFrame(Duration.millis(animeDuration), new KeyValue(line.startXProperty(), width / 2)));
                    endTimeline = new Timeline(new KeyFrame(Duration.millis(animeDuration), new KeyValue(line.endXProperty(), width / 2)));
                }
                startTimeline.play();
                endTimeline.play();
            });
        });

        super.heightProperty().addListener((o, ov, nv) -> {
        });

        /*
        textField.addEventFilter(ScrollEvent.SCROLL, event -> {
            double deltaY = event.getDeltaY();
            double scroll = textField.getScrollTop() - deltaY * 3;
            scroll = Math.max(0, scroll);
            scroll = Math.min(textField.getMaxScrollTop(), scroll);
            Timeline timeline = new Timeline(new KeyFrame(Duration.millis(animeDuration), new KeyValue(textField.scrollTopProperty(), scroll)));
            timeline.play();
        });
         */

        super.getChildren().addAll(border, textField, line);
        return this;
    }



    public TextField getTextField() {
        return this.textField;
    }

    public double getConstHeight() {
        return height;
    }
}