package mai_onsyn.AnimeFX2.Module;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import mai_onsyn.AnimeFX2.LanguageSwitchable;
import mai_onsyn.AnimeFX2.Styles.DefaultAXButtonStyle;
import mai_onsyn.AnimeFX2.Styles.AXButtonStyle;

import java.util.Map;

public class AXButton extends AXBase implements LanguageSwitchable {
    private AXButtonStyle style = new DefaultAXButtonStyle();

    private final Label textLabel = new Label();

    private Timeline textTimeline = new Timeline();

    public AXButton(String text) {
        init(text);
    }

    public AXButton() {
        init("");
    }

    private void init(String text) {
        super.setTheme(style);
        textLabel.setText(text);

        textLabel.setTextFill(style.getTextColor());
        textLabel.setFont(style.getTextFont());

        super.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
            setCursor(Cursor.HAND);
            textTimeline.stop();
            textTimeline = new Timeline(new KeyFrame(Duration.millis(200 * style.getAnimeRate() ),
                    new KeyValue(textLabel.textFillProperty(), style.getTextHoverColor())
            ));
            textTimeline.play();
        });

        super.addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
            textTimeline.stop();
            textTimeline = new Timeline(new KeyFrame(Duration.millis(200 * style.getAnimeRate() ),
                    new KeyValue(textLabel.textFillProperty(), isPressed ? style.getTextHoverColor() : style.getTextColor())
            ));
            textTimeline.play();
        });

        super.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                Circle circle = new Circle(0, style.getFillColor());

                Point2D layout = super.localToScene(0, 0);
                Bounds layoutBounds = super.getLayoutBounds();

                circle.setCenterX(event.getSceneX() - layout.getX());
                circle.setCenterY(event.getSceneY() - layout.getY());
                super.getChildren().add(1, circle);
                Timeline bloom = new Timeline(new KeyFrame(Duration.millis(200 * style.getAnimeRate()), new KeyValue(circle.radiusProperty(), 1.414 * Math.max(layoutBounds.getWidth(), layoutBounds.getHeight()))));
                bloom.play();
                bloom.setOnFinished(_ -> {
                    Timeline fade = new Timeline(new KeyFrame(Duration.millis(200 * style.getAnimeRate()), new KeyValue(circle.opacityProperty(), 0)));
                    fade.setOnFinished(_ -> super.getChildren().remove(circle));
//                    Thread.ofVirtual().start(() -> {
//                        while (isPressed) {
//                            try {
//                                Thread.sleep(10);
//                            } catch (InterruptedException e) {
//                                throw new RuntimeException(e);
//                            }
//                        }
//
//                    });
                    fade.play();
                });
            }
        });

        super.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            textTimeline.stop();
            textTimeline = new Timeline(new KeyFrame(Duration.millis(200 * style.getAnimeRate() ),
                    new KeyValue(textLabel.textFillProperty(), isHover() ? style.getTextHoverColor() : style.getTextColor())
            ));
            textTimeline.play();
        });



        textLabel.setAlignment(Pos.CENTER);
        super.getChildren().add(textLabel);
        super.setPosition(textLabel, false, 0, 0, 0, 0);
    }

    @Override
    public void update() {
        textLabel.setFont(style.getTextFont());
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(200 * style.getAnimeRate()),
                new KeyValue(textLabel.textFillProperty(), isHover() ? style.getTextColor() : isPressed ? style.getTextHoverColor() : style.getTextColor())
        ));
        timeline.play();

        super.update();
    }

    public void setText(String text) {
        textLabel.setText(text);
        Platform.runLater(super::flushSize);
    }

    public void setTheme(AXButtonStyle style) {
        this.style = style;
        super.setTheme(style);
    }

    @Override
    public void switchLanguage(String str) {
        setText(str);
    }

    @Override
    public Map<String, LanguageSwitchable> getLanguageElements() {
        return null;
    }

    public Label getTextLabel() {
        return textLabel;
    }

    public AXButtonStyle style() {
        return style;
    }
}
