package mai_onsyn.AnimeFX.Frame.Module;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Cursor;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class SmoothSwitch extends Pane {

    private double width = 100;
    private double height = 50;
    private int animeDuration = 200;
    private Color bgColor = new Color(1, 1, 1, 0.5);
    private Color bgOpenColor = new Color(0, 1, 0, 0.5);
    private Color thumbColor = new Color(1, 0.8, 0.5, 0.5);
    private Color thumbHoverColor = new Color(0.9, 0.4, 0.4, 0.5);
    private Color thumbPressedColor = new Color(0.8, 0.0, 0.3, 0.5);
    private Color bgHoverColor = new Color(0.8, 0.8, 0.8, 0.5);
    private Color bgOpenHoverColor = new Color(0, 0.8, 0, 0.5);
    private Color thumbOpenColor = new Color(0, 0.2, 1, 0.5);
    private Color thumbOpenHoverColor = new Color(0.4, 0.5, 0, 0.5);
    private Color thumbOpenPressedColor = new Color(0.8, 0.2, 0, 0.5);

    private final Rectangle border = new Rectangle(0, 0, bgColor);
    private final Rectangle borderClip = new Rectangle();
    private final Circle circle = new Circle(10, thumbColor);

    public SmoothSwitch borderRadius(double r) {
        this.border.setStrokeWidth(r);
        this.borderClip.setStrokeWidth(r);
        return this;
    }
    public SmoothSwitch borderColor(Color color) {
        this.border.setStroke(color);
        this.borderClip.setStroke(color);
        return this;
    }
    public SmoothSwitch thumbBorderRadius(double r) {
        this.circle.setStrokeWidth(r);
        return this;
    }
    public SmoothSwitch thumbBorderColor(Color color) {
        this.circle.setStroke(color);
        return this;
    }
    public SmoothSwitch width(double width) {
        this.width = width;
        return this;
    }
    public SmoothSwitch height(double height) {
        this.height = height;
        return this;
    }
    public SmoothSwitch animeDuration(int animeDuration) {
        this.animeDuration = animeDuration;
        return this;
    }
    public SmoothSwitch state(boolean state) {
        this.open.setValue(state);
        return this;
    }
    public SmoothSwitch bgColor(Color bgColor) {
        this.bgColor = bgColor;
        return this;
    }
    public SmoothSwitch thumbPressedColor(Color thumbPressedColor) {
        this.thumbPressedColor = thumbPressedColor;
        return this;
    }
    public SmoothSwitch thumbHoverColor(Color thumbHoverColor) {
        this.thumbHoverColor = thumbHoverColor;
        return this;
    }
    public SmoothSwitch thumbColor(Color thumbColor) {
        this.thumbColor = thumbColor;
        return this;
    }
    public SmoothSwitch bgOpenColor(Color bgSwitchedColor) {
        this.bgOpenColor = bgSwitchedColor;
        return this;
    }
    public SmoothSwitch bgHoverColor(Color bgHoverColor) {
        this.bgHoverColor = bgHoverColor;
        return this;
    }
    public SmoothSwitch bgOpenHoverColor(Color bgOpenHoverColor) {
        this.bgOpenHoverColor = bgOpenHoverColor;
        return this;
    }
    public SmoothSwitch thumbOpenColor(Color thumbOpenColor) {
        this.thumbOpenColor = thumbOpenColor;
        return this;
    }
    public SmoothSwitch thumbOpenHoverColor(Color thumbOpenHoverColor) {
        this.thumbOpenHoverColor = thumbOpenHoverColor;
        return this;
    }
    public SmoothSwitch thumbOpenPressedColor(Color thumbOpenPressedColor) {
        this.thumbOpenPressedColor = thumbOpenPressedColor;
        return this;
    }

    private boolean pressing;
    private boolean hovering;
    private final SimpleBooleanProperty open = new SimpleBooleanProperty(false);
    private Timeline scaleTrans = new Timeline();
    public SmoothSwitch init() {
        border.setWidth(width);
        border.setHeight(height);
        borderClip.setWidth(width);
        borderClip.setHeight(height);
        border.setArcWidth(height);
        border.setArcHeight(height);
        borderClip.setArcWidth(height);
        borderClip.setArcHeight(height);

        circle.setLayoutX(open.getValue() ? width - height / 2 : height / 2);
        circle.setLayoutY(height / 2);
        circle.setRadius(height * 0.35);
        circle.setFill(open.getValue() ? thumbOpenColor : thumbColor);

        border.setFill(open.getValue() ? bgOpenColor : bgColor);

        super.setOnMouseEntered(event -> {
            hovering = true;
            super.setCursor(Cursor.HAND);

            scaleTrans.stop();
            scaleTrans = new Timeline(new KeyFrame(Duration.millis(animeDuration), new KeyValue(circle.radiusProperty(), height * 0.4)));
            Timeline colorTrans = new Timeline(new KeyFrame(Duration.millis(animeDuration), new KeyValue(circle.fillProperty(), open.getValue() ? thumbOpenHoverColor : thumbHoverColor)));
            Timeline bgColorTrans = new Timeline(new KeyFrame(Duration.millis(animeDuration), new KeyValue(border.fillProperty(), open.getValue() ? bgOpenHoverColor : bgHoverColor)));
            scaleTrans.play();
            colorTrans.play();
            bgColorTrans.play();
        });

        super.setOnMouseExited(event -> {
            hovering = false;

            Thread.ofVirtual().start(() -> {
                try {
                    while (pressing) {
                        Thread.sleep(10);
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                scaleTrans.stop();
                scaleTrans = new Timeline(new KeyFrame(Duration.millis(animeDuration), new KeyValue(circle.radiusProperty(), height * 0.35)));
                Timeline colorTrans = new Timeline(new KeyFrame(Duration.millis(animeDuration), new KeyValue(circle.fillProperty(), open.getValue() ? thumbOpenColor : thumbColor)));
                Timeline bgColorTrans = new Timeline(new KeyFrame(Duration.millis(animeDuration), new KeyValue(border.fillProperty(), open.getValue() ? bgOpenColor : bgColor)));
                scaleTrans.play();
                colorTrans.play();
                bgColorTrans.play();
            });
        });

        super.setOnMousePressed(event -> {
            pressing = true;
            open.setValue(!open.getValue());

            scaleTrans.stop();
            scaleTrans = new Timeline(new KeyFrame(Duration.millis(animeDuration / 2.0), new KeyValue(circle.radiusProperty(), height * 0.25)));
            Timeline colorTrans = new Timeline(new KeyFrame(Duration.millis(animeDuration), new KeyValue(circle.fillProperty(), open.getValue() ? thumbOpenPressedColor : thumbPressedColor)));
            scaleTrans.play();
            colorTrans.play();

            scaleTrans.setOnFinished(f -> {
                scaleTrans = new Timeline(new KeyFrame(Duration.millis(animeDuration / 2.0), new KeyValue(circle.radiusProperty(), height * (pressing ? 0.3 : 0.4))));
                scaleTrans.play();
            });

            Timeline posTrans = new Timeline(new KeyFrame(Duration.millis(animeDuration / 2.0), new KeyValue(circle.layoutXProperty(), open.getValue() ? width - height / 2 : height / 2)));
            Timeline bgTrans = new Timeline(new KeyFrame(Duration.millis(animeDuration), new KeyValue(border.fillProperty(), open.getValue() ? (hovering ? bgOpenHoverColor : bgOpenColor) : (hovering ? bgHoverColor : bgColor))));
            posTrans.play();
            bgTrans.play();
        });

        super.setOnMouseReleased(event -> {
            pressing = false;

            Timeline colorTrans = new Timeline(new KeyFrame(Duration.millis(animeDuration), new KeyValue(circle.fillProperty(), open.getValue() ? (hovering ? thumbOpenHoverColor : thumbOpenColor) : (hovering ? thumbHoverColor : thumbColor))));
            colorTrans.play();
            if (hovering && !pressing) {
                scaleTrans.stop();
                scaleTrans = new Timeline(new KeyFrame(Duration.millis(animeDuration), new KeyValue(circle.radiusProperty(), height * 0.4)));
                scaleTrans.play();
            }
        });

        super.disabledProperty().addListener((o, ov, nv) -> {
            if (nv) super.setOpacity(0.5);
            else super.setOpacity(1);
        });

        super.setClip(borderClip);
        super.getChildren().addAll(border, circle);

        return this;
    }

    public double getFixedWidth() {
        return width;
    }

    public double getFixedHeight() {
        return height;
    }

    public BooleanProperty stateProperty() {
        return this.open;
    }
}