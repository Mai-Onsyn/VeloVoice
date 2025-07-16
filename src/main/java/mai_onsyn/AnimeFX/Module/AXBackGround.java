package mai_onsyn.AnimeFX.Module;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import mai_onsyn.AnimeFX.Styles.AXBackGroundStyle;
import mai_onsyn.AnimeFX.Styles.DefaultAXBackGroundStyle;
import mai_onsyn.AnimeFX.layout.AutoPane;

public class AXBackGround extends AutoPane {

    private AXBackGroundStyle style = new DefaultAXBackGroundStyle();

    private final ImageView imageView;
    private final Rectangle shadow;
    private final GaussianBlur effect = new GaussianBlur(0);
    double viewW, viewH;

    private Point2D mousePoint = new Point2D(0, 0);
    private double boxW;
    private double boxH;
    private double imgRatio;
    private final SimpleDoubleProperty scale = new SimpleDoubleProperty(1.0);
    private final SimpleDoubleProperty scaleStrengthProperty;
    private Timeline scaleTimeline = new Timeline();

    public AXBackGround(Image image, Color shadowColor, double scaleStrength, double blur) {
        style.setBGShadow(shadowColor);
        style.setBGBlurStrength(blur);
        effect.setRadius(blur);
        scaleStrengthProperty = new SimpleDoubleProperty(scaleStrength);

        imageView = new ImageView(image);
        imageView.setEffect(effect);
        shadow = new Rectangle(0, 0, style.getBGShadow());

        shadow.widthProperty().bind(super.widthProperty());
        shadow.heightProperty().bind(super.heightProperty());

        final double imgW = image.getWidth();
        final double imgH = image.getHeight();
        imgRatio = imgW / imgH;

        super.layoutBoundsProperty().addListener((o, ov, nv) -> {
            boxW = nv.getWidth();
            boxH = nv.getHeight();

            layoutOffset();
        });

        sceneProperty().addListener((o, ov, nv) -> {
            if (nv != null) {
                // 当组件被添加到场景时执行
                getScene().addEventFilter(MouseEvent.MOUSE_MOVED, e -> {
                    mousePoint = new Point2D(e.getX(), e.getY());
                    layoutOffset();
                });

                getScene().addEventFilter(MouseEvent.MOUSE_ENTERED, e -> {
                    mousePoint = new Point2D(e.getX(), e.getY());
                    scaleTimeline.stop();
                    scaleTimeline = new Timeline(new KeyFrame(Duration.millis(200 * style.getAnimeRate()), new KeyValue(scale, scaleStrengthProperty.get())));
                    scaleTimeline.play();
                });
                getScene().addEventFilter(MouseEvent.MOUSE_EXITED, e -> {
                    scaleTimeline.stop();
                    scaleTimeline = new Timeline(new KeyFrame(Duration.millis(200 * style.getAnimeRate()), new KeyValue(scale, 1)));
                    scaleTimeline.play();
                });
            }
        });

        scale.addListener((o, ov, nv) -> {
            layoutOffset();
        });

        super.getChildren().addAll(imageView, shadow);
    }

    private void layoutOffset() {
        double layoutRatio = boxW / boxH;

        if (layoutRatio > imgRatio) {
            viewW = boxW * scale.get();
            viewH = boxW / imgRatio * scale.get();
        }
        else {
            viewW = boxH * imgRatio * scale.get();
            viewH = boxH * scale.get();
        }

        double px = mousePoint.getX() / boxW * boxW;
        double py = mousePoint.getY() / boxH * boxH;

        imageView.setFitWidth(viewW);
        imageView.setFitHeight(viewH);
        imageView.setLayoutX((1 - scale.get()) * px - (viewW - boxW * scale.get()) / 2);
        imageView.setLayoutY((1 - scale.get()) * py - (viewH - boxH * scale.get()) / 2);
    }

    public void setBlurStrength(double strength) {
        style.setBGBlurStrength(strength);
        effect.setRadius(strength);
    }

    public void setShadowColor(Color color) {
        style.setBGShadow(color);
        shadow.setFill(color);
    }

    public void setScale(double scale) {
        this.scale.set(scale);
        this.scaleStrengthProperty.set(scale);
    }

    public void setImage(Image image) {
        imageView.setImage(image);
    }

//    private void layoutOffset() {
//        double xRatio = 0.5 * Math.sin(Math.PI * (mousePosition.getX() / super.getLayoutBounds().getWidth() - 0.5)) + 0.5;
//        double yRatio = 0.5 * Math.sin(Math.PI * (mousePosition.getY() / super.getLayoutBounds().getHeight() - 0.5)) + 0.5;
//
//        double imgViewWidth = imageView.getFitWidth();
//        double imgViewHeight = imageView.getFitHeight();
//
//        double xTotal = imgViewWidth - super.getLayoutBounds().getWidth();
//        double yTotal = imgViewHeight - super.getLayoutBounds().getHeight();
//        xOffset = xTotal * xRatio;
//        yOffset = yTotal * yRatio;
//
//        moveTimeline.stop();
//        moveTimeline = new Timeline(new KeyFrame(Duration.millis(style.getAnimeRate() * 100),
//                new KeyValue(imageView.layoutXProperty(), -xOffset),
//                new KeyValue(imageView.layoutYProperty(), -yOffset)
//        ));
//        moveTimeline.play();
//    }
}
