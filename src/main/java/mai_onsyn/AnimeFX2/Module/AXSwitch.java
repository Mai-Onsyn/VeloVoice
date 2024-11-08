package mai_onsyn.AnimeFX2.Module;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Cursor;
import javafx.util.Duration;
import mai_onsyn.AnimeFX2.Styles.AXSwitchStyle;
import mai_onsyn.AnimeFX2.Styles.DefaultAXSwitchStyle;

public class AXSwitch extends AXBase {

    private AXSwitchStyle style = new DefaultAXSwitchStyle();

    private final AXBase thumb = new AXBase();
    private final SimpleBooleanProperty stateProperty;

    private final SimpleDoubleProperty thumbInsetsProperty;

    private Timeline thumbTimeline = new Timeline();
    private Timeline thumbScaleTimeline = new Timeline();

    public AXSwitch(boolean b) {
        stateProperty = new SimpleBooleanProperty(b);
        thumbInsetsProperty = new SimpleDoubleProperty(style.getThumbInsetsScale());
        thumb.setTheme(b ? style.getSwitchedThumbStyle() : style.getThumbStyle());
        super.setTheme(b ? style.getSwitchedTrackStyle() : style.getTrackStyle());

        thumb.layoutYProperty().bind(super.prefHeightProperty().multiply(thumbInsetsProperty));

        DoubleBinding sizeBinding = super.prefHeightProperty().subtract(thumbInsetsProperty.multiply(2).multiply(super.prefHeightProperty()));
        thumb.maxWidthProperty().bind(sizeBinding);
        thumb.minWidthProperty().bind(sizeBinding);

        thumb.maxHeightProperty().bind(sizeBinding);
        thumb.minHeightProperty().bind(sizeBinding);

        this.setCursor(Cursor.HAND);

        Platform.runLater(() -> playMovement(b, 1));

        stateProperty.addListener((_, _, val) -> {
            thumb.setTheme(val ? style.getSwitchedThumbStyle() : style.getThumbStyle());
            super.setTheme(val ? style.getSwitchedTrackStyle() : style.getTrackStyle());

            playMovement(val, 200 * style.getAnimeRate());

            update();
        });

        super.setOnMouseClicked(e -> {
            stateProperty.set(!stateProperty.get());
        });

        super.setOnMouseEntered(event -> {
            thumbScaleTimeline.stop();
            thumbScaleTimeline = new Timeline(new KeyFrame(Duration.millis(200 * style.getAnimeRate()),
                    new KeyValue(thumb.scaleXProperty(), thumb.isPressed ? style.getPressedScale() : style.getHoveredScale()),
                    new KeyValue(thumb.scaleYProperty(), thumb.isPressed ? style.getPressedScale() : style.getHoveredScale())
            ));
            thumbScaleTimeline.play();
        });
        super.setOnMouseExited(event -> {
            thumbScaleTimeline.stop();
            thumbScaleTimeline = new Timeline(new KeyFrame(Duration.millis(200 * style.getAnimeRate()),
                    new KeyValue(thumb.scaleXProperty(), thumb.isPressed ? style.getPressedScale() : 1),
                    new KeyValue(thumb.scaleYProperty(), thumb.isPressed ? style.getPressedScale() : 1)
            ));
            thumbScaleTimeline.play();
        });
        super.setOnMousePressed(event -> {

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
        super.setOnMouseReleased(event -> {
            thumbScaleTimeline.stop();
            thumbScaleTimeline = new Timeline(new KeyFrame(Duration.millis(200 * style.getAnimeRate()),
                    new KeyValue(thumb.scaleXProperty(), thumb.isHover() ? style.getHoveredScale() : 1),
                    new KeyValue(thumb.scaleYProperty(), thumb.isHover() ? style.getHoveredScale() : 1)
            ));
            thumbScaleTimeline.play();
        });

        super.widthProperty().addListener((_, _, _) -> Platform.runLater(() -> playMovement(stateProperty.get(), 1)));

        super.getChildren().add(thumb);

        update();
    }

    private void playMovement(boolean b, double duration) {
        thumbTimeline.stop();
        thumbTimeline = new Timeline(new KeyFrame(Duration.millis(duration),
                new KeyValue(thumb.layoutXProperty(), b ? super.getPrefWidth() - style.getThumbInsetsScale() * super.getPrefHeight() - thumb.getLayoutBounds().getWidth() : style.getThumbInsetsScale() * super.getPrefHeight())
        ));
        thumbTimeline.play();
    }

    public SimpleBooleanProperty stateProperty() {
        return stateProperty;
    }

    public void setTheme(AXSwitchStyle style) {
        this.style = style;
    }

    @Override
    public void update() {
        super.update();
        thumb.update();
        thumbInsetsProperty.set(style.getThumbInsetsScale());
    }
}
