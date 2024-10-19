package mai_onsyn.AnimeFX2.layout;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
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
import mai_onsyn.AnimeFX2.AutoUpdatable;
import mai_onsyn.AnimeFX2.Module.AXBase;
import mai_onsyn.AnimeFX2.Module.AXButton;
import mai_onsyn.AnimeFX2.Styles.AXContextPaneStyle;
import mai_onsyn.AnimeFX2.Styles.DefaultAXContextPaneStyle;
import mai_onsyn.AnimeFX2.Utls.Toolkit;

public class AXContextPane extends Popup implements AutoUpdatable {
    private AXContextPaneStyle style = new DefaultAXContextPaneStyle();
    private final AXBase root = new AXBase();
    private final VBox content = new VBox();
    private final ScrollPane scrollPane = new ScrollPane();

    public AXContextPane() {
        super();
        super.getContent().add(root);

        scrollPane.setContent(content);
        scrollPane.setFitToWidth(true);
        scrollPane.getStylesheets().add("style.css");
        Toolkit.addSmoothScrolling(scrollPane);

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

    private void flushSize() {
        double height = content.getChildren().size() * style.getItemHeight() + 2 * style.getBGInsets() + 2;
        height = Math.min(height, style.getMaxHeight());
        root.setMaxHeight(height);
        root.setMinHeight(height);
    }

    public void setTheme(AXContextPaneStyle style) {
        this.style = style;
    }

    public AXBase getRoot() {
        return root;
    }

    @Override
    public void update() {
        root.update();
    }


    @Override
    public void show(Window ownerWindow, double anchorX, double anchorY) {
        super.setOpacity(0);

        root.setMaxHeight(0);
        root.setMinHeight(0);

        content.setSpacing(-style.getItemHeight());
        double height = content.getChildren().size() * style.getItemHeight() + 2 * style.getBGInsets() + 2;
        height = Math.min(height, style.getMaxHeight());

        super.show(ownerWindow, anchorX, anchorY);
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(200 * style.getAnimeRate()),
                new KeyValue(super.opacityProperty(), 1),
                new KeyValue(root.maxHeightProperty(), height),
                new KeyValue(root.minHeightProperty(), height),
                new KeyValue(content.spacingProperty(), 0)
        ));
        timeline.play();
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
        name.setFont(new Font(name.getFont().getName(), name.getFont().getSize()));  // 默认字体

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

        button.setPosition(name, AutoPane.AlignmentMode.LEFT_CENTER, AutoPane.LocateMode.ABSOLUTE, itemHeight * 1.2, itemHeight / 2);
        button.setPosition(prompt, AutoPane.AlignmentMode.RIGHT_CENTER, AutoPane.LocateMode.ABSOLUTE, -itemHeight * 0.1, itemHeight / 2);
    }
}
