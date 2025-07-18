package mai_onsyn.AnimeFX.layout;

import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Pair;

import java.util.HashMap;
import java.util.Map;


public class AutoPane extends Region {
    public enum AlignmentMode {
        TOP_LEFT,
        TOP_RIGHT,
        LEFT_CENTER,
        RIGHT_CENTER,
        CENTER,
    }
    public enum Motion {
        LEFT,
        RIGHT,
        TOP,
        BOTTOM
    }
    public enum LocateMode {
        ABSOLUTE,
        RELATIVE
    }

    private final Map<Node, Pair<Boolean, Double>[]> nodeAlignData = new HashMap<>();
    private final Map<Node, Boolean[]> flipRelativeMode = new HashMap<>();
    private final Map<Node, Pair<Pair<AlignmentMode, LocateMode>, Point2D>> rest = new HashMap<>();

    public void removePosition(Node node) {
        nodeAlignData.remove(node);
        flipRelativeMode.remove(node);
        rest.remove(node);
    }

    public void setPosition(Node node, boolean lb, boolean rb, boolean tb, boolean bb, double l, double r, double t, double b) {
        nodeAlignData.put(node, new Pair[]{
                new Pair<>(lb, l),
                new Pair<>(rb, r),
                new Pair<>(tb, t),
                new Pair<>(bb, b),
        });
        flush();
    }
    public void setPosition(Node node, boolean e, double l, double r, double t, double b) {
        nodeAlignData.put(node, new Pair[]{
                new Pair<>(e, l),
                new Pair<>(e, r),
                new Pair<>(e, t),
                new Pair<>(e, b),
        });
        flush();
    }
    public void setPosition(Node node, AlignmentMode a, LocateMode l,  double px, double py) {
        rest.put(node, new Pair<>(new Pair<>(a, l), new Point2D(px, py)));
        flush();
    }
    public void delete(Node node) {
        nodeAlignData.remove(node);
        flipRelativeMode.remove(node);
        rest.remove(node);
        super.getChildren().remove(node);
    }
    public void flipRelativeMode(Node node, Motion motion) {
        Boolean[] data = flipRelativeMode.get(node);
        if (data == null) {
            data = new Boolean[4];
            for (int i = 0; i < 4; i++) {
                data[i] = false;
            }
            flipRelativeMode.put(node, data);
        }
        switch (motion) {
            case LEFT -> data[0] = !data[0];
            case RIGHT -> data[1] = !data[1];
            case TOP -> data[2] = !data[2];
            case BOTTOM -> data[3] = !data[3];
        }
    }
    public void flipRelativeMode(Node node, Motion motion, boolean flip) {
        Boolean[] data = flipRelativeMode.get(node);
        if (data == null) {
            data = new Boolean[4];
            for (int i = 0; i < 4; i++) {
                data[i] = false;
            }
            flipRelativeMode.put(node, data);
        }
        switch (motion) {
            case LEFT -> data[0] = flip;
            case RIGHT -> data[1] = flip;
            case TOP -> data[2] = flip;
            case BOTTOM -> data[3] = flip;
        }
    }

    @Override
    public ObservableList<Node> getChildren() {
        return super.getChildren();
    }

    public AutoPane() {
        super.widthProperty().addListener((o, ov, nv) -> {
            double width = nv.doubleValue();
            flushWidth(width);
        });

        super.heightProperty().addListener((o, ov, nv) -> {
            double height = nv.doubleValue();
            flushHeight(height);
        });
    }

    public void flush() {
        flushWidth(super.getLayoutBounds().getWidth());
        flushHeight(super.getLayoutBounds().getHeight());
    }

    public void flush(double w, double h) {
        flushWidth(w);
        flushHeight(h);
    }

    public void flushWidth(double width) {
        for (Map.Entry<Node, Pair<Boolean, Double>[]> entry : nodeAlignData.entrySet()) {
            Node node = entry.getKey();
            Pair<Boolean, Double>[] configs = entry.getValue();
            Boolean[] flips = flipRelativeMode.getOrDefault(node, new Boolean[]{false, false, false, false});

            double l, r;

            if (configs[0].getKey()) l = configs[0].getValue() * width;
            else l = configs[0].getValue();
            if (configs[1].getKey()) r = configs[1].getValue() * width;
            else r = configs[1].getValue();

            if (flips[0] && flips[1]) {
                setWidth(node, width - r - l);
                node.setLayoutX(r);
            } else if (flips[0]) {
                setWidth(node, Math.abs(r - l));
                node.setLayoutX(width - Math.max(r, l));
            } else if (flips[1]) {
                setWidth(node, Math.abs(r - l));
                node.setLayoutX(Math.min(r, l));
            } else {
                setWidth(node, width - r - l);
                node.setLayoutX(l);
            }
        }

        for (Map.Entry<Node, Pair<Pair<AlignmentMode, LocateMode>, Point2D>> entry : rest.entrySet()) {
            Node node = entry.getKey();
            Point2D point = entry.getValue().getValue();

            double xValue = switch (entry.getValue().getKey().getValue()) {
                case ABSOLUTE -> point.getX();
                case RELATIVE -> point.getX() * width;
            };

            switch (entry.getValue().getKey().getKey()) {
                case TOP_LEFT, LEFT_CENTER -> node.setLayoutX(xValue);
                case TOP_RIGHT, RIGHT_CENTER -> node.setLayoutX(xValue - getWidth(node) + width);
                case CENTER -> node.setLayoutX(xValue - getWidth(node) / 2);
            }
        }
    }

