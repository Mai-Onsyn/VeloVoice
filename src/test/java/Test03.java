import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import mai_onsyn.AnimeFX2.Module.AXLogger;
import mai_onsyn.AnimeFX2.Module.AXTextArea;

import java.util.logging.*;

public class Test03 extends Application {

    private static final Logger logger = Logger.getLogger(Test03.class.getName());
    private AXTextArea logTextArea;

    @Override
    public void start(Stage primaryStage) {
        // 初始化文本区域用于显示日志
        logTextArea = new AXLogger();
        logTextArea.setEditable(false);

        // 创建一个自定义的StreamHandler来将日志输出到TextArea
        Handler textAreaHandler = new StreamHandler(new LogTextAreaOutputStream(logTextArea.textArea()), new SimpleFormatter()) {
            @Override
            public void publish(LogRecord record) {
                super.publish(record);
                flush(); // 确保日志立即显示
            }

            @Override
            public void close() throws SecurityException {
                // 不关闭TextArea的流
            }
        };
        textAreaHandler.setFormatter(new SimpleFormatter());
        textAreaHandler.setLevel(Level.ALL); // 设置日志级别
        logger.addHandler(textAreaHandler); // 将Handler添加到Logger

        // 移除默认的ConsoleHandler，因为我们已经有自己的Handler了
        for (Handler handler : logger.getHandlers()) {
            if (handler instanceof ConsoleHandler) {
                logger.removeHandler(handler);
                break;
            }
        }

        // 设置UI
        BorderPane root = new BorderPane();
        root.setCenter(logTextArea);
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("JavaFX Log Viewer");
        primaryStage.setScene(scene);
        primaryStage.show();

        // 输出一些日志作为示例
        logger.info("Application started.");
        logger.warning("This is a warning message.");
        logger.severe("This is a severe error message.");
    }

    // 自定义OutputStream，将日志输出到TextArea
    private static class LogTextAreaOutputStream extends java.io.OutputStream {
        private final TextArea textArea;
        private StringBuilder buffer = new StringBuilder();

        public LogTextAreaOutputStream(TextArea textArea) {
            this.textArea = textArea;
        }

        @Override
        public void write(int b) {
            char c = (char) b;
            if (c == '\n') {
                flushBuffer();
            } else {
                buffer.append(c);
            }
        }

        private void flushBuffer() {
            if (buffer.length() > 0) {
                String text = buffer.toString();
                buffer.setLength(0); // 清空缓冲区
                textArea.appendText(text + "\n"); // 将文本追加到TextArea
            }
        }

        @Override
        public void flush() {
            flushBuffer(); // 确保所有缓冲的日志都输出到TextArea
        }

        @Override
        public void close() {
            // 不需要关闭TextArea的流
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}