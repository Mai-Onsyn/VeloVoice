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
        // 确保在JavaFX应用线程执行
        Platform.runLater(() -> {

            if (lineCount > 1000) {
                super.textArea().deleteText(0, super.textArea().getText().indexOf("\n") + 1);
            }


            // 获取当前文本长度作为插入位置
            int timeStartPos = super.textArea().getLength();
            int threadStartPos = timeStartPos + logEvent.time.length() + 3;
            int levelStartPos = threadStartPos + logEvent.thread.length() + 3;

            // 构建带格式的日志行
            String formattedLog = String.format("[%s] [%s] [%s] %s\n",
                    logEvent.time,
                    logEvent.thread,
                    logEvent.level,
                    logEvent.message
            );

            appendText(formattedLog);

            String cssStyle = getStyleForLevel(logEvent.level);

            textArea().setStyle(timeStartPos, threadStartPos, "-fx-fill: #3993d4;");
            textArea().setStyle(threadStartPos, levelStartPos, "-fx-fill: #90890e;");
            textArea().setStyle(levelStartPos, levelStartPos + formattedLog.length(), cssStyle);

            virtualizedScrollPane.scrollYBy(2147483647);


            lineCount++;

        });
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
                    Throwable throwable = logEvent.getThrown();
                    String message;
                    if (throwable != null) {
                        StringWriter sw = new StringWriter();
                        throwable.printStackTrace(new PrintWriter(sw));
                        message = logEvent.getMessage().getFormattedMessage() + "\n" + sw;
                    } else message = logEvent.getMessage().getFormattedMessage();
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
