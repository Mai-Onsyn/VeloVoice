package mai_onsyn.AnimeFX.Styles;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

public class DefaultAXTreeItemStyle extends DefaultAXButtonStyle implements AXTreeItemStyle {

    private Image icon = new WritableImage(1, 1);
    private double iconInsets = 3;
    private double textLeftInsets = 0;
    private double textRightInsets = 10;
    private double childrenInsets = 30;
    private double itemHeight = 25;

    @Override
    public Image getIcon() {
        return icon;
    }

    @Override
    public double getIconInsets() {
        return iconInsets;
    }

    @Override
    public double getTextLeftInsets() {
        return textLeftInsets;
    }

    @Override
    public double getTextRightInsets() {
        return textRightInsets;
    }

    @Override
    public double getChildrenInsets() {
        return childrenInsets;
    }

    @Override
    public double getItemHeight() {
        return itemHeight;
    }


    public void setIcon(Image icon) {
        this.icon = icon;
    }

    public void setIconInsets(double iconInsets) {
        this.iconInsets = iconInsets;
    }

    public void setChildrenInsets(double childrenInsets) {
        this.childrenInsets = childrenInsets;
    }

    public void setItemHeight(double itemHeight) {
        this.itemHeight = itemHeight;
    }

    public void setTextLeftInsets(double textLeftInsets) {
        this.textLeftInsets = textLeftInsets;
    }

    public void setTextRightInsets(double textRightInsets) {
        this.textRightInsets = textRightInsets;
    }
}
