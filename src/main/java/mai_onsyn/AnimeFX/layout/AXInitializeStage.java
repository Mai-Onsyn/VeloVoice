package mai_onsyn.AnimeFX.layout;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class AXInitializeStage extends Stage {
    public AXInitializeStage() {
        initStage();
    }

    private void initStage() {
        // 设置无标题栏样式
        initStyle(StageStyle.UNDECORATED);

        // 创建内容布局
        VBox root = new VBox(10);
        root.setStyle("-fx-alignment: center; -fx-padding: 20; -fx-background-color: #f0f0f0;");

        // 添加进度指示器
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setStyle("-fx-progress-color: #3498db;");

        // 添加提示文本
        Label label = new Label("正在启动，请稍候...");
        label.setStyle("-fx-text-fill: #333333; -fx-font-size: 14px;");

        root.getChildren().addAll(progressIndicator, label);

        // 设置场景
        Scene scene = new Scene(root, 200, 150);
        setScene(scene);

        // 设置窗口位置居中
        centerOnScreen();

        // 设置窗口不可调整大小
        setResizable(false);
        Platform.runLater(() -> {});
    }
}
