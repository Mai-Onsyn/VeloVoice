package mai_onsyn.AnimeFX.Module;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Cursor;
import javafx.util.Duration;
import mai_onsyn.AnimeFX.AutoUpdatable;
import mai_onsyn.AnimeFX.Styles.AXSliderStyle;
import mai_onsyn.AnimeFX.Styles.DefaultAXSliderStyle;
import mai_onsyn.AnimeFX.layout.AutoPane;

public class AXSlider extends AutoPane implements AutoUpdatable {

    private AXSliderStyle style = new DefaultAXSliderStyle();
    private final AXProgressBar track = new AXProgressBar();
    private final AXBase thumb = new AXBase();

    private final SimpleDoubleProperty valueProperty;
    private final SimpleDoubleProperty minProperty;
    private final SimpleDoubleProperty maxProperty;
    private final SimpleDoubleProperty stepProperty;
    private final SimpleDoubleProperty progressProperty;

    private Timeline thumbXTimeline = new Timeline();
    private Timeline thumbScaleTimeline = new Timeline();

    public AXSlider(double min, double max, double step, double value) {

        if (max - min < step || step <= 0) {
            throw new IllegalArgumentException("Invalid parameters");
        }
        if (!testIsDividable(max - min, step)) {
            throw new IllegalArgumentException("Steps are not divisible");
        }

        track.setTheme(style.getTrackStyle());
        thumb.setTheme(style.getThumbStyle());
        minProperty = new SimpleDoubleProperty(min);
        maxProperty = new SimpleDoubleProperty(max);
        stepProperty = new SimpleDoubleProperty(step);
        valueProperty = new SimpleDoubleProperty(value);
        progressProperty = new SimpleDoubleProperty();

        progressProperty.bind(valueProperty.subtract(minProperty).divide(maxProperty.subtract(minProperty)));

        track.progressProperty().bindBidirectional(progressProperty);
        bindTrackAndThumbLayout();
        thumb.setCursor(Cursor.HAND);
        thumb.setOnMouseDragged(event -> {
            //获取鼠标当前的screenX坐标和上次记录的screenX差值
            double deltaX = event.getScreenX() - (double) thumb.getUserData();
            double thumbX = event.getScreenX();

            // 更新当前鼠标位置为上次的起点
            thumb.setUserData(event.getScreenX());

            // 获取滑动条的左边界和右边界的 screenX 坐标
            double trackStartX = track.localToScreen(track.getBoundsInLocal()).getMinX();
            double trackEndX = track.localToScreen(track.getBoundsInLocal()).getMaxX();
            double trackWidth = track.getWidth();
            double progress = (thumbX - trackStartX) / trackWidth;

            if (event.getScreenX() < trackStartX) {
                // 当鼠标在滑动条左边界之外时，将进度设为 0
                progress = 0;
            }
            if (event.getScreenX() > trackEndX) {
                // 当鼠标在滑动条右边界之外时，将进度设为 1
                progress = 1;
            }

            double v = minProperty.get() + (maxProperty.get() - minProperty.get()) * progress;
            valueProperty.set(roundValueToSteps(v, minProperty.get(), maxProperty.get(), stepProperty.get()));
        });

//        minProperty.addListener((o, ov, nv) -> minProgress = stepProperty.get() / (maxProperty.get() - nv.doubleValue()));
//        maxProperty.addListener((o, ov, nv) -> maxProgress = stepProperty.get() / (nv.doubleValue() - minProperty.get()));
//        minProperty.set(min);
//        maxProperty.set(max);
//
//        valueProperty.bind(progressProperty.multiply(max - min));
//        track.progressProperty().bind(progressProperty);
//
//        bindTrackAndThumbLayout();
//        thumb.setCursor(Cursor.HAND);
//
//        thumb.setOnMouseDragged(event -> {
//            // 获取鼠标当前的screenX坐标和上次记录的screenX差值
//            double deltaX = event.getScreenX() - (double) thumb.getUserData();
//
//            // 更新当前鼠标位置为上次的起点
//            thumb.setUserData(event.getScreenX());
//
//            // 获取滑动条的左边界和右边界的 screenX 坐标
//            double trackStartX = track.localToScreen(track.getBoundsInLocal()).getMinX();
//            double trackEndX = track.localToScreen(track.getBoundsInLocal()).getMaxX();
//            double trackWidth = track.getWidth();
//
//            if (event.getScreenX() < trackStartX) {
//                // 当鼠标在滑动条左边界之外时，将进度设为 0
//                preciseValue = 0;
//                progressProperty.set(0);
//            } else if (event.getScreenX() > trackEndX) {
//                // 当鼠标在滑动条右边界之外时，将进度设为 1
//                preciseValue = 1;
//                progressProperty.set(1);
//            } else {
//                // 如果鼠标在滑动条范围内，计算相对滑动距离并更新进度
//                double deltaProgress = deltaX / trackWidth;
//
//                // 更新 progressProperty，并限制在 [0, 1] 范围内
//                double newProgress = Math.max(0, Math.min(preciseValue + deltaProgress, 1));
//                preciseValue = newProgress;
//
//                double stepValue = Math.max(minProperty.get(), Math.min(maxProperty.get(), stepProperty.get() / (maxProperty.get() - minProperty.get())));
//                newProgress = Math.round(newProgress / stepValue) * stepValue;
//                System.out.println(newProgress);
//
//                progressProperty.set(newProgress);
//            }
//        });

        thumb.setOnMouseEntered(event -> {
            thumbScaleTimeline.stop();
            thumbScaleTimeline = new Timeline(new KeyFrame(Duration.millis(200 * style.getAnimeRate()),
                    new KeyValue(thumb.scaleXProperty(), thumb.isPressed ? style.getPressedScale() : style.getHoveredScale()),
                    new KeyValue(thumb.scaleYProperty(), thumb.isPressed ? style.getPressedScale() : style.getHoveredScale())
            ));
            thumbScaleTimeline.play();
        });
        thumb.setOnMouseExited(event -> {
            thumbScaleTimeline.stop();
            thumbScaleTimeline = new Timeline(new KeyFrame(Duration.millis(200 * style.getAnimeRate()),
                    new KeyValue(thumb.scaleXProperty(), thumb.isPressed ? style.getPressedScale() : 1),
                    new KeyValue(thumb.scaleYProperty(), thumb.isPressed ? style.getPressedScale() : 1)
            ));
            thumbScaleTimeline.play();
        });
        thumb.setOnMousePressed(event -> {
            thumb.setUserData(event.getScreenX());

            thumbScaleTimeline.stop();
            thumbScaleTimeline = new Timeline(new KeyFrame(Duration.millis(100 * style.getAnimeRate()),
                    new KeyValue(thumb.scaleXProperty(), style.getClickedScale()),
                    new KeyValue(thumb.scaleYProperty(), style.getClickedScale())
            ));
            thumbScaleTimeline.setOnFinished(_ -> {
                new Timeline(new KeyFrame(Duration.millis(100 * style.getAnimeRate()),
                        new KeyValue(thumb.scaleXProperty(), style.getPressedScale()),
                        new KeyValue(thumb.scaleYProperty(), style.getPressedScale())
                )).play();
            });
            thumbScaleTimeline.play();
        });
        thumb.setOnMouseReleased(event -> {
            thumbScaleTimeline.stop();
            thumbScaleTimeline = new Timeline(new KeyFrame(Duration.millis(200 * style.getAnimeRate()),
                    new KeyValue(thumb.scaleXProperty(), thumb.isHover() ? style.getHoveredScale() : 1),
                    new KeyValue(thumb.scaleYProperty(), thumb.isHover() ? style.getHoveredScale() : 1)
            ));
            thumbScaleTimeline.play();
        });
        super.getChildren().addAll(track, thumb);
        update();
    }

