package mai_onsyn.AnimeFX.Frame.Module;

import javafx.application.Platform;
import javafx.scene.Cursor;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import mai_onsyn.AnimeFX.Frame.Styles.ButtonStyle;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FXLogger extends SmoothTextArea {
    public FXLogger borderRadius(double r) {
        super.borderRadius(r);
        return this;
    }
    public FXLogger borderColor(Color color) {
        super.borderColor(color);
        return this;
    }
    public FXLogger borderShape(double v1) {
        super.borderShape(v1);
        return this;
    }
    public FXLogger font(Font font) {
        super.font(font);
        return this;
    }
    public FXLogger width(double width) {
        super.width(width);
        return this;
    }
    public FXLogger height(double height) {
        super.height(height);
        return this;
    }
    public FXLogger bgColor(Color bgColor) {
        super.bgColor(bgColor);
        return this;
    }
    public FXLogger animeDuration(int animeDuration) {
        super.animeDuration(animeDuration);
        return this;
    }
    public FXLogger textHighlightBGColor(Color textHighlightBGColor) {
        super.textHighlightBGColor(textHighlightBGColor);
        return this;
    }
    public FXLogger textHighlightColor(Color textHighlightColor) {
        super.textHighlightColor(textHighlightColor);
        return this;
    }
    public FXLogger textColor(Color textColor) {
        super.textColor(textColor);
        return this;
    }
    public FXLogger init() {
        super.init();
        return this;
    }
    public FXLogger buttonStyle(ButtonStyle style) {
        super.buttonStyle(style);
        return this;
    }

    public FXLogger logLevel(int level) {
        this.level = level;
        return this;
    }

    private final TextArea logMenu;
    private final SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");


    /**
     * 0 - error
     * 1 - warn
     * 2 - info
     * 3 - debug
     */
    private int level = 3;

    public FXLogger() {
        super();
        logMenu = super.getTextArea();
        logMenu.setEditable(false);
        //logMenu.setOnMouseEntered(event -> logMenu.setCursor(Cursor.DEFAULT));
    }

    private void log(String s) {
        if (logMenu.getText().isEmpty()) {
            Platform.runLater(() -> logMenu.appendText("[" + format.format(new Date()) + "] " + s));
            return;
        }
        Platform.runLater(() -> logMenu.appendText("\n[" + format.format(new Date()) + "] " + s));
    }

    public void debug(String s) {
        if (level >= 3) log(s);
    }

    public void info(String s) {
        if (level >= 2) log(s);
    }

    public void warn(String s) {
        if (level >= 1) log(s);
    }

    public void error(String s) {
        log(s);
    }

    public void prompt(String s) {
        log(s);
    }

    public void cancel() {
        String text = logMenu.getText();
        int lastIndex = text.lastIndexOf('\n');
        if (lastIndex >= 0) {
            logMenu.setText(text.substring(0, lastIndex + 1));
        } else {
            logMenu.clear();
        }
    }
}
