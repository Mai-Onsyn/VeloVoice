package mai_onsyn.AnimeFX.Styles;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import mai_onsyn.AnimeFX.ResourceManager;

public class DefaultAXChoiceBoxStyle extends DefaultAXBaseStyle implements AXChoiceBoxStyle {

    private double signalRelateX = 5;
    private double signalScale = 20;
    private RelativePosition signalRelate = RelativePosition.RIGHT;
    private Image signalImage = ResourceManager.triangle;


    @Override
    public double getSignalRelateX() {
        return signalRelateX;
    }

    @Override
    public double getSignalScale() {
        return signalScale;
    }

    @Override
    public RelativePosition getSignalRelate() {
        return signalRelate;
    }

    @Override
    public Image getSignalImage() {
        return signalImage;
    }

    @Override
    public AXContextPaneStyle getContextPaneStyle() {
        return new DefaultAXContextPaneStyle();
    }

    @Override
    public AXButtonStyle getFreeButtonStyle() {
        return new DefaultAXButtonStyle();
    }

    @Override
    public AXButtonStyle getSelectedButtonStyle() {
        return new DefaultAXButtonGroupStyle();
    }


    public void setSignalRelateX(double signalRelateX) {
        this.signalRelateX = signalRelateX;
    }

    public void setSignalScale(double signalScale) {
        this.signalScale = signalScale;
    }

    public void setSignalRelate(RelativePosition signalRelate) {
        this.signalRelate = signalRelate;
    }

    public void setSignalImage(Image signalImage) {
        this.signalImage = signalImage;
    }

    @Override
    public Color getTextColor() {
        return Color.BLACK;
    }

    @Override
    public Color getTextHoverColor() {
        return Color.RED;
    }

    @Override
    public Color getFillColor() {
        return Color.CORNFLOWERBLUE;
    }

    @Override
    public Font getTextFont() {
        return new Font(12);
    }
}
