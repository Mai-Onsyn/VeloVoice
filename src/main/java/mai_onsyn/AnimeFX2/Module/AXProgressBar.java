package mai_onsyn.AnimeFX2.Module;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import mai_onsyn.AnimeFX2.Styles.AXProgressBarStyle;
import mai_onsyn.AnimeFX2.Styles.DefaultAXProgressBarStyle;

public class AXProgressBar extends AXBase {

    private AXProgressBarStyle style = new DefaultAXProgressBarStyle();
    private final Rectangle progressBar = new Rectangle();

    private final SimpleDoubleProperty progress = new SimpleDoubleProperty(0);

    private final SimpleDoubleProperty insets = new SimpleDoubleProperty(0);
    private Timeline progressTimeline = new Timeline();


    public AXProgressBar() {
        super();
        super.setTheme(style);
        insets.set(style.getBorderInsets());

        progressBar.layoutXProperty().bind(insets);
        progressBar.layoutYProperty().bind(insets);
        progressBar.heightProperty().bind(super.heightProperty().subtract(insets.multiply(2)));

        progressBar.setFill(style.getInnerBGColor());
        progressBar.setArcWidth(style.getInnerBorderArcSize());
        progressBar.setArcHeight(style.getInnerBorderArcSize());
        progressBar.setStrokeWidth(style.getInnerBorderRadius());
        progressBar.setStroke(style.getInnerBorderColor());

        super.getChildren().add(progressBar);
    }


    public void setProgress(double p) {
        p = Math.max(0, Math.min(1, p));
        double width = (super.getLayoutBounds().getWidth() - insets.get() * 2) * p;

        Duration duration;

        if (p <= 1e-8 || p >= 1-1e-8) duration = Duration.millis(10);
        else duration = Duration.millis(200 * style.getAnimeRate());

        progressTimeline.stop();
        progressTimeline = new Timeline(new KeyFrame(duration,
                new KeyValue(progressBar.widthProperty(), width),
                new KeyValue(progress, p)
        ));
        progressTimeline.play();
    }

    public SimpleDoubleProperty progressProperty() {
        return progress;
    }

    public void setTheme(AXProgressBarStyle style) {
        this.style = style;
        super.setTheme(style);
    }

    @Override
    public void update() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(200 * style.getAnimeRate()),
                new KeyValue(insets, style.getBorderInsets()),
                new KeyValue(progressBar.strokeWidthProperty(), style.getInnerBorderRadius()),
                new KeyValue(progressBar.arcWidthProperty(), style.getInnerBorderRadius()),
                new KeyValue(progressBar.arcHeightProperty(), style.getInnerBorderRadius()),
                new KeyValue(progressBar.strokeProperty(), style.getInnerBorderColor()),
                new KeyValue(progressBar.fillProperty(), style.getInnerBGColor())
        ));
        timeline.play();


        super.update();
    }
}
