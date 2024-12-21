package mai_onsyn.AnimeFX2.Module;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import mai_onsyn.AnimeFX.Frame.Layout.AutoPane;
import mai_onsyn.AnimeFX2.AutoUpdatable;
import mai_onsyn.AnimeFX2.Styles.AXBackGroundStyle;
import mai_onsyn.AnimeFX2.Styles.DefaultAXBackGroundStyle;

public class AXBackGround extends AutoPane implements AutoUpdatable {

    private AXBackGroundStyle style = new DefaultAXBackGroundStyle();

    private final ImageView imageView;
    private final Rectangle shadow;
    private final GaussianBlur effect = new GaussianBlur(0);
    double viewW, viewH;

    private Point2D layoutCorner = new Point2D(0, 0);
    private double layoutWidth;
    private double layoutHeight;
//    private Timeline scaleTimeline = new Timeline();
//    private Timeline moveTimeline = new Timeline();
//    private double xOffset;
//    private double yOffset;
//    private Point2D mousePosition = new Point2D(0, 0);

    public AXBackGround(Image image, Color shadowColor, double blur) {
        style.setBGShadow(shadowColor);
        style.setBGBlurStrength(blur);
        effect.setRadius(blur);

        imageView = new ImageView(image);
        imageView.setEffect(effect);
        shadow = new Rectangle(0, 0, style.getBGShadow());

        shadow.widthProperty().bind(super.widthProperty());
        shadow.heightProperty().bind(super.heightProperty());

        final double imgW = image.getWidth();
        final double imgH = image.getHeight();
        final double imgRatio = imgW / imgH;

        super.layoutBoundsProperty().addListener((o, ov, nv) -> {
            layoutWidth = nv.getWidth();
            layoutHeight = nv.getHeight();

            double layoutRatio = layoutWidth / layoutHeight;

            if (layoutRatio > imgRatio) {
                viewW = layoutWidth;
                viewH = layoutWidth / imgRatio;
            }
            else {
                viewW = layoutHeight * imgRatio;
                viewH = layoutHeight;
            }
            layoutCorner = new Point2D((layoutWidth - viewW) / 2, (layoutHeight - viewH) / 2);

            imageView.setFitWidth(viewW);
            imageView.setFitHeight(viewH);
            imageView.setLayoutX(layoutCorner.getX());
            imageView.setLayoutY(layoutCorner.getY());
        });

//        super.setOnMouseMoved(e -> {
//            mousePosition = new Point2D(e.getX(), e.getY());
//
//            layoutOffset();
//        });
//
//        super.setOnMouseEntered(e -> {
//            //layoutCorner = new Point2D((layoutWidth - viewW * scaleStrengthProperty.get()), (layoutHeight - viewH * scaleStrengthProperty.get()));
//            //layoutOffset();
//            scaleTimeline.stop();
//            scaleTimeline = new Timeline(new KeyFrame(Duration.millis(style.getAnimeRate() * 200),
//                    new KeyValue(imageView.fitWidthProperty(), viewW * scaleStrengthProperty.get()),
//                    new KeyValue(imageView.fitHeightProperty(), viewH * scaleStrengthProperty.get()),
//                    new KeyValue(imageView.layoutXProperty(), xOffset),
//                    new KeyValue(imageView.layoutYProperty(), yOffset)
//            ));
//            scaleTimeline.play();
//        });
//
//        super.setOnMouseExited(e -> {
//            scaleTimeline.stop();
//            scaleTimeline = new Timeline(new KeyFrame(Duration.millis(style.getAnimeRate() * 200),
//                    new KeyValue(imageView.fitWidthProperty(), viewW),
//                    new KeyValue(imageView.fitHeightProperty(), viewH),
//                    new KeyValue(imageView.layoutXProperty(), (layoutWidth - viewW) / 2),
//                    new KeyValue(imageView.layoutYProperty(), (layoutHeight - viewH) / 2)
//            ));
//            scaleTimeline.play();
//        });
        super.getChildren().addAll(imageView, shadow);
    }

    public void setBlurStrength(double strength) {
        style.setBGBlurStrength(strength);
        effect.setRadius(strength);
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

    @Override
    public void update() {

    }
}
