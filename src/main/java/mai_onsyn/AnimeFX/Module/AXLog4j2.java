package mai_onsyn.AnimeFX.Module;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.fxmisc.flowless.VirtualizedScrollPane;

import java.awt.*;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AXLog4j2 extends AXInlineTextArea {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    private static final List<AXLog4j2> logListeners = new ArrayList<>();
    private static final List<LogEventSnapshot> logTemps = new ArrayList<>(256);

    private static boolean timerStarted = false;

    private int lineCount = 0;
    private final VirtualizedScrollPane<?> virtualizedScrollPane = (VirtualizedScrollPane<?>) textArea().getParent();
    private volatile boolean showThread = true;
    private volatile boolean showLevel  = true;

    private static final int MAX_LINES = 2000;
    private static final int TRIM_LINES = 500;

    public AXLog4j2() {
        super.setEditable(false);
        logListeners.add(this);

        if (!timerStarted) {
            AnimationTimer timer = new AnimationTimer() {
                @Override
                public void handle(long now) {

                    synchronized (AXLog4j2.class) {
                        for (LogEventSnapshot eventSnapshot : logTemps) {
                            for (AXLog4j2 logListener : logListeners) {
                                logListener.appendLog(eventSnapshot);
                            }
                        }

                        logTemps.clear();
                    }

                }
            };
            timer.start();
            timerStarted = true;
        }
    }

    private void appendLog(LogEventSnapshot logEvent) {
        Platform.runLater(() -> {

            trimIfNeeded();

            int start = textArea().getLength();
            StringBuilder sb = new StringBuilder();

            // 时间
            int timeStart = sb.length();
            sb.append('[').append(logEvent.time).append("] ");

            // 线程
            int threadStart = -1;
            if (showThread) {
                threadStart = sb.length();
                sb.append('[').append(logEvent.thread).append("] ");
            }

            // level
            int levelStart = -1;
            if (showLevel) {
                levelStart = sb.length();
                sb.append('[').append(logEvent.level).append("] ");
            }

            // message
            int messageStart = sb.length();
            sb.append(logEvent.message).append('\n');

            appendText(sb.toString());

            // === 样式 ===
            textArea().setStyle(start + timeStart, start + timeStart + logEvent.time.length() + 2, "-fx-fill:#3993d4;");

            if (showThread) {
                textArea().setStyle(start + threadStart, start + threadStart + logEvent.thread.length() + 2, "-fx-fill:#90890e;");
            }

            String levelStyle = getStyleForLevel(logEvent.level);
            if (showLevel) {
                textArea().setStyle(start + levelStart, start + levelStart + logEvent.level.length() + 2, levelStyle);
            }

            textArea().setStyle(start + messageStart, start + messageStart + logEvent.message.length(), levelStyle);

            virtualizedScrollPane.scrollYBy(Double.MAX_VALUE);
            lineCount++;
        });
    }

    private void trimIfNeeded() {
        if (lineCount <= MAX_LINES) return;

        int removeTo = nthNewLine(TRIM_LINES);
        if (removeTo > 0) {
            textArea().deleteText(0, removeTo);
            lineCount -= TRIM_LINES;
        }
    }

    private int nthNewLine(int n) {
        String text = textArea().getText();
        int count = 0;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '\n' && ++count == n) {
                return i + 1;
            }
        }
        return -1;
    }


    // 根据日志级别返回对应的CSS样式
    private String getStyleForLevel(String level) {
        return switch (level) {
            case "ERROR" -> "-fx-fill: #ff4444;";
            case "WARN" -> "-fx-fill: #daa520;";
            case "INFO" -> "-fx-fill: #" + super.style.getDefaultTextColor().toString().substring(2) + ";";
            default -> "-fx-fill: #646464;";
        };
    }

    private record LogEventSnapshot(String time, String thread, String level, String message) {}

    @Plugin(name = "AXAppender", category = "Core", elementType = "appender")
    private static class AXAppender extends AbstractAppender {

        protected AXAppender(String name, Filter filter, Layout<? extends Serializable> layout) {
            super(name, filter, layout, false, new Property[0]);
        }

        @Override
        public void append(LogEvent logEvent) {
            if (logEvent.getLevel() != Level.TRACE && logEvent.getLevel() != Level.ALL) {
                synchronized (AXLog4j2.class) {
//                    Throwable throwable = logEvent.getThrown();
                    String message = logEvent.getMessage().getFormattedMessage();
                    logTemps.add(new LogEventSnapshot(
                            dateFormat.format(new Date(logEvent.getTimeMillis())),
                            logEvent.getThreadName(),
                            logEvent.getLevel().name(),
                            message
                    ));

                    if (logEvent.getLevel() == Level.ERROR) {
                        Toolkit.getDefaultToolkit().beep();
                    }
                }
            }
        }

        @PluginFactory
        public static AXAppender createAppender(
                @PluginAttribute("name") String name,
                @PluginElement("Filter") Filter filter,
                @PluginElement("Layout") Layout<? extends Serializable> layout) {
            return new AXAppender(name, filter, layout);
        }
    }
}
