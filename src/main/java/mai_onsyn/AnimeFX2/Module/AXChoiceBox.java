package mai_onsyn.AnimeFX2.Module;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;
import mai_onsyn.AnimeFX2.LanguageSwitchable;
import mai_onsyn.AnimeFX2.Styles.AXChoiceBoxStyle;
import mai_onsyn.AnimeFX2.Styles.DefaultAXChoiceBoxStyle;
import mai_onsyn.AnimeFX2.Utls.AXButtonGroup;
import mai_onsyn.AnimeFX2.layout.AXContextPane;

import java.util.HashMap;
import java.util.Map;

public class AXChoiceBox extends AXButton {

    private AXChoiceBoxStyle style = new DefaultAXChoiceBoxStyle();
    private final AXContextPane itemPane = new AXContextPane();
    private final ImageView signalView = new ImageView(style.getSignalImage());
    private Timeline rotateTimeline = new Timeline();
    private final AXButtonGroup buttonGroup = new AXButtonGroup();


    public AXChoiceBox() {
        super.setTheme(style);

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
                    new KeyValue(signalView.rotateProperty(), 180)
            ));
            rotateTimeline.play();
        });
        itemPane.setOnHidden(event -> {
            rotateTimeline.stop();
            rotateTimeline = new Timeline(new KeyFrame(Duration.millis(200 * style.getAnimeRate()),
                    new KeyValue(signalView.rotateProperty(), 0)
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
        buttonGroup.setFreeStyle(item.style());
        buttonGroup.register(item);
        return item;
    }


    public void setTheme(AXChoiceBoxStyle style) {
        this.style = style;
        super.setTheme(style);
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
    }


    @Override
    public void switchLanguage(String str) {
        super.switchLanguage(str);
    }

    @Override
    public Map<String, LanguageSwitchable> getLanguageElements() {
        return itemLang;
    }

    private Map<String, LanguageSwitchable> itemLang = new HashMap<>();

    public void registerItemLang(String nameSpace, LanguageSwitchable element) {
        itemLang.put(nameSpace, element);
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
}
