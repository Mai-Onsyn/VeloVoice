package mai_onsyn.AnimeFX.Module;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import mai_onsyn.AnimeFX.AutoUpdatable;
import mai_onsyn.AnimeFX.Styles.AXBaseStyle;
import mai_onsyn.AnimeFX.Styles.DefaultAXBaseStyle;
import mai_onsyn.AnimeFX.layout.AutoPane;
import mai_onsyn.AnimeFX.Utls.Toolkit;

public class AXBase extends AutoPane implements AutoUpdatable {
    private AXBaseStyle style = new DefaultAXBaseStyle();

    private final Rectangle border = new Rectangle(100, 50, style.getBGColor());
    private final Rectangle shadow = new Rectangle(100, 50, Toolkit.adjustOpacity(style.getHoverShadow(), 0));

    private Timeline shadowTimeline = new Timeline();
    protected boolean isPressed = false;

    public AXBase() {

        border.setArcWidth(style.getBorderArcSize());
        border.setArcHeight(style.getBorderArcSize());

        border.setStroke(style.getBorderColor());
        border.setStrokeWidth(style.getBorderRadius());

        Rectangle borderClip = new Rectangle();
        borderClip.widthProperty().bindBidirectional(border.widthProperty());
        borderClip.heightProperty().bindBidirectional(border.heightProperty());
        borderClip.arcWidthProperty().bindBidirectional(border.arcWidthProperty());
        borderClip.arcHeightProperty().bindBidirectional(border.arcHeightProperty());
        super.setClip(borderClip);
        shadow.widthProperty().bindBidirectional(border.widthProperty());
        shadow.heightProperty().bindBidirectional(border.heightProperty());
        shadow.arcWidthProperty().bindBidirectional(border.arcWidthProperty());
        shadow.arcHeightProperty().bindBidirectional(border.arcHeightProperty());

        border.widthProperty().bind(super.widthProperty());
        border.heightProperty().bind(super.heightProperty());

        super.addEventHandler(MouseEvent.MOUSE_ENTERED, _ -> {
            fireShadowTimeline(isPressed ? style.getPressedShadow() : style.getHoverShadow());
        });

        super.addEventHandler(MouseEvent.MOUSE_EXITED, _ -> {
            fireShadowTimeline(isPressed ? style.getPressedShadow() : Toolkit.adjustOpacity(style.getHoverShadow(), 0));
        });

        super.addEventHandler(MouseEvent.MOUSE_PRESSED, _ -> {
            isPressed = true;
            fireShadowTimeline(style.getPressedShadow());
        });

        super.addEventHandler(MouseEvent.MOUSE_RELEASED, _ -> {
            isPressed = false;
            fireShadowTimeline(isHover() ? style.getHoverShadow() : Toolkit.adjustOpacity(style.getHoverShadow(), 0));
        });

        super.getChildren().addAll(border, shadow);
    }

    private void fireShadowTimeline(Color target) {
        shadowTimeline.stop();
        shadowTimeline = new Timeline(new KeyFrame(Duration.millis(200 * style.getAnimeRate()),
                new KeyValue(shadow.fillProperty(), target)
        ));
        shadowTimeline.play();
    }

    @Override
    public void update() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(200 * style.getAnimeRate()),
                new KeyValue(border.fillProperty(), style.getBGColor()),
                new KeyValue(border.strokeProperty(), style.getBorderColor()),
                new KeyValue(border.strokeWidthProperty(), style.getBorderRadius()),
                new KeyValue(border.arcWidthProperty(), style.getBorderArcSize()),
                new KeyValue(border.arcHeightProperty(), style.getBorderArcSize()),
                new KeyValue(shadow.fillProperty(), isHover() ? style.getHoverShadow() : isPressed ? style.getPressedShadow() : Toolkit.adjustOpacity(style.getHoverShadow(), 0))
        ));
        timeline.play();

        Platform.runLater(super::flush);
    }

    public void setTheme(AXBaseStyle style) {
        this.style = style;
    }
}
