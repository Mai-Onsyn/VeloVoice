package mai_onsyn.AnimeFX.Frame.Module;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Slider;
import javafx.scene.control.skin.SliderSkin;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;
import mai_onsyn.AnimeFX.Frame.Utils.Toolkit;

public class SmoothSlider extends Slider {

    private boolean pressing;
    private boolean hovering;

    private double width = 300;
    private double height = 2;
    private int animeDuration = 200;
    private Color trackColor = new Color(1, 1, 1, 0.5);
    private Color trackProgressColor = new Color(0, 0.5, 1, 0.5);
    private Color thumbColor = new Color(1, 1, 1, 0.5);
    private Color hoverColor = new Color(0.8, 0.8, 0.8, 0.5);
    private Color pressedColor = new Color(0.6, 0.6, 0.6, 0.5);
    private Shape thumb = new Circle(10, thumbColor);

    public SmoothSlider thumb(Shape thumb) {
        this.thumb = thumb;
        thumbColor = (Color) thumb.getFill();
        return this;
    }
    public SmoothSlider width(double width) {
        this.width = width;
        return this;
    }
    public SmoothSlider height(double height) {
        this.height = height;
        return this;
    }
    public SmoothSlider animeDuration(int animeDuration) {
        this.animeDuration = animeDuration;
        return this;
    }
    public SmoothSlider trackColor(Color bgColor) {
        this.trackColor = bgColor;
        return this;
    }
    public SmoothSlider trackProgressColor(Color trackProgressColor) {
        this.trackProgressColor = trackProgressColor;
        return this;
    }
    public SmoothSlider thumbColor(Color thumbColor) {
        this.thumbColor = thumbColor;
        thumb.setFill(thumbColor);
        return this;
    }
    public SmoothSlider hoverColor(Color hoverColor) {
        this.hoverColor = hoverColor;
        return this;
    }
    public SmoothSlider pressedColor(Color pressedColor) {
        this.pressedColor = pressedColor;
        return this;
    }

    public SmoothSlider(double min, double max, double value) {
        super(min, max, value);
    }

    public SmoothSlider init() {
        super.getStylesheets().add("styles/slider.css");
        super.setMajorTickUnit(0.5);
        Platform.runLater(() -> super.lookup(".track").setStyle(String.format(
                """
                -fx-background-color: linear-gradient(to right, #%s %.2f%%, #%s %.2f%%);
                -fx-padding: %.2fpx;
                """,
                Toolkit.colorToString(trackProgressColor),
                (super.getValue() - super.getMin()) / (super.getMax() - super.getMin()) * 100,
                Toolkit.colorToString(trackColor),
                (super.getValue() - super.getMin()) / (super.getMax() - super.getMin()) * 100,
                height
                )));

        super.setPrefWidth(width);

        thumb.setScaleX(1);
        thumb.setScaleY(1);

        super.setSkin(new CustomSliderSkin(this, thumb));

        super.valueProperty().addListener((o, ov, nv) -> {
            double percentage = (nv.doubleValue() - super.getMin()) / (super.getMax() - super.getMin()) * 100;

            Node track = super.lookup(".track");
            track.setStyle(String.format(
                            """
                            -fx-background-color: linear-gradient(to right, #%s %.2f%%, #%s %.2f%%);
                            -fx-padding: %.2fpx;
                            """,
                    Toolkit.colorToString(trackProgressColor),
                    percentage,
                    Toolkit.colorToString(trackColor),
                    percentage,
                    height
                    ));
        });

        thumb.setOnMouseEntered(event -> {
            hovering = true;
            thumb.setCursor(Cursor.HAND);
            Timeline increase = new Timeline(new KeyFrame(Duration.millis(animeDuration), new KeyValue(thumb.scaleXProperty(), 1.4), new KeyValue(thumb.scaleYProperty(), 1.4)));
            Timeline colorTrans = new Timeline(new KeyFrame(Duration.millis(animeDuration), new KeyValue(thumb.fillProperty(), hoverColor)));
            increase.play();
            colorTrans.play();
        });

        thumb.setOnMouseExited(event -> {
            hovering = false;
            Thread.ofVirtual().start(() -> {
                try {
                    while (pressing) {
                        Thread.sleep(10);
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                Timeline decrease = new Timeline(new KeyFrame(Duration.millis(animeDuration), new KeyValue(thumb.scaleXProperty(), 1), new KeyValue(thumb.scaleYProperty(), 1)));
                Timeline colorTrans = new Timeline(new KeyFrame(Duration.millis(animeDuration), new KeyValue(thumb.fillProperty(), thumbColor)));
                decrease.play();
                colorTrans.play();
            });
        });

        thumb.setOnMousePressed(event -> {
            pressing = true;
            Timeline decrease = new Timeline(new KeyFrame(Duration.millis(animeDuration / 2.0), new KeyValue(thumb.scaleXProperty(), 1.1), new KeyValue(thumb.scaleYProperty(), 1.25)));
            Timeline colorTrans = new Timeline(new KeyFrame(Duration.millis(animeDuration), new KeyValue(thumb.fillProperty(), pressedColor)));
            decrease.play();
            colorTrans.play();
            decrease.setOnFinished(f -> {
                Timeline increase = new Timeline(new KeyFrame(Duration.millis(animeDuration / 2.0), new KeyValue(thumb.scaleXProperty(), 1.4), new KeyValue(thumb.scaleYProperty(), 1.4)));
                increase.play();
            });
        });
        thumb.setOnMouseReleased(event -> {
            pressing = false;
            Timeline colorTrans = new Timeline(new KeyFrame(Duration.millis(animeDuration), new KeyValue(thumb.fillProperty(), hovering ? hoverColor : thumbColor)));
            colorTrans.play();
        });

        return this;
    }
}

class CustomSliderSkin extends SliderSkin {

    public CustomSliderSkin(Slider slider, Shape thumbShape) {
        super(slider);

        getChildren().stream()
                .filter(node -> "thumb".equals(node.getStyleClass().getFirst()))
                .forEach(node -> {
                    if (node instanceof StackPane thumbPane) {
                        thumbPane.getChildren().clear(); // 清除原有的 Thumb
                        thumbPane.getChildren().add(thumbShape); // 添加自定义的 Thumb
                    }
                });
    }
}