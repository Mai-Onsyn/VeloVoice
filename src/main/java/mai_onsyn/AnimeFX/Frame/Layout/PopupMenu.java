package mai_onsyn.AnimeFX.Frame.Layout;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class PopupMenu extends Popup {

    private final Rectangle borderClip = new Rectangle();
    public PopupMenu borderShape(double v1) {
        borderClip.setArcWidth(v1);
        borderClip.setArcHeight(v1);
        return this;
    }

    private boolean firstToAddListener = true;

    private final Pane pane = new Pane();

    public PopupMenu(double width, HBox... items) {
        pane.getChildren().addAll(items);
        pane.setClip(borderClip);
        super.getContent().add(pane);
        super.setWidth(width);

        pane.widthProperty().addListener((observableValue, oldV, newV) -> {
            borderClip.setWidth(newV.doubleValue());
        });

        pane.heightProperty().addListener((o, ov, nv) -> borderClip.setHeight(nv.doubleValue()));

        pane.setOnMouseEntered(event -> pane.setCursor(Cursor.HAND));
        pane.setOnMouseExited(event -> pane.setCursor(Cursor.DEFAULT));
    }

    public void addItem(HBox... hBoxes) {
        pane.getChildren().addAll(hBoxes);
    }

    public void bind(Node node) {
        node.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                // 获取点击位置
                double x = event.getScreenX();
                double y = event.getScreenY();

                double startX = super.getX();
                double startY = super.getY();
                // 显示 Popup
                if (super.isShowing()) {
                    Timeline timeline = new Timeline();
                    for (int i = 0; i < 6; i++) {
                        double progress = (double) i / (6 - 1);
                        KeyFrame keyFrame = new KeyFrame(Duration.millis(1000.0 / 60 * i), e -> {
                            double newX = startX + (x - 10 - startX) * progress;
                            double newY = startY + (y - 10 - startY) * progress;
                            //System.out.println("<" + newX + ", " + newY + ">, <" + x + ", " + y + ">, <" + startX + ", " + startY + ">");
                            super.setX(newX);
                            super.setY(newY);
                        });

                        timeline.getKeyFrames().add(keyFrame);
                    }

                    timeline.setDelay(Duration.millis(20));
                    timeline.play();
                    timeline.setOnFinished(_ -> showWithAnime(node.getScene().getWindow(), x - 10, y - 10));
                }
                else showWithAnime(node.getScene().getWindow(), x - 10, y - 10);
            }

            if (firstToAddListener) {
                firstToAddListener = false;
                node.getScene().getWindow().focusedProperty().addListener((o, ov, nv) -> {
                    if (!nv) {
                        hideWithAnime();
                    }
                });

                node.getScene().getWindow().xProperty().addListener((o, ov, nv) -> {
                    if (super.isShowing()) {
                        hideWithAnime();
                    }
                });

                node.getScene().getWindow().yProperty().addListener((o, ov, nv) -> {
                    if (super.isShowing()) {
                        hideWithAnime();
                    }
                });

                node.getScene().getWindow().widthProperty().addListener((o, ov, nv) -> {
                    if (super.isShowing()) {
                        hideWithAnime();
                    }
                });

                node.getScene().getWindow().heightProperty().addListener((o, ov, nv) -> {
                    if (super.isShowing()) {
                        hideWithAnime();
                    }
                });

                node.getScene().getWindow().getScene().addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
                    if (!super.getContent().getFirst().getBoundsInLocal().contains(e.getSceneX(), e.getSceneY())) {
                        //if (e.getButton() == MouseButton.PRIMARY) hideWithAnime();
//                        for (PopupMenu popupMenu : showingPopup) {
//                            if (popupMenu != this) {
//                                hideWithAnime();
//                            }
//                        }

                        if (super.isShowing()) hideWithAnime();
                    }
                });
            }
        });
    }

    //private static final List<PopupMenu> showingPopup = new CopyOnWriteArrayList<>();

    private void showWithAnime(Window window, double anchorX, double anchorY) {
        //showingPopup.add(this);
        super.show(window, anchorX, anchorY);

        double height = 0;
        for (Node e : ((Pane) super.getContent().getFirst()).getChildren()) {
            if (!(e instanceof HBox hBox)) continue;
            double h = hBox.getLayoutBounds().getHeight();
            Timeline positionTrans = new Timeline(new KeyFrame(Duration.millis(100), new KeyValue(hBox.layoutYProperty(), height)));
            positionTrans.play();
            Timeline opacityTrans = new Timeline(new KeyFrame(Duration.millis(100), new KeyValue(hBox.opacityProperty(), 1)));
            opacityTrans.play();
            height += h;
        }
    }

    private void hideWithAnime() {
        //showingPopup.remove(this);
        for (Node e : ((Pane) super.getContent().getFirst()).getChildren()) {
            if (!(e instanceof HBox hBox)) continue;
            double h = hBox.getLayoutBounds().getHeight();
            Timeline positionTrans = new Timeline(new KeyFrame(Duration.millis(100), new KeyValue(hBox.layoutYProperty(), -h)));
            positionTrans.play();
            Timeline opacityTrans = new Timeline(new KeyFrame(Duration.millis(100), new KeyValue(hBox.opacityProperty(), 0)));
            opacityTrans.play();
            positionTrans.setOnFinished(_ -> super.hide());
        }
    }

}
