package mai_onsyn.AnimeFX.Module;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import mai_onsyn.AnimeFX.Styles.AXProgressBarStyle;
import mai_onsyn.AnimeFX.Styles.DefaultAXProgressBarStyle;

public class AXProgressBar extends AXBase {

    private AXProgressBarStyle style = new DefaultAXProgressBarStyle();
    private final Rectangle progressBar = new Rectangle();

    private final SimpleDoubleProperty progress = new SimpleDoubleProperty(0);

    private final SimpleDoubleProperty insets = new SimpleDoubleProperty(0);
    private Timeline progressTimeline = new Timeline();
    private boolean setLocker = false;

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

        progress.addListener((o, ov, nv) -> {
            if (!setLocker) {
                setLocker = true;
                this.setProgress(nv.doubleValue());
                setLocker = false;
            }
        });

        super.widthProperty().addListener((o, ov, nv) -> Platform.runLater(() -> {
            if (!setLocker) {
                setLocker = true;
                this.setProgress(progress.get(), 1);
                setLocker = false;
            }
        }));

        super.getChildren().add(progressBar);
    }


    public void setProgress(double p) {
        setProgress(p, 200 * style.getAnimeRate());
    }

    public void setProgress(double p, double duration) {
        p = Math.max(0, Math.min(1, p));
        double width = (super.getLayoutBounds().getWidth() - insets.get() * 2) * p;

        progressTimeline.stop();
        progressTimeline = new Timeline(new KeyFrame(Duration.millis(duration),
                new KeyValue(progressBar.widthProperty(), width)//,
                //new KeyValue(progress, p)
        ));
        progressTimeline.play();
        setLocker = true;
        progress.set(p);
        setLocker = false;
    }

    public SimpleDoubleProperty progressProperty() {
        return progress;
    }

    public double getProgress() {
        return progress.get();
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
