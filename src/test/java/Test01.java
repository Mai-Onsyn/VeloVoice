import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

public class Test01 extends Application {

    @Override
    public void start(Stage primaryStage) {
        Pane dropPane = new Pane();
        dropPane.setMinSize(400, 300);
        dropPane.setStyle("-fx-background-color: lightgray;");

        // 设置拖放监听器
        dropPane.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                // 允许拖放文件
                if (event.getDragboard().hasFiles()) {
                    event.acceptTransferModes(TransferMode.COPY);
                }
                event.consume();
            }
        });

        dropPane.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                // 阻止默认处理
                event.setDropCompleted(true);
                event.consume();

                // 提取文件
                List<File> files = event.getDragboard().getFiles();
                if (files != null && !files.isEmpty()) {
                    // 这里可以处理文件，例如显示文件名
                    StringBuilder sb = new StringBuilder();
                    for (File file : files) {
                        sb.append(file.getName()).append("\n");
                    }
                    Label label = new Label(sb.toString());
                    StackPane.setAlignment(label, javafx.geometry.Pos.CENTER);
                    dropPane.getChildren().add(label);
                }
            }
        });

        StackPane root = new StackPane();
        root.getChildren().add(dropPane);

        Scene scene = new Scene(root, 500, 400);

        primaryStage.setTitle("Drag and Drop Example");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}