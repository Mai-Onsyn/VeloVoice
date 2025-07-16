package mai_onsyn.AnimeFX.layout;

import javafx.beans.value.ChangeListener;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.stage.Popup;
import javafx.stage.Window;
import mai_onsyn.AnimeFX.AutoUpdatable;
import mai_onsyn.AnimeFX.Module.AXBase;
import mai_onsyn.AnimeFX.Module.AXTextField;
import mai_onsyn.AnimeFX.Styles.AXTextInputPopupStyle;
import mai_onsyn.AnimeFX.Styles.DefaultAXTextInputPopupStyle;

public class AXTextInputPopup extends Popup implements AutoUpdatable {

    private AXTextInputPopupStyle style = new DefaultAXTextInputPopupStyle();

    private final AXBase pane = new AXBase();
    private final Label label = new Label();
    private final AXTextField textField = new AXTextField();

    private ChangeListener<String> onTextAvailable;

    public AXTextInputPopup() {
        pane.setTheme(style);
        textField.setTheme(style);

        pane.getChildren().addAll(label, textField);

        pane.setPosition(label, AutoPane.AlignmentMode.CENTER, AutoPane.LocateMode.RELATIVE, 0.5, 0.3);
        pane.setPosition(textField, AutoPane.AlignmentMode.CENTER, AutoPane.LocateMode.RELATIVE, 0.5, 0.6);

        //Draggable
        {
            final double[] offset = new double[2];
            pane.setOnMouseDragged(event -> {
                super.setX(event.getScreenX() - offset[0]);
                super.setY(event.getScreenY() - offset[1]);
            });
            pane.setOnMousePressed(event -> {
                pane.requestFocus();
                offset[0] = event.getSceneX();
                offset[1] = event.getSceneY();
            });
        }

        textField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER && !textField.getText().isEmpty()) {
                if (onTextAvailable != null) {
                    onTextAvailable.changed(null, null, textField.getText());
                }
                this.hide();
            }
        });

        super.getContent().add(pane);
        super.setAutoHide(true);

        update();
    }

    public void setTheme(AXTextInputPopupStyle style) {
        this.style = style;
        pane.setTheme(style);
        this.textField.setTheme(style.getTextFieldStyle());
    }

    public void show(String label, Window parent) {
        this.label.setText(label);
        super.show(parent);
        pane.flush();
    }

    public void show(String label, String text, Window parent) {
        this.show(label, parent);
        textField.setText(text);
        textField.textField().selectAll();
    }

    public void showOnCenter(String label, Window parent) {
        Point2D c = getPopupCorner(style.getWidth(), style.getHeight(), parent);
        super.show(parent, c.getX(), c.getY());

        this.label.setText(label);
        textField.textField().selectAll();
        pane.flush();
    }

    public void showOnCenter(String label, String text, Window parent) {
        showOnCenter(label, parent);

        textField.setText(text);
    }

    public void setOnTextAvailable(ChangeListener<String> listener) {
        this.onTextAvailable = listener;
    }

    @Override
    public void update() {
        label.setTextFill(style.getTextColor());
        label.setFont(style.getTextFont());

        textField.setMaxSize(style.getWidth() * 0.8, style.getTextFieldHeight());
        textField.setMinSize(style.getWidth() * 0.8, style.getTextFieldHeight());

        pane.setMaxSize(style.getWidth(), style.getHeight());
        pane.setMinSize(style.getWidth(), style.getHeight());

        pane.update();
        textField.update();
    }

    public void clear() {
        textField.clear();
    }

    public AXTextInputPopupStyle style() {
        return style;
    }

    private static Point2D getPopupCorner(double popupWidth, double popupHeight, Window window) {
        // 获取窗口的中心坐标
        double centerX = window.getX() + window.getWidth() / 2;
        double centerY = window.getY() + window.getHeight() / 2;

        // 计算Popup左上角的坐标
        double popupX = centerX - popupWidth / 2;
        double popupY = centerY - popupHeight / 2;

        // 返回左上角的Point2D
        return new Point2D(popupX, popupY);
    }
}
