package mai_onsyn.AnimeFX2.Styles;

import javafx.scene.paint.Color;

public class DefaultAXContextPaneStyle extends DefaultAXBaseStyle implements AXContextPaneStyle {

    private Color hoverShadow = Color.TRANSPARENT;
    private Color pressedShadow = Color.TRANSPARENT;
    private double itemArcSize = 10.0;
    private double itemHeight = 25.0;
    private double bgInsets = 5.0;
    private double paneWidth = 192.0;
    private double maxHeight = 300.0;

    @Override
    public Color getHoverShadow() {
        return hoverShadow;
    }

    @Override
    public Color getPressedShadow() {
        return pressedShadow;
    }

    @Override
    public double getItemHeight() {
        return itemHeight;
    }

    @Override
    public double getBGInsets() {
        return bgInsets;
    }

    @Override
    public double getPaneWidth() {
        return paneWidth;
    }

    @Override
    public double getMaxHeight() {
        return maxHeight;
    }

    @Override
    public void setHoverShadow(Color hoverShadow) {
        this.hoverShadow = hoverShadow;
    }

    @Override
    public void setPressedShadow(Color pressedShadow) {
        this.pressedShadow = pressedShadow;
    }

    public void setItemArcSize(double itemArcSize) {
        this.itemArcSize = itemArcSize;
    }

    public void setItemHeight(double itemHeight) {
        this.itemHeight = itemHeight;
    }

    public void setBgInsets(double bgInsets) {
        this.bgInsets = bgInsets;
    }

    public void setPaneWidth(double paneWidth) {
        this.paneWidth = paneWidth;
    }

    public void setMaxHeight(double maxHeight) {
        this.maxHeight = maxHeight;
    }

    @Override
    public ItemStyle getItemStyle() {
        return new ItemStyle();
    }

    public class ItemStyle extends DefaultAXButtonStyle {
        @Override
        public Color getBGColor() {
            return Color.TRANSPARENT;
        }

        public Color getBorderColor() {
            return Color.TRANSPARENT;
        }

        public double getBorderArcSize() {
            return itemArcSize;
        }
    }
}