    public void flushHeight(double height) {
        for (Map.Entry<Node, Pair<Boolean, Double>[]> entry : nodeAlignData.entrySet()) {
            Node node = entry.getKey();
            Pair<Boolean, Double>[] configs = entry.getValue();
            Boolean[] flips = flipRelativeMode.getOrDefault(node, new Boolean[]{false, false, false, false});

            double t, b;
            if (configs[2].getKey()) t = configs[2].getValue() * height;
            else t = configs[2].getValue();
            if (configs[3].getKey()) b = configs[3].getValue() * height;
            else b = configs[3].getValue();

            if (flips[2] && flips[3]) {
                setHeight(node, height - b - t);
                node.setLayoutY(b);
            } else if (flips[2]) {
                setHeight(node, Math.abs(b - t));
                node.setLayoutY(height - Math.max(b, t));
            } else if (flips[3]) {
                setHeight(node, Math.abs(b - t));
                node.setLayoutY(Math.min(b, t));
            } else {
                setHeight(node, height - b - t);
                node.setLayoutY(t);
            }
        }

        for (Map.Entry<Node, Pair<Pair<AlignmentMode, LocateMode>, Point2D>> entry : rest.entrySet()) {
            Node node = entry.getKey();
            Point2D point = entry.getValue().getValue();

            double yValue = switch (entry.getValue().getKey().getValue()) {
                case ABSOLUTE -> point.getY();
                case RELATIVE -> point.getY() * height;
            };

            switch (entry.getValue().getKey().getKey()) {
                case TOP_LEFT, TOP_RIGHT -> node.setLayoutY(yValue);
                case CENTER, LEFT_CENTER, RIGHT_CENTER -> node.setLayoutY(yValue - getHeight(node) / 2);
            }
        }
    }

    private double getWidth(Node node) {
        return switch (node) {
            case Label label -> {
                Text text = new Text(label.getText());
                text.setFont(label.getFont());
                yield text.getLayoutBounds().getWidth();
            }
            case Region region -> region.getLayoutBounds().getWidth();
            case Rectangle rectangle -> rectangle.getWidth();
            case ImageView imageView -> imageView.getFitWidth();
            case null, default -> 0;
        };
    }

    private double getHeight(Node node) {
        return switch (node) {
            case Label label -> {
//                Text text = new Text(label.getText());
//                text.setFont(label.getFont());
//                yield text.getLayoutBounds().getHeight();
                yield label.getLayoutBounds().getHeight();
            }
            case Region region -> region.getLayoutBounds().getHeight();
            case Rectangle rectangle -> rectangle.getHeight();
            case ImageView imageView -> imageView.getFitHeight();
            case null, default -> 0;
        };
    }

    private void setWidth(Node node, double v) {
        switch (node) {
            case AutoPane pane -> {
                pane.setMaxWidth(v);
                pane.setMinWidth(v);
                pane.setPrefWidth(v);
            }
            case Region region -> region.setPrefWidth(v);
            case Rectangle rectangle -> rectangle.setWidth(v);
            case ImageView imageView -> imageView.setFitWidth(v);
            default -> throw new IllegalStateException("Unexpected value: " + node);
        }
    }

    private void setHeight(Node node, double v) {
        switch (node) {
            case AutoPane pane -> {
                pane.setMaxHeight(v);
                pane.setMinHeight(v);
                pane.setPrefHeight(v);
            }
            case Region region -> region.setPrefHeight(v);
            case Rectangle rectangle -> rectangle.setHeight(v);
            case ImageView imageView -> imageView.setFitHeight(v);
            default -> throw new IllegalStateException("Unexpected value: " + node);
        }
    }

    public static void lockSize(Region node, double width, double height) {
        node.setMaxHeight(height);
        node.setMinHeight(height);

        node.setMaxWidth(width);
        node.setMinWidth(width);
    }
}
