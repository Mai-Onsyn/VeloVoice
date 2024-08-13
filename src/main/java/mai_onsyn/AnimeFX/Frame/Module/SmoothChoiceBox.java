package mai_onsyn.AnimeFX.Frame.Module;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Popup;
import javafx.util.Duration;
import mai_onsyn.AnimeFX.Frame.Utils.Toolkit;

public class SmoothChoiceBox extends DiffusionButton {

    private final Rectangle popupBorderClip = new Rectangle();
    private double popupMaxHeight = 200;
    private int popupAnimeDuration = 500;
    private Color popupBG = Color.TRANSPARENT;

    public SmoothChoiceBox popupBorderShape(double v1) {
        popupBorderClip.setArcWidth(v1);
        popupBorderClip.setArcHeight(v1);
        return this;
    }
    public SmoothChoiceBox popupAnimeDuration(int popupAnimeDuration) {
        this.popupAnimeDuration = popupAnimeDuration;
        return this;
    }
    public SmoothChoiceBox setPopupBG(Color popupBG) {
        this.popupBG = popupBG;
        return this;
    }
    public SmoothChoiceBox popupMaxHeight(double maxHeight) {
        this.popupMaxHeight = maxHeight;
        scrollPane.setMaxHeight(maxHeight);
        return this;
    }

    public SmoothChoiceBox borderColor(Color color) {
        super.borderColor(color);
        return this;
    }
    public SmoothChoiceBox borderShape(double v1) {
        super.borderShape(v1);
        return this;
    }
    public SmoothChoiceBox font(Font font) {
        super.font(font);
        return this;
    }
    public SmoothChoiceBox width(double width) {
        super.width(width);
        return this;
    }
    public SmoothChoiceBox height(double height) {
        super.height(height);
        return this;
    }
    public SmoothChoiceBox bgColor(Color bgColor) {
        super.bgColor(bgColor);
        return this;
    }
    public SmoothChoiceBox animeDuration(int animeDuration) {
        super.animeDuration(animeDuration);
        return this;
    }
    public SmoothChoiceBox textColor(Color textColor) {
        super.textColor(textColor);
        return this;
    }
    public SmoothChoiceBox ftImageXOffset(double ftImageXOffset) {
        super.ftImageXOffset(ftImageXOffset);
        return this;
    }
    public SmoothChoiceBox ftImageYOffset(double ftImageYOffset) {
        super.ftImageYOffset(ftImageYOffset);
        return this;
    }
    public SmoothChoiceBox textXOffset(double textXOffset) {
        super.textXOffset(textXOffset);
        return this;
    }
    public SmoothChoiceBox textYOffset(double textYOffset) {
        super.textYOffset(textYOffset);
        return this;
    }
    public SmoothChoiceBox align(int align) {
        super.align(align);
        return this;
    }
    public SmoothChoiceBox bgImage(Image bgImage) {
        super.bgImage(bgImage);
        return this;
    }
    public SmoothChoiceBox ftImage(Image ftImage) {
        super.ftImage(ftImage);
        return this;
    }
    public SmoothChoiceBox ftImageRatio(double v) {
        super.ftImageRatio(v);
        return this;
    }
    public SmoothChoiceBox name(String description) {
        super.name(description);
        return this;
    }
    public SmoothChoiceBox borderRadius(double r) {
        super.borderRadius(r);
        return this;
    }
    public SmoothChoiceBox textFocusColor(Color textFocusColor) {
        super.textFocusColor(textFocusColor);
        return this;
    }
    public SmoothChoiceBox bgFocusColor(Color backGroundFocusColor) {
        super.bgFocusColor(backGroundFocusColor);
        return this;
    }
    public SmoothChoiceBox fillColor(Color fillColor) {
        super.fillColor(fillColor);
        return this;
    }
    public SmoothChoiceBox init() {
        super.init();
        return this;
    }


    private final Popup popup = new Popup();
    private final ScrollPane scrollPane = new ScrollPane();
    private final VBox box = new VBox();

    private final double spacing;
    private final SimpleBooleanProperty showProperty = new SimpleBooleanProperty(false);

    private boolean firstToAddListener = true;

