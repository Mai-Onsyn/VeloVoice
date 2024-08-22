package mai_onsyn.AnimeFX.Frame.Utils;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.effect.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

public class Toolkit {

    public static String colorToString(Color color) {
        return color.toString().substring(2);
    }

    public static void addBackGroundImage(Pane pane, Image image) {
        ImageView bgImage = new ImageView(image);
        bgImage.setPreserveRatio(true);

        if (!pane.getChildren().isEmpty() && pane.getChildren().getFirst() instanceof ImageView) pane.getChildren().set(0, bgImage);
        else pane.getChildren().addFirst(bgImage);
        resizeImageView(pane, bgImage);
        pane.widthProperty().addListener((o, ov, nv) -> resizeImageView(pane, bgImage));
        pane.heightProperty().addListener((o, ov, nv) -> resizeImageView(pane, bgImage));
    }

    public static void setBackGroundImageEffect(Pane pane, Effect effect) {
        if (pane.getChildren().getFirst() instanceof ImageView bgImage) {
            bgImage.setEffect(effect);
        }
    }

    public static Effect getBackGroundImageEffect(Pane pane) {
        if (pane.getChildren().getFirst() instanceof ImageView bgImage) {
            return bgImage.getEffect();
        } else return null;
    }

    public static Image loadImage(String uri) throws IOException {

        URL resource = Toolkit.class.getResource(uri.startsWith("/") ? uri : "/" + uri);
        if (resource != null) {
            return new Image(resource.toString());
        }

        try {
            return new Image(uri);
        } catch (IllegalArgumentException _) {}

        try {
            return new Image(new FileInputStream(uri));
        } catch (FileNotFoundException e) {
            throw new IOException(e);
        }
    }

    private static void resizeImageView(Pane pane, ImageView imageView) {
        double paneWidth = pane.getWidth();
        double paneHeight = pane.getHeight();
        double imageAspectRatio = imageView.getImage().getWidth() / imageView.getImage().getHeight();
        double paneAspectRatio = paneWidth / paneHeight;

        if (paneAspectRatio > imageAspectRatio) {
            // Pane is wider than image aspect ratio
            imageView.setFitWidth(paneWidth);
            imageView.setFitHeight(paneWidth / imageAspectRatio);
        } else {
            // Pane is taller than image aspect ratio
            imageView.setFitHeight(paneHeight);
            imageView.setFitWidth(paneHeight * imageAspectRatio);
        }

        // Center the ImageView
        imageView.setLayoutX((paneWidth - imageView.getFitWidth()) / 2);
        imageView.setLayoutY((paneHeight - imageView.getFitHeight()) / 2);
    }

    public static void addSmoothScrolling(ScrollPane scrollPane) {
        final double[] scroll = new double[2];
        final Timeline[] anime = new Timeline[2];
        scrollPane.addEventFilter(ScrollEvent.SCROLL, event -> {
            if (event.isShiftDown()) {
                event.consume();
                double width = scrollPane.getContent().getBoundsInLocal().getWidth() - scrollPane.getViewportBounds().getWidth();
                scroll[0] -= event.getDeltaX() * 2 / width;
                scroll[0] = Math.min(scroll[0], 1);
                scroll[0] = Math.max(scroll[0], 0);

                if (anime[0] != null) anime[0].stop();
                anime[0] = new Timeline(new KeyFrame(Duration.millis(200), new KeyValue(scrollPane.hvalueProperty(), scroll[0])));
                anime[0].play();
            } else {
                event.consume();
                double height = scrollPane.getContent().getBoundsInLocal().getHeight() - scrollPane.getViewportBounds().getHeight();
                scroll[1] -= event.getDeltaY() * 2 / height;
                scroll[1] = Math.min(scroll[1], 1);
                scroll[1] = Math.max(scroll[1], 0);

                if (anime[1] != null) anime[1].stop();
                anime[1] = new Timeline(new KeyFrame(Duration.millis(200), new KeyValue(scrollPane.vvalueProperty(), scroll[1])));
                anime[1].play();
            }
        });
        boolean[] isDragging = new boolean[1];
        scrollPane.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> isDragging[0] = true);
        scrollPane.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> isDragging[0] = false);

        scrollPane.vvalueProperty().addListener((o, ov, nv) -> {
            if (isDragging[0]) {
                scroll[1] = nv.doubleValue();
            }
        });
        scrollPane.hvalueProperty().addListener((o, ov, nv) -> {
            if (isDragging[0]) {
                scroll[0] = nv.doubleValue();
            }
        });

        if (scrollPane.getUserData() instanceof SimpleBooleanProperty property) {
            property.addListener((o, ov, nv) -> {
                if (nv) {
                    scroll[1] = scrollPane.getVvalue();
                    scroll[0] = scrollPane.getHvalue();
                }
            });
        }
    }

    public static double textFieldSizeToHeight(TextField field) {
        return field.getFont().getSize() * 1.9310698825 + 1.2242836529;
    }

    public static Point2D getInScreenPosition(Node node) {
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

    public static Color adjustHue(Color color, double h) {
        return Color.hsb(h, color.getSaturation(), color.getBrightness(), color.getOpacity());
    }

    public static Color adjustSaturation(Color color, double s) {
        s = Math.max(0, Math.min(1, s));
        return Color.hsb(color.getHue(), s, color.getBrightness(), color.getOpacity());
    }

    public static Color adjustBrightness(Color color, double b) {
        b = Math.max(0, Math.min(1, b));
        return Color.hsb(color.getHue(), color.getSaturation(), b, color.getOpacity());
    }

    public static Color adjustHue(Color color, double h, double opacity) {
        opacity = Math.max(0, Math.min(1, opacity));
        return Color.hsb(h, color.getSaturation(), color.getBrightness(), opacity);
    }

    public static Color adjustSaturation(Color color, double s, double opacity) {
        s = Math.max(0, Math.min(1, s));
        opacity = Math.max(0, Math.min(1, opacity));
        return Color.hsb(color.getHue(), s, color.getBrightness(), opacity);
    }

    public static Color adjustBrightness(Color color, double b, double opacity) {
        b = Math.max(0, Math.min(1, b));
        opacity = Math.max(0, Math.min(1, opacity));
        return Color.hsb(color.getHue(), color.getSaturation(), b, opacity);
    }

    public static Color adjustOpacity(Color color, double opacity) {
        opacity = Math.max(0, Math.min(1, opacity));
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), opacity);
    }

    public static Blend getColorBlend(Color targetColor, double degree) {
        ColorInput colorInput = new ColorInput(0, 0, 16384, 16384, adjustOpacity(targetColor, degree));

        Blend blend = new Blend();
        blend.setMode(BlendMode.SRC_OVER);
        blend.setTopInput(colorInput);

        return blend;
    }
}
