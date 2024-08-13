package mai_onsyn.AnimeFX.Frame.Styles;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import mai_onsyn.AnimeFX.Frame.Module.SmoothTextField;

public class DefaultNamePopupStyle implements NamePopupStyle{
    @Override
    public SmoothTextField createTextField() {
        return new SmoothTextField()
                .borderRadius(1)
                .subLineColor(Color.GREEN)
                .font(new Font(15))
                .borderShape(30)
                .init();
    }

    @Override
    public Label createDescription(String description) {
        return new Label(description);
    }

    @Override
    public Rectangle createBackground(double width, double height) {
        Rectangle rectangle = new Rectangle(width, height);
        rectangle.setFill(Color.LIGHTGRAY);
        return rectangle;
    }
}