    public SmoothChoiceBox(double spacing, Node... items) {
        scrollPane.setClip(popupBorderClip);
        this.spacing = spacing;
        scrollPane.setFitToWidth(true);
        box.setFillWidth(true);
        //box.setStyle("-fx-background-color: #8f008f85");

        if (items.length != 0) {
            box.getChildren().addAll(items);
        }

        scrollPane.setContent(box);
        popup.getContent().add(scrollPane);
        scrollPane.getStylesheets().add("styles/scroll-pane.css");
        scrollPane.setStyle("-fx-background-color: #" + Toolkit.colorToString(popupBG));

        super.setOnMouseClicked(mouseEvent -> {
            if (!popup.isShowing() && mouseEvent.getButton() == MouseButton.PRIMARY) {
                show();
            }
            if (firstToAddListener) {
                firstToAddListener = false;
                getScene().getWindow().focusedProperty().addListener((o, ov, nv) -> {
                    if (!nv) {
                        hide();
                    }
                });

                getScene().getWindow().xProperty().addListener((o, ov, nv) -> {
                    if (popup.isShowing()) {
                        hide();
                    }
                });

                getScene().getWindow().yProperty().addListener((o, ov, nv) -> {
                    if (popup.isShowing()) {
                        hide();
                    }
                });

                getScene().getWindow().widthProperty().addListener((o, ov, nv) -> {
                    if (popup.isShowing()) {
                        hide();
                    }
                });

                getScene().getWindow().heightProperty().addListener((o, ov, nv) -> {
                    if (popup.isShowing()) {
                        hide();
                    }
                });
                getScene().addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
                    Bounds popupBounds = popup.getContent().getFirst().localToScreen(popup.getContent().getFirst().getBoundsInLocal());
                    double x = event.getScreenX();
                    double y = event.getScreenY();

                    if (x < popupBounds.getMinX() || x > popupBounds.getMaxX() || y < popupBounds.getMinY() || y > popupBounds.getMaxY()) {
                        hide();
                    }
                });
            }
        });
        scrollPane.setMaxHeight(popupMaxHeight);
        scrollPane.setUserData(showProperty);
        Toolkit.addSmoothScrolling(scrollPane);

        super.widthProperty().addListener((o, ov, nv) -> {
            double width = nv.doubleValue();
            scrollPane.setPrefWidth(width);
        });
        scrollPane.widthProperty().addListener((o, ov, nv) -> {
            double width = nv.doubleValue();
            popupBorderClip.setWidth(width);
        });
        scrollPane.heightProperty().addListener((o, ov, nv) -> {
            double height = nv.doubleValue();
            popupBorderClip.setHeight(height);
        });

        box.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) hide();
        });
    }

    public void addItem(Node... items) {
        if (items.length != 0) {
            box.getChildren().addAll(items);
        }
    }

    private void hide() {
        if (popup.isShowing()) {
            Timeline spacingTrans = new Timeline(new KeyFrame(Duration.millis(popupAnimeDuration), new KeyValue(box.spacingProperty(), -spacing)));
            Timeline layoutTrans = new Timeline(new KeyFrame(Duration.millis(popupAnimeDuration), new KeyValue(box.layoutYProperty(), -spacing)));
            Timeline opacityTrans = new Timeline(new KeyFrame(Duration.millis(popupAnimeDuration), new KeyValue(box.opacityProperty(), 0)));
            spacingTrans.play();
            opacityTrans.play();
            layoutTrans.play();
            spacingTrans.setOnFinished(f -> popup.hide());
        }
    }

    private void show() {
        box.setSpacing(-this.spacing);
        box.setOpacity(0);
        Timeline spacingTrans = new Timeline(new KeyFrame(Duration.millis(popupAnimeDuration), new KeyValue(box.spacingProperty(), 0)));
        Timeline layoutTrans = new Timeline(new KeyFrame(Duration.millis(popupAnimeDuration), new KeyValue(box.layoutYProperty(), 0)));
        Timeline opacityTrans = new Timeline(new KeyFrame(Duration.millis(popupAnimeDuration), new KeyValue(box.opacityProperty(), 1)));
        spacingTrans.play();
        opacityTrans.play();
        layoutTrans.play();
        spacingTrans.setOnFinished(f -> {
            showProperty.setValue(true);
            showProperty.setValue(false);
        });
        Point2D position = Toolkit.getInScreenPosition(this);
        popup.show(getScene().getWindow(), position.getX(), position.getY() + super.getPrefHeight());
    }

}
