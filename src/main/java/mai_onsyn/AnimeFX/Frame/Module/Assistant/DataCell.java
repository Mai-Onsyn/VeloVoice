package mai_onsyn.AnimeFX.Frame.Module.Assistant;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class DataCell<T> extends Cell {

    private T data;

    public DataCell(double depth, double offset, Image image, String text) {
        super(depth, offset, image, text);
    }

    public DataCell<T> setData(T data) {
        this.data = data;
        return this;
    }

    public T getData() {
        return data;
    }





    public DataCell<T> borderColor(Color color) {
        super.borderColor(color);
        return this;
    }
    public DataCell<T> borderShape(double v1) {
        super.borderShape(v1);
        return this;
    }
    public DataCell<T> font(Font font) {
        super.font(font);
        return this;
    }
    public DataCell<T> width(double width) {
        super.width(width);
        return this;
    }
    public DataCell<T> height(double height) {
        super.height(height);
        return this;
    }
    public DataCell<T> bgColor(Color bgColor) {
        super.bgColor(bgColor);
        return this;
    }
    public DataCell<T> animeDuration(int animeDuration) {
        super.animeDuration(animeDuration);
        return this;
    }
    public DataCell<T> textColor(Color textColor) {
        super.textColor(textColor);
        return this;
    }
    public DataCell<T> ftImageXOffset(double ftImageXOffset) {
        super.ftImageXOffset(ftImageXOffset);
        return this;
    }
    public DataCell<T> ftImageYOffset(double ftImageYOffset) {
        super.ftImageYOffset(ftImageYOffset);
        return this;
    }
    public DataCell<T> textXOffset(double textXOffset) {
        super.textXOffset(textXOffset);
        return this;
    }
    public DataCell<T> textYOffset(double textYOffset) {
        super.textYOffset(textYOffset);
        return this;
    }
    public DataCell<T> align(int align) {
        super.align(align);
        return this;
    }
    public DataCell<T> bgImage(Image bgImage) {
        super.bgImage(bgImage);
        return this;
    }
    public DataCell<T> ftImage(Image ftImage) {
        super.ftImage(ftImage);
        return this;
    }
    public DataCell<T> ftImageRatio(double v) {
        super.ftImageRatio(v);
        return this;
    }
    public DataCell<T> name(String description) {
        super.name(description);
        return this;
    }
    public DataCell<T> borderRadius(double r) {
        super.borderRadius(r);
        return this;
    }
    public DataCell<T> textFocusColor(Color textFocusColor) {
        super.textFocusColor(textFocusColor);
        return this;
    }
    public DataCell<T> bgFocusColor(Color backGroundFocusColor) {
        super.bgFocusColor(backGroundFocusColor);
        return this;
    }
    public DataCell<T> fillColor(Color fillColor) {
        super.fillColor(fillColor);
        return this;
    }
    public DataCell<T> init() {
        super.init();
        return this;
    }
}