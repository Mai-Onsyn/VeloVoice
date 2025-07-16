package mai_onsyn.AnimeFX.Module;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Duration;
import mai_onsyn.AnimeFX.Localizable;
import mai_onsyn.AnimeFX.Styles.AXChoiceBoxStyle;
import mai_onsyn.AnimeFX.Styles.DefaultAXChoiceBoxStyle;
import mai_onsyn.AnimeFX.Utls.AXButtonGroup;
import mai_onsyn.AnimeFX.Utls.Toolkit;
import mai_onsyn.AnimeFX.layout.AXContextPane;

import java.util.*;

public class AXChoiceBox extends AXButton {

    private AXChoiceBoxStyle style = new DefaultAXChoiceBoxStyle();
    private final AXContextPane itemPane = new AXContextPane();
    private final ImageView signalView = new ImageView(style.getSignalImage());
    private Timeline rotateTimeline = new Timeline();
    private final AXButtonGroup buttonGroup = new AXButtonGroup();


    public AXChoiceBox() {
        super.setTheme(style);
        itemPane.setTheme(style.getContextPaneStyle());
        buttonGroup.setFreeStyle(style.getFreeButtonStyle());
        buttonGroup.setSelectedStyle(style.getSelectedButtonStyle());

        super.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                Point2D inScreenPosition = getInScreenPosition(this);
                itemPane.show(super.getScene().getWindow(), inScreenPosition.getX(), inScreenPosition.getY() + super.getLayoutBounds().getHeight());
            }
        });
;
        itemPane.setOnShown(event -> {
            rotateTimeline.stop();
            rotateTimeline = new Timeline(new KeyFrame(Duration.millis(200 * style.getAnimeRate()),
                    new KeyValue(signalView.rotateProperty(), 180, Toolkit.SHARP_OUT)
            ));
            rotateTimeline.play();
        });
        itemPane.setOnHidden(event -> {
            rotateTimeline.stop();
            rotateTimeline = new Timeline(new KeyFrame(Duration.millis(200 * style.getAnimeRate()),
                    new KeyValue(signalView.rotateProperty(), 0, Toolkit.SHARP_OUT)
            ));
            rotateTimeline.play();
        });

        signalView.setFitWidth(style.getSignalScale());
        signalView.setFitHeight(style.getSignalScale());

        super.heightProperty().addListener((o, ov, nv) -> {
            super.setPosition(signalView, style.getSignalRelate() == AXChoiceBoxStyle.RelativePosition.LEFT ? AlignmentMode.LEFT_CENTER : AlignmentMode.RIGHT_CENTER, LocateMode.ABSOLUTE, style.getSignalRelate() == AXChoiceBoxStyle.RelativePosition.LEFT ? style.getSignalRelateX() : -style.getSignalRelateX(), nv.doubleValue() / 2);
        });

        itemPane.getRoot().maxWidthProperty().bind(super.widthProperty());
        itemPane.getRoot().minWidthProperty().bind(super.widthProperty());

        super.getChildren().add(signalView);
    }

    public AXButton createItem() {
        AXButton item = itemPane.createItem();
        //buttonGroup.setFreeStyle(item.style());
        buttonGroup.register(item);
        return item;
    }

    public void setTheme(AXChoiceBoxStyle style) {
        this.style = style;
        super.setTheme(style);
        itemPane.setTheme(style.getContextPaneStyle());
        buttonGroup.setFreeStyle(style.getFreeButtonStyle());
        buttonGroup.setSelectedStyle(style.getSelectedButtonStyle());
    }

    public void removeItem(AXButton item) {
        itemPane.removeItem(item);
    }

    public void showItem(AXButton item) {
        itemPane.showItem(item);
    }

    public boolean containsItem(AXButton item) {
        return itemPane.containsItem(item);
    }

    @Override
    public void update() {
        signalView.setImage(style.getSignalImage());
        super.setPosition(signalView, style.getSignalRelate() == AXChoiceBoxStyle.RelativePosition.LEFT ? AlignmentMode.LEFT_CENTER : AlignmentMode.RIGHT_CENTER, LocateMode.ABSOLUTE, style.getSignalRelate() == AXChoiceBoxStyle.RelativePosition.LEFT ? style.getSignalRelateX() : -style.getSignalRelateX(), super.getLayoutBounds().getHeight() / 2);

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(200 * style.getAnimeRate()),
                new KeyValue(signalView.fitWidthProperty(), style.getSignalScale()),
                new KeyValue(signalView.fitHeightProperty(), style.getSignalScale())
        ));
        timeline.play();

        super.update();
        itemPane.update();
        buttonGroup.update();
    }

    public AXButtonGroup getButtonGroup() {
        return buttonGroup;
    }

    private static Point2D getInScreenPosition(Node node) {
        if (node == null || node.getScene() == null) {
            return null;
        }

        Bounds boundsInScene = node.localToScene(node.getBoundsInLocal());
        double sceneX = boundsInScene.getMinX();
        double sceneY = boundsInScene.getMinY();

        Stage stage = (Stage) node.getScene().getWindow();
        double stageX = stage.getX();
        double stageY = stage.getY();

        double sceneXInStage = node.getScene().getX();
        double sceneYInStage = node.getScene().getY();

        double windowDecorationHeight = stageY - stage.getScene().getWindow().getY();

        double screenX = stageX + sceneX + sceneXInStage;
        double screenY = stageY + sceneY + sceneYInStage + windowDecorationHeight;

        return new Point2D(screenX, screenY);
    }

    public void flushChosenButton() {
        super.setText(buttonGroup.getSelectedButton().getText());
    }



    private final List<Localizable> registeredChildren = new ArrayList<>();

    @Override
    public List<Localizable> getChildrenLocalizable() {
        return registeredChildren;
    }

    @Override
    public void setChildrenI18NKeys(Map<String, String> keyMap) {
        buttonGroup.getButtonList().forEach(b -> {
            if (b.getUserData() instanceof String buttonKey) {

                keyMap.forEach((k, v) -> {
                    if (k.equals(buttonKey)) {
                        b.setI18NKey(v);
                        registeredChildren.add(b);
                    }
                });

            }
        });
    }

    @Override
    public void localize(String str) {
        Platform.runLater(() -> super.setText(buttonGroup.getSelectedButton().getText()));
    }
}
