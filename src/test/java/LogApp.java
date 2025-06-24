import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LogApp extends Application {

    private final ObservableList<JavaFXAppender.StyledLog> logList = FXCollections.observableArrayList();
    private static final Logger logger = LogManager.getLogger(LogApp.class);

    @Override
    public void start(Stage stage) {
        // 初始化 ListView
        ListView<JavaFXAppender.StyledLog> logListView = new ListView<>(logList);
        logListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(JavaFXAppender.StyledLog item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item.text());
                    setStyle(item.style());
                }
            }
        });

        // 注册日志消费者
        JavaFXAppender.setLogConsumer(styledLog -> {
            if (logList.size() > 1000) logList.remove(0); // 限制条目数
            logList.add(styledLog);
            logListView.scrollTo(logList.size() - 1); // 自动滚动
        });

        // 测试日志
        logger.info("普通信息消息");
        logger.warn("警告消息");
        logger.error("错误消息");

        stage.setScene(new Scene(logListView, 600, 400));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}