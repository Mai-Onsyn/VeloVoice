package mai_onsyn.AnimeFX2.layout;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Cursor;
import javafx.scene.layout.Region;
import mai_onsyn.AnimeFX.Frame.Layout.AutoPane;

public class HDoubleSplitPane extends AutoPane {

    private final AutoPane right = new AutoPane();
    private final AutoPane left = new AutoPane();
    private final Region divider = new Region();

    private final SimpleDoubleProperty valueProperty;


    public HDoubleSplitPane(double dividerWidth, double value) {

        if (value < 0.0 || value > 1.0) {
            throw new IllegalArgumentException("value must be between 0.0 and 1.0");
        }
        valueProperty = new SimpleDoubleProperty(value);

        AutoPane leftBox = new AutoPane();
        AutoPane rightBox = new AutoPane();

        leftBox.getChildren().add(left);
        rightBox.getChildren().add(right);

        leftBox.setPosition(left, true, false, true, true, 0, dividerWidth / 2, 0, 0);
        rightBox.setPosition(right, false, true, true, true, dividerWidth / 2, 0, 0, 0);

        super.getChildren().addAll(leftBox, rightBox, divider);
        super.setPosition(leftBox, true, 0, value, 0, 0);
        super.setPosition(rightBox, true, value, 0, 0, 0);

        divider.setStyle("-fx-background-color: #0020ff40;");

        divider.setPrefWidth(dividerWidth);
        divider.prefHeightProperty().bind(super.heightProperty());
        divider.layoutXProperty().bind(super.widthProperty().multiply(valueProperty).subtract(dividerWidth / 2));

        divider.setCursor(Cursor.H_RESIZE);


        divider.setOnMousePressed(event -> {
            divider.setUserData(event.getScreenX());
        });
        divider.setOnMouseDragged(event -> {

            // 获取鼠标当前的screenX坐标和上次记录的screenX差值
            double deltaX = event.getScreenX() - (double) divider.getUserData();

            // 更新当前鼠标位置为上次的起点
            divider.setUserData(event.getScreenX());

            // 获取滑动条的左边界和右边界的 screenX 坐标
            double trackStartX = super.localToScreen(super.getBoundsInLocal()).getMinX();
            double trackEndX = super.localToScreen(super.getBoundsInLocal()).getMaxX();
            double trackWidth = super.getWidth();

            if (event.getScreenX() < trackStartX) {
                // 当鼠标在滑动条左边界之外时，将进度设为 0
                valueProperty.set(0);
            } else if (event.getScreenX() > trackEndX) {
                // 当鼠标在滑动条右边界之外时，将进度设为 1
                valueProperty.set(1);
            } else {
                // 如果鼠标在滑动条范围内，计算相对滑动距离并更新进度
                double deltaProgress = deltaX / trackWidth;

                // 更新 progressProperty，并限制在 [0, 1] 范围内
                double newProgress = Math.max(0, Math.min(valueProperty.get() + deltaProgress, 1));
                valueProperty.set(newProgress);
            }
        });
    }


    public AutoPane getRight() {
        return right;
    }

    public AutoPane getLeft() {
        return left;
    }

    public SimpleDoubleProperty valueProperty() {
        return valueProperty;
    }
}
