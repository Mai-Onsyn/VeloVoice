import javafx.application.Platform;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

import java.io.Serializable;
import java.util.function.Consumer;

@Plugin(name = "JavaFXAppender", category = "Core", elementType = "appender")
public class JavaFXAppender extends AbstractAppender {

    private static Consumer<StyledLog> logConsumer;

    protected JavaFXAppender(String name, Filter filter, Layout<? extends Serializable> layout) {
        super(name, filter, layout, true, null);
    }

    @PluginFactory
    public static JavaFXAppender createAppender(
            @PluginAttribute("name") String name,
            @PluginElement("Filter") Filter filter,
            @PluginElement("Layout") Layout<? extends Serializable> layout) {
        return new JavaFXAppender(name, filter, layout);
    }

    @Override
    public void append(LogEvent event) {
        if (logConsumer == null) return;

        // 获取日志文本
        String message = new String(getLayout().toByteArray(event));
        // 根据日志级别设置颜色
        String color = switch (event.getLevel().name()) {
            case "ERROR" -> "-fx-fill: red;";
            case "WARN" -> "-fx-fill: orange;";
            case "INFO" -> "-fx-fill: green;";
            case "DEBUG" -> "-fx-fill: blue;";
            default -> "-fx-fill: black;";
        };

        // 转发到 JavaFX 线程
        Platform.runLater(() ->
                logConsumer.accept(new StyledLog(message, color))
        );
    }

    // 设置 JavaFX 日志消费者
    public static void setLogConsumer(Consumer<StyledLog> consumer) {
        logConsumer = consumer;
    }

    // 封装带样式的日志
    public static record StyledLog(String text, String style) {}
}