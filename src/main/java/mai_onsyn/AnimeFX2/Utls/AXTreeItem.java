package mai_onsyn.AnimeFX2.Utls;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import mai_onsyn.AnimeFX2.AutoUpdatable;
import mai_onsyn.AnimeFX2.Module.AXButton;
import mai_onsyn.AnimeFX2.Module.AXTreeView;
import mai_onsyn.AnimeFX2.Styles.AXTreeItemStyle;
import mai_onsyn.AnimeFX2.Styles.DefaultAXTreeItemStyle;
import mai_onsyn.AnimeFX2.layout.AutoPane;

import java.util.List;

public class AXTreeItem extends Pane implements AutoUpdatable {

    private AXTreeItemStyle style = new DefaultAXTreeItemStyle();
    private final AXButton item;
    private final VBox childrenBox = new VBox();

    private Timeline expandTimeline = new Timeline();
    private boolean isExpanded = true;

    private AXTreeView<?> attribution;

    public AXTreeItem(String name) {
        this.item = new AXButton(name);
        item.setTheme(style);
        item.getChildren().add(new ImageView(style.getIcon()));

        item.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getClickCount() % 2 == 0) {
                if (isExpanded) {
                    collapse();
                } else {
                    expand();
                }
            }
        });

        update();
        super.getChildren().addAll(item, childrenBox);
        item.getTextLabel().setAlignment(Pos.CENTER_LEFT);
    }

    public List<AXTreeItem> getChildrenAsItem() {
        return childrenBox.getChildren().stream().map(node -> (AXTreeItem) node).toList();
    }

    public ObservableList<Node> getChildrenItem() {
        return childrenBox.getChildren();
    }

    public void add(AXTreeItem... item) {
        if (attribution != null) {
            attribution.add(this, item);
            for (AXTreeItem treeItem : item) {
                treeItem.setAttribution(attribution);
            }
        }
        else childrenBox.getChildren().addAll(item);
        for (AXTreeItem treeItem : item) {
            this.update();
        }
    }

    public void addToChildrenBox(AXTreeItem... item) {
        childrenBox.getChildren().addAll(item);
    }

    public void setAttribution(AXTreeView<?> attribution) {
        this.attribution = attribution;
    }

    public void remove(AXTreeItem item) {
        childrenBox.getChildren().remove(item);
    }

    public AXTreeItem getParentItem() {
        if (this.getParent() != null && this.getParent() instanceof VBox box) {
            if (box.getParent() instanceof AXTreeItem parent) {
                return parent;
            } else return null;
        } else return null;
    }

    public AXTreeView<?> getAttribution() {
        return attribution;
    }

    public void expand() {
        if (!childrenBox.getChildren().isEmpty() && !isExpanded) {
            isExpanded = true;

            super.getChildren().add(childrenBox);
            expandTimeline.stop();
            expandTimeline = new Timeline(new KeyFrame(Duration.millis(200 * style.getAnimeRate()),
                    new KeyValue(childrenBox.spacingProperty(), 0, Toolkit.SHARP_OUT),
                    new KeyValue(childrenBox.layoutYProperty(), style.getItemHeight(), Toolkit.SHARP_OUT),
                    new KeyValue(childrenBox.opacityProperty(), 1, Toolkit.SHARP_OUT)
            ));
            expandTimeline.play();
        }
    }

    public void collapse() {
        if (!childrenBox.getChildren().isEmpty() && isExpanded) {
            for (AXTreeItem treeItem : getChildrenAsItem()) {
                treeItem.collapse();
            }

            isExpanded = false;

            expandTimeline.stop();
            expandTimeline = new Timeline(new KeyFrame(Duration.millis(200 * style.getAnimeRate()),
                    new KeyValue(childrenBox.spacingProperty(), -style.getItemHeight(), Toolkit.SHARP_OUT),
                    new KeyValue(childrenBox.layoutYProperty(), 0, Toolkit.SHARP_OUT),
                    new KeyValue(childrenBox.opacityProperty(), 0, Toolkit.SHARP_OUT)
            ));
            expandTimeline.setOnFinished(_ -> super.getChildren().remove(childrenBox));
            expandTimeline.play();
        }
    }

    @Override
    public void update() {
        childrenBox.setLayoutX(style.getChildrenInsets());
        childrenBox.setLayoutY(style.getItemHeight());

        double width = style.getItemHeight() + parseTextSize(item.getText(), item.style().getTextFont()) + style.getTextLeftInsets() + style.getTextRightInsets();
        item.setMaxWidth(width);
        item.setMinWidth(width);

        item.setMaxHeight(style.getItemHeight());
        item.setMinHeight(style.getItemHeight());

        if (item.getChildren().getLast() instanceof ImageView iconView) {
            iconView.setImage(style.getIcon());

            item.setPosition(iconView, false, style.getIconInsets(), style.getItemHeight() - style.getIconInsets(), style.getIconInsets(), style.getIconInsets());
            item.flipRelativeMode(iconView, AutoPane.Motion.RIGHT, true);
        }

        item.setPosition(item.getTextLabel(), false, style.getItemHeight() + style.getTextLeftInsets(), 0, 0, 0);

        item.update();
    }

    public void setTheme(AXTreeItemStyle style) {
        this.style = style;
        item.setTheme(style);
    }

    private static double parseTextSize(String text, Font font) {
        Text t = new Text(text);
        t.setFont(font);
        return t.getLayoutBounds().getWidth();
    }

    public AXButton getButton() {
        return item;
    }

    public String getHeadName() {
        return item.getText();
    }

    public void rename(String name) {
        item.setText(name);
        this.update();
    }

    public void clear() {
        childrenBox.getChildren().clear();
    }

    public void expandAll() {
        for (AXTreeItem item : getChildrenAsItem()) {
            item.expandAll();
        }
        expand();
    }

    public AXTreeItemStyle style() {
        return style;
    }

}
