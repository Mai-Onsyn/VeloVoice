package mai_onsyn.AnimeFX2.layout;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Cursor;
import javafx.scene.layout.Region;

public class VDoubleSplitPane extends AutoPane {

    private final AutoPane top = new AutoPane();
    private final AutoPane bottom = new AutoPane();
    private final Region divider = new Region();

    private final SimpleDoubleProperty valueProperty;

    public VDoubleSplitPane(double dividerHeight, double value, double tInsets, double bInsets) {

        if (value < 0.0 || value > 1.0) {
            throw new IllegalArgumentException("value must be between 0.0 and 1.0");
        }
        valueProperty = new SimpleDoubleProperty(value);

        AutoPane topBox = new AutoPane();
        AutoPane bottomBox = new AutoPane();

        topBox.getChildren().add(top);
        bottomBox.getChildren().add(bottom);

        topBox.setPosition(top, true, true, true, false, 0, 0, 0, dividerHeight / 2);
        bottomBox.setPosition(bottom, true, true, false, true, 0, 0, dividerHeight / 2, 0);

        super.getChildren().addAll(topBox, bottomBox, divider);
        super.setPosition(topBox, true, 0, 0, 0, 1 - value);
        super.setPosition(bottomBox, true, 0, 0, value, 0);

        //divider.setStyle("-fx-background-color: #0020ff40;");

        divider.setPrefHeight(dividerHeight);
        divider.prefWidthProperty().bind(super.widthProperty());
        divider.layoutYProperty().bind(super.heightProperty().multiply(valueProperty).subtract(dividerHeight / 2));

        divider.setCursor(Cursor.V_RESIZE);

        divider.setOnMousePressed(event -> {
            divider.setUserData(event.getScreenY());
        });
        divider.setOnMouseDragged(event -> {
            double deltaY = event.getScreenY() - (double) divider.getUserData();

            divider.setUserData(event.getScreenY());

            double trackStartY = super.localToScreen(super.getBoundsInLocal()).getMinY();
            double trackEndY = super.localToScreen(super.getBoundsInLocal()).getMaxY();
            double trackHeight = super.getHeight();

            if (event.getScreenY() < trackStartY + tInsets) {
                valueProperty.set(tInsets / trackHeight);
            } else if (event.getScreenY() > trackEndY - bInsets) {
                valueProperty.set(1 - bInsets / trackHeight);
            } else {
                double deltaProgress = deltaY / trackHeight;

                double newProgress = Math.max(0, Math.min(valueProperty.get() + deltaProgress, 1));
                valueProperty.set(newProgress);
            }
        });

        valueProperty.addListener((o, ov, nv) -> {
            super.setPosition(topBox, true, 0, 0, 0, 1 - nv.doubleValue());
            super.setPosition(bottomBox, true, 0, 0, nv.doubleValue(), 0);
        });
    }

    public AutoPane getTop() {
        return top;
    }

    public AutoPane getBottom() {
        return bottom;
    }

    public SimpleDoubleProperty valueProperty() {
        return valueProperty;
    }
}
