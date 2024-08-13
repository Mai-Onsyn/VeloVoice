package mai_onsyn.AnimeFX.Frame.Layout;

import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Popup;
import javafx.stage.Stage;
import mai_onsyn.AnimeFX.Frame.Module.SmoothTextField;
import mai_onsyn.AnimeFX.Frame.Styles.DefaultNamePopupStyle;
import mai_onsyn.AnimeFX.Frame.Styles.NamePopupStyle;

public class NamePopup extends Popup {
    private static Stage stage;
    public static void setStage(Stage s) {
        stage = s;
    }

    private final Rectangle borderClip;

    private NamePopupStyle popupStyle = new DefaultNamePopupStyle();
    private String description;
    private double width, height;

    public NamePopup borderShape(double v1) {
        borderClip.setArcWidth(v1);
        borderClip.setArcHeight(v1);
        return this;
    }
    public NamePopup popupStyle(NamePopupStyle popupStyle) {
        this.popupStyle = popupStyle;
        return this;
    }

    public NamePopup init() {

        SmoothTextField textField = popupStyle.createTextField();

        Label label = popupStyle.createDescription(description);

        textField.setMaxWidth(width * 0.8);
        // 设置垂直居中排列
        VBox box = new VBox();
        box.setAlignment(Pos.CENTER);
        box.setSpacing(10);
        box.getChildren().addAll(label, textField);

        // 设置背景面板
        Rectangle background = popupStyle.createBackground(width, height);
        //background.setFill(Color.LIGHTGRAY);

        StackPane stackPane = new StackPane();
        stackPane.setPrefSize(width, height);
        stackPane.getChildren().addAll(background, box);
        stackPane.setClip(borderClip);
        stackPane.setOnMouseClicked(_ -> stackPane.requestFocus());

        // 设置文本框的事件监听器，当按下Enter时关闭弹窗
        textField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER && !textField.getTextField().getText().isEmpty()) {
                if (onTextAvailable != null) {
                    onTextAvailable.changed(null, null, textField.getTextField().getText());
                }
                this.hide();
            }
        });

        // 将StackPane添加到Popup中
        getContent().add(stackPane);

        stage.focusedProperty().addListener((o, ov, nv) -> {
            if (!nv) {
                super.hide();
            }
        });

        stage.xProperty().addListener((o, ov, nv) -> {
            if (super.isShowing()) {
                super.hide();
            }
        });

        stage.yProperty().addListener((o, ov, nv) -> {
            if (super.isShowing()) {
                super.hide();
            }
        });

        stage.getScene().addEventFilter(javafx.scene.input.MouseEvent.MOUSE_CLICKED, e -> {
            if (!super.getContent().getFirst().getBoundsInLocal().contains(e.getSceneX(), e.getSceneY())) {
                super.hide(); // 点击在Popup之外，隐藏Popup
            }
        });

        return this;
    }

    private ChangeListener<String> onTextAvailable;

    public NamePopup(String description, double width, double height) {
        this.width = width;
        this.height = height;
        this.description = description;
        borderClip = new Rectangle(width, height);
    }

    public void showOnCenter() {
        if (stage != null) {
            double centerX = stage.getX() + stage.getWidth() / 2;
            double centerY = stage.getY() + stage.getHeight() / 2;
            // 将Popup的左上角移到计算的中心位置，因此需要减去Popup的一半宽高
            show(stage, centerX - width / 2, centerY - height / 2);
        }
    }

    public void setOnTextAvailable(ChangeListener<String> listener) {
        this.onTextAvailable = listener;
    }

}