    /**
     * 将值v限制在[min, max]范围内，并将其四舍五入到步长step的整数倍
     * @param v 值
     * @param min 最小值
     * @param max 最大值
     * @param step 步长
     * @return 四舍五入后的值
     */
    private double roundValueToSteps(double v, double min, double max, double step) {
        // 限制v在[min, max]范围内
        if (v < min) {
            v = min;
        } else if (v > max) {
            v = max;
        }

        // 将v四舍五入到最接近的步长
        return Math.round((v - min) / step) * step + min;
    }

    private void bindTrackAndThumbLayout() {
        track.maxWidthProperty().bind(super.widthProperty());
        track.minWidthProperty().bind(super.widthProperty());
        track.layoutYProperty().bind(super.heightProperty().divide(2).subtract(track.heightProperty().divide(2)));

        progressProperty.addListener((o, ov, nv) -> moveThumb(nv.doubleValue(), 200 * style.getAnimeRate()));
        thumb.layoutYProperty().bind(super.heightProperty().divide(2).subtract(thumb.heightProperty().divide(2)));
        super.widthProperty().addListener((o, ov, nv) -> Platform.runLater(() -> moveThumb(progressProperty.get(), 1)));
    }

    private void moveThumb(double v, double duration) {
        double trackWidth = track.getBoundsInLocal().getWidth();
        double thumbWidth = thumb.getBoundsInLocal().getWidth();
        double targetX = v * trackWidth - thumbWidth / 2;

        thumbXTimeline.stop();
        thumbXTimeline = new Timeline(
                new KeyFrame(Duration.millis(duration),
                        new KeyValue(thumb.layoutXProperty(), targetX)
                )
        );
        thumbXTimeline.play();
    }


    @Override
    public void update() {
        track.setMaxHeight(style.getTrackHeight());
        track.setMinHeight(style.getTrackHeight());

        thumb.setMaxSize(style.getThumbWidth(), style.getThumbHeight());
        thumb.setMinSize(style.getThumbWidth(), style.getThumbHeight());

        track.update();
        thumb.update();
    }

    public void setTheme(AXSliderStyle style) {
        this.style = style;
        track.setTheme(style.getTrackStyle());
        thumb.setTheme(style.getThumbStyle());
    }

    public SimpleDoubleProperty valueProperty() {
        return valueProperty;
    }

    private static boolean testIsDividable(double a, double b) {
        long aa = (long) (a * 0x100000000L);
        long bb = (long) (b * 0x100000000L);

        return aa % bb < 0x100000000L;
    }
}
