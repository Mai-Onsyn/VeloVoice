package mai_onsyn.AnimeFX.layout;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Popup;
import javafx.stage.Window;
import javafx.util.Duration;
import mai_onsyn.AnimeFX.AutoUpdatable;
import mai_onsyn.AnimeFX.Module.AXBase;
import mai_onsyn.AnimeFX.Module.AXButton;
import mai_onsyn.AnimeFX.Styles.AXContextPaneStyle;
import mai_onsyn.AnimeFX.Styles.DefaultAXContextPaneStyle;
import mai_onsyn.AnimeFX.Utls.Toolkit;

public class AXContextPane extends Popup implements AutoUpdatable {
    private AXContextPaneStyle style = new DefaultAXContextPaneStyle();
    private final AXBase root = new AXBase();
    private final VBox content = new VBox();
    private final ScrollPane scrollPane = new AXScrollPane();

    private double vValue;
    private boolean onScaling = false;

    public AXContextPane() {
        super();
        super.getContent().add(root);

        scrollPane.setContent(content);
        scrollPane.setFitToWidth(true);
        scrollPane.vvalueProperty().addListener((o, ov, nv) -> {
            if (!onScaling) vValue = nv.doubleValue();
        });

        root.getChildren().add(scrollPane);
        root.setPosition(scrollPane, false, style.getBGInsets(), style.getBGInsets(), style.getBGInsets(), style.getBGInsets());
        root.setMaxWidth(style.getPaneWidth());
        root.setMinWidth(style.getPaneWidth());
        root.setTheme(style);
        root.update();

        super.setAutoHide(true);
    }

    public AXButton createItem() {
        AXButton item = new AXButton();
        item.setMaxHeight(style.getItemHeight());
        item.setMinHeight(style.getItemHeight());
        item.setTheme(style.getItemStyle());
        item.update();
        item.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.PRIMARY) super.hide();
        });
        content.getChildren().add(item);
        flushSize();
        return item;
    }

    public void removeItem(AXButton item) {
        content.getChildren().remove(item);
        flushSize();
    }

    public void showItem(AXButton item, int index) {
        content.getChildren().add(index, item);
        flushSize();
    }

    public void showItem(AXButton item) {
        content.getChildren().add(item);
        flushSize();
    }

    public boolean containsItem(AXButton item) {
        return content.getChildren().contains(item);
    }

    private void flushSize() {
        double height = content.getChildren().size() * style.getItemHeight() + 2 * style.getBGInsets() + 9;
        height = Math.min(height, style.getMaxHeight());
        root.setMaxHeight(height);
        root.setMinHeight(height);
    }

    public void setTheme(AXContextPaneStyle style) {
        this.style = style;
        root.setTheme(style);
        for (Node child : content.getChildren()) {
            if (child instanceof AXButton button) button.setTheme(style.getItemStyle());
        }
    }

    public AXBase getRoot() {
        return root;
    }

    @Override
    public void update() {
        root.update();
        for (Node child : content.getChildren()) {
            if (child instanceof AXButton button) {
                button.setMaxHeight(style.getItemHeight());
                button.setMinHeight(style.getItemHeight());
                button.update();
            }
        }
    }


    @Override
    public void show(Window ownerWindow, double anchorX, double anchorY) {
        onScaling = true;
        super.setOpacity(0);

        root.setMaxHeight(0);
        root.setMinHeight(0);

        content.setSpacing(-style.getItemHeight());
        double height = content.getChildren().size() * style.getItemHeight() + 2 * style.getBGInsets() + 9;

        double targetV = vValue;
        if (height <= style.getMaxHeight()) {
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            targetV = 0;
        }
        else scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        height = Math.min(height, style.getMaxHeight());

        super.show(ownerWindow, anchorX, anchorY);
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(200 * style.getAnimeRate()),
                new KeyValue(super.opacityProperty(), 1, Toolkit.SHARP_OUT),
                new KeyValue(root.maxHeightProperty(), height, Toolkit.SHARP_OUT),
                new KeyValue(root.minHeightProperty(), height, Toolkit.SHARP_OUT),
                new KeyValue(content.spacingProperty(), 0, Toolkit.SHARP_OUT),
                new KeyValue(scrollPane.vvalueProperty(), targetV, Toolkit.SHARP_OUT)
        ));
        timeline.play();
        timeline.setOnFinished(f -> onScaling = false);
    }

    public AXContextPaneStyle getStyle() {
        return this.style;
    }

    public ScrollPane getScrollPane() {
        return this.scrollPane;
    }

    public static void setupContextMenuItem(AXButton button, WritableImage icon, String text, String shortcut, double itemHeight) {
        ImageView imageView = new ImageView(icon);
        Label name = button.getTextLabel();
        name.setText(text);
        //name.setFont(new Font(name.getFont().getName(), name.getFont().getSize()));  // 默认字体
        name.setAlignment(Pos.CENTER_LEFT);

        // 设置快捷键提示
        Label prompt = new Label(shortcut);
        prompt.setFont(new Font(name.getFont().getName(), name.getFont().getSize() * 0.8));
        prompt.setTextFill(Color.GRAY);

        // 将图标、名称、快捷键提示添加到按钮
        button.getChildren().addAll(imageView, prompt);

        // 设置各个组件的位置和布局
        button.setPosition(imageView, false, itemHeight * 0.1, itemHeight * 0.9, itemHeight * 0.1, itemHeight * 0.1);
        button.flipRelativeMode(imageView, AutoPane.Motion.RIGHT);

        Toolkit.adjustImageColor(icon, button.style().getTextColor());  // 调整图标颜色

        button.setPosition(name, false, itemHeight * 1.2, 0, 0, 0);
        button.setPosition(prompt, AutoPane.AlignmentMode.RIGHT_CENTER, AutoPane.LocateMode.ABSOLUTE, -itemHeight * 0.1, itemHeight / 2);
    }
}
