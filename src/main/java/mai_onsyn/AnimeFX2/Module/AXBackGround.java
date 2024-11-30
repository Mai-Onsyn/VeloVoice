package mai_onsyn.AnimeFX2.Module;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
    private final SimpleDoubleProperty scaleStrengthProperty;
    double viewW, viewH;

    private Point2D layoutCorner = new Point2D(0, 0);

    public AXBackGround(Image image, double scaleStrength) {
        imageView = new ImageView(image);
        shadow = new Rectangle(0, 0, style.getBGShadow());
        scaleStrengthProperty = new SimpleDoubleProperty(scaleStrength);

        shadow.widthProperty().bind(super.widthProperty());
        shadow.heightProperty().bind(super.heightProperty());

        final double imgW = image.getWidth();
        final double imgH = image.getHeight();
        final double imgRatio = imgW / imgH;

        super.setOnMouseMoved(e -> {
            double x = e.getX();
            double y = e.getY();

            // 鼠标在父容器中的相对位置比例
            double xRatio = x / super.getLayoutBounds().getWidth();
            double yRatio = y / super.getLayoutBounds().getHeight();

            xRatio = 0.5 * Math.cbrt(2 * xRatio - 1) + 0.5;
            yRatio = 0.5 * Math.cbrt(2 * yRatio - 1) + 0.5;

            // 计算图片的显示宽度和高度（考虑放大后的尺寸）
            double imgViewWidth = imageView.getFitWidth();
            double imgViewHeight = imageView.getFitHeight();

            double xTotal = imgViewWidth - super.getLayoutBounds().getWidth();
            double yTotal = imgViewHeight - super.getLayoutBounds().getHeight();
            double xOffset = xTotal * xRatio;
            double yOffset = yTotal * yRatio;

            imageView.setLayoutX(-xOffset);
            imageView.setLayoutY(-yOffset);
        });

        super.layoutBoundsProperty().addListener((o, ov, nv) -> {
            double width = nv.getWidth();
            double height = nv.getHeight();

            double layoutRatio = width / height;

            if (layoutRatio > imgRatio) {
                viewW = width;
                viewH = width / imgRatio;
            }
            else {
                viewW = height * imgRatio;
                viewH = height;
            }
            layoutCorner = new Point2D((width - viewW) / 2 * scaleStrengthProperty.get(), (height - viewH) / 2 * scaleStrengthProperty.get());

            imageView.setFitWidth(viewW);
            imageView.setFitHeight(viewH);
        });

        super.setOnMouseEntered(e -> {
            Timeline timeline = new Timeline(new KeyFrame(Duration.millis(200),
                    new KeyValue(imageView.fitWidthProperty(), viewW * scaleStrengthProperty.get()),
                    new KeyValue(imageView.fitHeightProperty(), viewH * scaleStrengthProperty.get()),
                    new KeyValue(imageView.layoutXProperty(), layoutCorner.getX()),
                    new KeyValue(imageView.layoutYProperty(), layoutCorner.getY())
            ));
            timeline.play();
        });

        super.setOnMouseExited(e -> {
            Timeline timeline = new Timeline(new KeyFrame(Duration.millis(200),
                    new KeyValue(imageView.fitWidthProperty(), viewW),
                    new KeyValue(imageView.fitHeightProperty(), viewH),
                    new KeyValue(imageView.layoutXProperty(), (super.getLayoutBounds().getWidth() - viewW) / 2),
                    new KeyValue(imageView.layoutYProperty(), (super.getLayoutBounds().getHeight() - viewH) / 2)
            ));
            timeline.play();
        });


        super.getChildren().addAll(imageView, shadow);
    }


    @Override
    public void update() {

    }
}
