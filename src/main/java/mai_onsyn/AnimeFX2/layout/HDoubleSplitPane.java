package mai_onsyn.AnimeFX2.layout;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Cursor;
import javafx.scene.layout.Region;

public class HDoubleSplitPane extends AutoPane {

    private final AutoPane right = new AutoPane();
    private final AutoPane left = new AutoPane();
    private final Region divider = new Region();

    private final SimpleDoubleProperty valueProperty;


    public HDoubleSplitPane(double dividerWidth, double value, double lInsets, double rInsets) {

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
        super.setPosition(leftBox, true, 0, 1 - value, 0, 0);
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
            double deltaX = event.getScreenX() - (double) divider.getUserData();

            divider.setUserData(event.getScreenX());

            double trackStartX = super.localToScreen(super.getBoundsInLocal()).getMinX();
            double trackEndX = super.localToScreen(super.getBoundsInLocal()).getMaxX();
            double trackWidth = super.getWidth();

            if (event.getScreenX() < trackStartX + lInsets) {
                valueProperty.set(lInsets / trackWidth);
            } else if (event.getScreenX() > trackEndX - rInsets) {
                valueProperty.set(1 - rInsets / trackWidth);
            } else {
                double deltaProgress = deltaX / trackWidth;

                double newProgress = Math.max(0, Math.min(valueProperty.get() + deltaProgress, 1));
                valueProperty.set(newProgress);
            }
        });


        valueProperty.addListener((o, ov, nv) -> {
            super.setPosition(leftBox, true, 0, 1 - nv.doubleValue(), 0, 0);
            super.setPosition(rightBox, true, nv.doubleValue(), 0, 0, 0);
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
