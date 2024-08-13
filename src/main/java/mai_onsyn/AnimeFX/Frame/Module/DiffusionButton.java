package mai_onsyn.AnimeFX.Frame.Module;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.effect.Effect;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class DiffusionButton extends Pane {

    private Color bgColor = new Color(1, 1, 1, 0.5);
    private Color bgFocusColor = new Color(0.827451f, 0.827451f, 0.827451f, 0.5);
    private Color fillColor = new Color(0.6784314f, 0.84705883f, 0.9019608f, 0.5);
    private Color textColor = new Color(0, 0, 0, 0.8);
    private Color textFocusColor = new Color(1, 0, 0, 0.8);
    private int animeDuration = 500;
    private double width = 100;
    private double height = 50;
    private double ftImageRatio = 0.5;
    private Font textFont = new Font(12);
    private double ftImageXOffset, ftImageYOffset, textXOffset, textYOffset;
    private int align = 1;

    public static int ALIGN_LEFT = 0, ALIGN_MIDDLE = 1, ALIGN_RIGHT = 2;

    private final Rectangle clip = new Rectangle();

    private final Circle circle = new Circle(0);
    private final Rectangle bgCover = new Rectangle(0, 0);
    private final Rectangle ftCover = new Rectangle(0, 0);
    private final Label label = new Label("");
    private final Rectangle border = new Rectangle(0, 0, new Color(0, 0, 0, 0));
    private final ImageView bgImage = new ImageView();
    private final ImageView ftImage = new ImageView();

    private volatile boolean animeState;

    public DiffusionButton bgColor(Color backGroundColor) {
        this.bgColor = backGroundColor;
        return this;
    }
    public DiffusionButton bgFocusColor(Color backGroundFocusColor) {
        this.bgFocusColor = backGroundFocusColor;
        return this;
    }
    public DiffusionButton fillColor(Color fillColor) {
        this.fillColor = fillColor;
        return this;
    }
    public DiffusionButton textColor(Color textColor) {
        this.textColor = textColor;
        return this;
    }
    public DiffusionButton textFocusColor(Color textFocusColor) {
        this.textFocusColor = textFocusColor;
        return this;
    }
    public DiffusionButton animeDuration(int animeDuration) {
        this.animeDuration = animeDuration;
        return this;
    }
    public DiffusionButton width(double width) {
        this.width = width;
        return this;
    }
    public DiffusionButton height(double height) {
        this.height = height;
        return this;
    }
    public DiffusionButton font(Font textFont) {
        this.textFont = textFont;
        return this;
    }
    public DiffusionButton bgImage(Image bgImage) {
        this.bgImage.setImage(bgImage);
        return this;
    }
    public DiffusionButton ftImage(Image ftImage) {
        this.ftImage.setImage(ftImage);
        this.ftImage.setPreserveRatio(true);
        return this;
    }
    public DiffusionButton ftImageRatio(double v) {
        this.ftImageRatio = v;
        return this;
    }
    public DiffusionButton name(String description) {
        this.label.setText(description);
        return this;
    }
    public DiffusionButton borderRadius(double r) {
        this.border.setStrokeWidth(r);
        return this;
    }
    public DiffusionButton borderColor(Color color) {
        this.border.setStroke(color);
        return this;
    }
    public DiffusionButton borderShape(double v1) {
        clip.widthProperty().addListener((o, ov, nv) -> clip.setArcWidth(v1));

        clip.heightProperty().addListener((o, ov, nv) -> clip.setArcHeight(v1));

        border.widthProperty().addListener((o, ov, nv) -> border.setArcWidth(v1));

        border.heightProperty().addListener((o, ov, nv) -> border.setArcHeight(v1));

        return this;
    }
    public DiffusionButton ftImageXOffset(double ftImageXOffset) {
        this.ftImageXOffset = ftImageXOffset;
        return this;
    }
    public DiffusionButton ftImageYOffset(double ftImageYOffset) {
        this.ftImageYOffset = ftImageYOffset;
        return this;
    }
    public DiffusionButton textXOffset(double textXOffset) {
        this.textXOffset = textXOffset;
        return this;
    }
    public DiffusionButton textYOffset(double textYOffset) {
        this.textYOffset = textYOffset;
        return this;
    }
    public DiffusionButton align(int align) {
        this.align = align;
        return this;
    }

    public DiffusionButton init() {
        label.setTextFill(this.textColor);
        label.toFront();
        if (!(textFont == null)) {
            label.setFont(textFont);
        }

        bgCover.setFill(this.bgColor);
        circle.setFill(this.fillColor);
        ftCover.setFill(new Color(1, 1, 1, 0));
        super.setClip(clip);

        super.widthProperty().addListener((o, ov, nv) -> {
            this.width = nv.doubleValue();
            Text model = new Text(label.getText());
            model.setFont(this.textFont);
            double labelWidth = model.getLayoutBounds().getWidth();

            if (align == 0) {
                label.setLayoutX(textXOffset);
            }
            else if (align == 1) {
                label.setLayoutX(width / 2.0 - labelWidth / 2.0 + textXOffset);
            }
            else if (align == 2) {
                label.setLayoutX(width - labelWidth + textXOffset);
            }

            bgCover.setWidth(this.width);
            ftCover.setWidth(this.width);
            border.setWidth(this.width);
            clip.setWidth(width);

            if (bgImage.getImage() != null) resizeBGImage();
            if (ftImage.getImage() != null) resizeFTImage();
        });

        super.heightProperty().addListener((o, ov, nv) -> {
            this.height = nv.doubleValue();
            Platform.runLater(() -> label.setLayoutY(height / 2.0 - label.getHeight() / 2.0 + textYOffset));

            bgCover.setHeight(this.height);
            ftCover.setHeight(this.height);
            border.setHeight(this.height);
            clip.setHeight(height);

            if (bgImage.getImage() != null) resizeBGImage();
            if (ftImage.getImage() != null) resizeFTImage();
        });


        super.setOnMouseEntered(event -> {
            setCursor(Cursor.HAND);
            bgTrans(this.bgFocusColor);
            textTrans(textFocusColor);
        });
        super.setOnMouseExited(event -> {
            bgTrans(this.bgColor);
            textTrans(textColor);
        });

        super.setOnMousePressed(event -> {
            if (!animeState) {
                animeState = true;
                Point2D layout = super.localToScene(0, 0);

                double target = Math.max(width, height) * 1.415;

                circle.setCenterX(event.getSceneX() - layout.getX());
                circle.setCenterY(event.getSceneY() - layout.getY());

                bloomTrans(target);
            }
        });

        super.setOnMouseReleased(event -> Thread.ofVirtual().start(() -> {
            if (animeState) {
                while (animeState) {
                    Thread.onSpinWait();
                }
                Platform.runLater(() -> {
                    circle.setRadius(0);
                    ftTrans(new Color(fillColor.getRed(), fillColor.getGreen(), fillColor.getBlue(), 0));
                });
            }
        }));

        super.setPrefWidth(this.width);
        super.setPrefHeight(this.height);


        //super.setStyle("-fx-background-color:#" + this.bgColor.toString().substring(2));
        super.getChildren().addAll(bgImage, bgCover, ftImage, circle, ftCover, border, label);

        return this;
    }

    public void setBgImageEffect(Effect effect) {
        this.bgImage.setEffect(effect);
    }

    public void setFtImageEffect(Effect effect) {
        this.ftImage.setEffect(effect);
    }

    public String getText() {
        return label.getText();
    }

    public void setText(String text) {
        label.setText(text);

        Text model = new Text(text);
        model.setFont(this.textFont);
        double labelWidth = model.getLayoutBounds().getWidth();

        if (align == 0) {
            label.setLayoutX(textXOffset);
        }
        else if (align == 1) {
            label.setLayoutX(width / 2.0 - labelWidth / 2.0 + textXOffset);
        }
        else if (align == 2) {
            label.setLayoutX(width - labelWidth + textXOffset);
        }
    }

    public void setBgColor(Color color) {
        bgColor = color;
        bgTrans(bgColor);
    }

    public void setBgFocusColor(Color bgFocusColor, boolean update) {
        this.bgFocusColor = bgFocusColor;
        if (update) bgTrans(bgFocusColor);
    }

    public void setTextColor(Color textColor, boolean update) {
        this.textColor = textColor;
        if (update) textTrans(textColor);
    }

    public void setTextFocusColor(Color textFocusColor, boolean update) {
        this.textFocusColor = textFocusColor;
        if (update) textTrans(textFocusColor);
    }

    public Color getBgColor() {
        return bgColor;
    }

    public Color getBgFocusColor() {
        return bgFocusColor;
    }

    public Color getTextColor() {
        return textColor;
    }

    public Color getTextFocusColor() {
        return textFocusColor;
    }

    private void resizeFTImage() {
        if (ftImage.getLayoutBounds().getWidth() > ftImage.getLayoutBounds().getHeight()) {
            ftImage.setFitHeight(Math.min(width, height) * ftImageRatio);
        }
        else {
            ftImage.setFitWidth(Math.min(width, height) * ftImageRatio);
        }

        ftImage.setLayoutX(width / 2.0 - ftImage.getLayoutBounds().getWidth() / 2.0 + ftImageXOffset);
        ftImage.setLayoutY(height / 2.0 - ftImage.getLayoutBounds().getHeight() / 2.0 + ftImageYOffset);
    }

    private void resizeBGImage() {
        double imageRatio = bgImage.getImage().getWidth() / bgImage.getImage().getHeight();
        double paneRatio = width / height;

        if (imageRatio <= paneRatio) {
            // 图片宽高比大于等于 Pane 宽高比，以 Pane 的宽度为基准调整 ImageView 的尺寸
            bgImage.setFitWidth(width);
            bgImage.setFitHeight(width / imageRatio);
        } else {
            // 图片宽高比小于 Pane 宽高比，以 Pane 的高度为基准调整 ImageView 的尺寸
            bgImage.setFitHeight(height);
            bgImage.setFitWidth(height * imageRatio);
        }

        // 居中显示 ImageView
        bgImage.setLayoutX((width - bgImage.getFitWidth()) / 2);
        bgImage.setLayoutY((height - bgImage.getFitHeight()) / 2);
    }

    private void bgTrans(Color B) {
        //FillTransition bgAnime = new FillTransition(Duration.millis(animeDuration), bgCover, (Color) bgCover.getFill(), B);
        Timeline bgAnime = new Timeline(new KeyFrame(Duration.millis(animeDuration), new KeyValue(bgCover.fillProperty(), B)));
        bgAnime.play();
    }

    private void bloomTrans(double target) {
        circle.setRadius(0);
        circle.setFill(fillColor);
        Timeline bloomAnime = new Timeline(new KeyFrame(Duration.millis(animeDuration), new KeyValue(circle.radiusProperty(), target)));
        bloomAnime.setOnFinished(event -> {
            if (bloomAnime.getStatus() == Animation.Status.STOPPED) {
                animeState = false;
            }
        });
        bloomAnime.play();
        Timeline disappearAnime = new Timeline(new KeyFrame(Duration.millis(animeDuration), new KeyValue(circle.fillProperty(), new Color(fillColor.getRed(), fillColor.getGreen(), fillColor.getBlue(), 0))));
        disappearAnime.setDelay(Duration.millis(animeDuration / 2.0));
        disappearAnime.play();

    }

    private void ftTrans(Color B) {
        FillTransition ftAnime = new FillTransition(Duration.millis(animeDuration), ftCover, (Color) circle.getFill(), B);
        ftAnime.play();
    }

    private void textTrans(Color B) {
        Timeline textAnime = new Timeline(new KeyFrame(Duration.millis(animeDuration), new KeyValue(label.textFillProperty(), B)));
        textAnime.play();
    }

    public Font getFont() {
        return textFont;
    }
}
