package mai_onsyn.AnimeFX.Frame.Module.Assistant;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import mai_onsyn.AnimeFX.Frame.Module.DiffusionButton;
import mai_onsyn.AnimeFX.Frame.Module.SmoothChoiceBox;

public class Cell extends DiffusionButton {

    private final double height;
    private final double offset;
    private boolean fold = false;
    private boolean fileType = false;
    private final String text;
    private final Image image;

    public Cell(double depth, double offset, Image image, String text) {
        this.offset = offset;
        this.height = depth;
        this.text = text;
        this.image = image;
    }

    public double getOffset() {
        return offset;
    }

    public boolean isFile() {
        return fileType;
    }

    public void setType(boolean fileType) {
        this.fileType = fileType;
    }

    public double getSize() {
        return height;
    }

    public boolean isFold() {
        return fold;
    }

    public void fold() {
        this.fold = true;
    }

    public void unfold() {
        this.fold = false;
    }




    public Cell borderColor(Color color) {
        super.borderColor(color);
        return this;
    }
    public Cell borderShape(double v1) {
        super.borderShape(v1);
        return this;
    }
    public Cell font(Font font) {
        super.font(font);
        return this;
    }
    public Cell width(double width) {
        super.width(width);
        return this;
    }
    public Cell height(double height) {
        super.height(height);
        return this;
    }
    public Cell bgColor(Color bgColor) {
        super.bgColor(bgColor);
        return this;
    }
    public Cell animeDuration(int animeDuration) {
        super.animeDuration(animeDuration);
        return this;
    }
    public Cell textColor(Color textColor) {
        super.textColor(textColor);
        return this;
    }
    public Cell ftImageXOffset(double ftImageXOffset) {
        super.ftImageXOffset(ftImageXOffset);
        return this;
    }
    public Cell ftImageYOffset(double ftImageYOffset) {
        super.ftImageYOffset(ftImageYOffset);
        return this;
    }
    public Cell textXOffset(double textXOffset) {
        super.textXOffset(textXOffset);
        return this;
    }
    public Cell textYOffset(double textYOffset) {
        super.textYOffset(textYOffset);
        return this;
    }
    public Cell align(int align) {
        super.align(align);
        return this;
    }
    public Cell bgImage(Image bgImage) {
        super.bgImage(bgImage);
        return this;
    }
    public Cell ftImage(Image ftImage) {
        super.ftImage(ftImage);
        return this;
    }
    public Cell ftImageRatio(double v) {
        super.ftImageRatio(v);
        return this;
    }
    public Cell name(String description) {
        super.name(description);
        return this;
    }
    public Cell borderRadius(double r) {
        super.borderRadius(r);
        return this;
    }
    public Cell textFocusColor(Color textFocusColor) {
        super.textFocusColor(textFocusColor);
        return this;
    }
    public Cell bgFocusColor(Color backGroundFocusColor) {
        super.bgFocusColor(backGroundFocusColor);
        return this;
    }
    public Cell fillColor(Color fillColor) {
        super.fillColor(fillColor);
        return this;
    }


    public Cell init() {

        Text t = new Text(text);
        t.setFont(super.getFont());
        double imageRatio = image.getWidth() / image.getHeight();
        double width = imageRatio * height + t.getLayoutBounds().getWidth() + offset * 2;

        super.name(text)
                .width(width)
                .height(height)
                .ftImage(image)
                .ftImageRatio(1)
                .ftImageXOffset((width - imageRatio * height) / -2 + offset)
                .textXOffset((width - t.getLayoutBounds().getWidth()) / 2 - offset);
        super.init();
        return this;
    }
}
