package mai_onsyn.AnimeFX2.Module;

import javafx.application.Platform;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AXLogger extends AXTextArea {
    /**
     * 0 - error
     * 1 - warn
     * 2 - info
     * 3 - debug
     */
    private int level = 3;

    private final SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

    public AXLogger() {
        super();
        super.setEditable(false);
    }

    private boolean firstLine = true;
    private void log(String s) {
        if (firstLine) {
            Platform.runLater(() -> super.appendText("[" + format.format(new Date()) + "] " + s));
            firstLine = false;
        }
        else Platform.runLater(() -> super.appendText("\n[" + format.format(new Date()) + "] " + s));
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
}
