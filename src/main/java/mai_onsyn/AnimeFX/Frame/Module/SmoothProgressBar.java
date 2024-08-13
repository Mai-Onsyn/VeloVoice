package mai_onsyn.AnimeFX.Frame.Module;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class SmoothProgressBar extends Pane {

    private final Rectangle border = new Rectangle();
    private final Rectangle borderClip = new Rectangle();
    private Color bgColor = new Color(1, 1, 1, 0.5);
    private Color progressColor = new Color(0.4, 0.64, 1, 0.5);
    private Color[] switchColor = new Color[0];
    private double[] switchProgress = new double[0];
    private ImageView progressImage = null;
    private ImageView progressImageOnRunning = null;
    private double width = 100;
    private double innerWInsets = 4;
    private double height = 10;
    private double innerHInsets = 4;
    private int animeDuration = 400;

    private double progress = 0;
    private final Rectangle progressBar = new Rectangle();

    private double orinWidth, orinHeight;
    {
        orinWidth = width;
        orinHeight = height;
    }

    public SmoothProgressBar borderRadius(double r) {
        this.border.setStrokeWidth(r);
        this.borderClip.setStrokeWidth(r);
        return this;
    }
    public SmoothProgressBar borderColor(Color color) {
        this.border.setStroke(color);
        this.borderClip.setStroke(color);
        return this;
    }
    public SmoothProgressBar borderShape(double v1) {
        border.widthProperty().addListener((o, ov, nv) -> {
            double newArcWidth = nv.doubleValue() / orinWidth * v1;
            border.setArcWidth(newArcWidth);
            double arcWidthRatio = border.getWidth() / (width - innerWInsets * 2);
            double rect2ArcWidth = border.getArcWidth() / arcWidthRatio;
            Platform.runLater(() -> progressBar.setArcWidth(rect2ArcWidth));
        });

        border.heightProperty().addListener((o, ov, nv) -> {
            double newArcHeight = nv.doubleValue() / orinHeight * width / height * v1;
            border.setArcHeight(newArcHeight);
            double arcHeightRatio = border.getHeight() / (height - innerHInsets * 2);
            double rect2ArcHeight = border.getArcHeight() / arcHeightRatio;
            Platform.runLater(() -> progressBar.setArcHeight(rect2ArcHeight));
        });
        borderClip.widthProperty().addListener((o, ov, nv) -> {
            double newArcWidth = nv.doubleValue() / orinWidth * v1;
            borderClip.setArcWidth(newArcWidth);
        });

        borderClip.heightProperty().addListener((o, ov, nv) -> {
            double newArcHeight = nv.doubleValue() / orinHeight * width / height * v1;
            borderClip.setArcHeight(newArcHeight);
        });

        return this;
    }
    public SmoothProgressBar bgColor(Color bgColor) {
        this.bgColor = bgColor;
        return this;
    }
    public SmoothProgressBar progressColor(Color progressColor) {
        this.progressColor = progressColor;
        return this;
    }
    public SmoothProgressBar switchColor(Color... switchColor) {
        this.switchColor = switchColor;
        return this;
    }
    public SmoothProgressBar switchProgress(double... switchProgress) {
        this.switchProgress = switchProgress;
        return this;
    }
    public SmoothProgressBar progressImage(Image progressImage) {
        this.progressImage = new ImageView(progressImage);
        return this;
    }
    public SmoothProgressBar progressImageOnRunning(Image progressImageOnRunning) {
        this.progressImageOnRunning = new ImageView(progressImageOnRunning);
        return this;
    }
    public SmoothProgressBar width(double width) {
        this.width = width;
        this.orinWidth = width;
        return this;
    }
    public SmoothProgressBar height(double height) {
        this.height = height;
        this.orinHeight = height;
        return this;
    }
    public SmoothProgressBar innerHInsets(double innerHInsets) {
        this.innerHInsets = innerHInsets;
        return this;
    }
    public SmoothProgressBar innerWInsets(double innerWInsets) {
        this.innerWInsets = innerWInsets;
        return this;
    }
    public SmoothProgressBar animeDuration(int animeDuration) {
        this.animeDuration = animeDuration;
        return this;
    }


    public SmoothProgressBar init() {
        progressBar.setFill(progressColor);
        border.setFill(bgColor);
        //super.setStyle("-fx-background-color: #ff80ff");
        super.setPrefWidth(200);
        super.setPrefHeight(20);

        super.setClip(borderClip);

        super.widthProperty().addListener((o, ov, nv) -> {
            this.width = nv.doubleValue();
            border.setWidth(width);
            progressBar.setWidth((width - innerWInsets * 2) * progress);
            progressBar.setLayoutX(innerWInsets);

            //System.out.println(length);
            borderClip.setWidth(width);
        });
        super.heightProperty().addListener((o, ov, nv) -> {
            this.height = nv.doubleValue();
            border.setHeight(height);
            progressBar.setHeight(height - innerHInsets * 2);
            progressBar.setLayoutY(innerHInsets);
            //System.out.println(height);

            borderClip.setHeight(height);
        });

        super.getChildren().addAll(border, progressBar);

        return this;
    }

    public void setProgress(double p) {
        if (p > 1.0) p = 1.0;
        if (p < 0.0) p = 0.0;

        progress = p;

        if (progress < 1e-6) {
            progressBar.setWidth(0);
            progressBar.setFill(switchColor.length != 0 ? switchColor[0] : progressColor);
        }

        Timeline progressTrans = new Timeline(new KeyFrame(Duration.millis(animeDuration), new KeyValue(progressBar.widthProperty(), (width - innerWInsets * 2) * progress)));
        progressTrans.play();
        if (switchProgress.length != switchColor.length) throw new RuntimeException("The progress switch point and color do not correspond!");

        int index = findMaxIndexInRange(progress, p);
        if (index != -1) {
            Timeline colorTrans = new Timeline(new KeyFrame(Duration.millis(animeDuration), new KeyValue(progressBar.fillProperty(), switchColor[index])));
            colorTrans.play();
        }

        //this.progressBar.setWidth((length - innerWInsets * 2) * progress);
    }

    private int findMaxIndexInRange(double progress, double p) {
        double min = Math.min(progress, p);
        double max = Math.max(progress, p);
        int maxIndex = -1;
        double maxValue = Double.NEGATIVE_INFINITY;

        for (int i = 0; i < switchProgress.length; i++) {
            if (switchProgress[i] >= min && switchProgress[i] <= max) {
                if (switchProgress[i] > maxValue) {
                    maxValue = switchProgress[i];
                    maxIndex = i;
                }
            }
        }

        return maxIndex;
    }

    public double getProgress() {
        return progress;
    }

}
