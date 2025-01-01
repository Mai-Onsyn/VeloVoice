package mai_onsyn.AnimeFX2.Utls;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorInput;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

public class Toolkit {

    public static java.awt.Toolkit defaultToolkit = java.awt.Toolkit.getDefaultToolkit();

    public static Interpolator SHARP_OUT = new Interpolator() {
        @Override
        protected double curve(double t) {
            return Math.sqrt(Math.sqrt(t));
        }
    };

    public static String colorToString(Color color) {
        return "#" + color.toString().substring(2);
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

    public static void adjustImageColor(WritableImage image, Color newColor) {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();

        PixelReader pixelReader = image.getPixelReader();
        PixelWriter pixelWriter = image.getPixelWriter();

        // 遍历图像像素块并批量修改颜色
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color originalColor = pixelReader.getColor(x, y);
                double opacity = originalColor.getOpacity();  // 获取透明度

                if (opacity > 0) {
                    // 更改颜色但保留透明度
                    Color adjustedColor = new Color(newColor.getRed(), newColor.getGreen(), newColor.getBlue(), opacity);
                    pixelWriter.setColor(x, y, adjustedColor);
                }
            }
        }
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
