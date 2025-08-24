package mai_onsyn.AnimeFX.Module;

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
import mai_onsyn.AnimeFX.Localizable;
import mai_onsyn.AnimeFX.Styles.DefaultAXButtonStyle;
import mai_onsyn.AnimeFX.Styles.AXButtonStyle;

import java.util.List;
import java.util.Map;

public class AXButton extends AXBase implements Localizable {
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

        //textLabel.setStyle("-fx-background-color: #20ff2020;");

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
        update(200);
    }

    public void update(long millis) {
        textLabel.setFont(style.getTextFont());
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(millis * style.getAnimeRate()),
                new KeyValue(textLabel.textFillProperty(), isHover() ? style.getTextColor() : isPressed ? style.getTextHoverColor() : style.getTextColor())
        ));
        timeline.play();

        super.update(millis);
    }

    public void setText(String text) {
        textLabel.setText(text);
        Platform.runLater(super::flush);
    }

    public void setTheme(AXButtonStyle style) {
        this.style = style;
        super.setTheme(style);
    }

    public String getText() {
        return textLabel.getText();
    }

    public Label getTextLabel() {
        return textLabel;
    }

    public AXButtonStyle style() {
        return style;
    }





    private String langKey = "";

    @Override
    public String getI18NKey() {
        return langKey;
    }

    @Override
    public List<Localizable> getChildrenLocalizable() {
        return List.of();
    }

    @Override
    public void setI18NKey(String key) {
        langKey = key;
    }

    @Override
    public void setChildrenI18NKeys(Map<String, String> keyMap) {

    }

    @Override
    public void localize(String str) {
        setText(str);
    }
}
