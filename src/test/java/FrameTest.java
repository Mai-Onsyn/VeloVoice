import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import mai_onsyn.AnimeFX2.LanguageManager;
import mai_onsyn.AnimeFX2.Module.*;
import mai_onsyn.AnimeFX2.Styles.DefaultAXTreeItemStyle;
import mai_onsyn.AnimeFX2.ThemeManager;
import mai_onsyn.AnimeFX2.Utls.AXButtonGroup;
import mai_onsyn.AnimeFX2.Utls.AXTreeItem;
import mai_onsyn.AnimeFX2.layout.AutoPane;
import mai_onsyn.AnimeFX2.layout.HDoubleSplitPane;

import java.io.File;

public class FrameTest extends Application {
    @Override
    public void start(Stage stage) throws Exception {

        AutoPane root = new AutoPane();
        root.setOnMousePressed(_ -> root.requestFocus());

        ThemeManager manager = new ThemeManager();
        LanguageManager languageManager = new LanguageManager(new File("D:\\Users\\Desktop\\Test\\lang"));
        {

            AXButton button = new AXButton("Test");
            AXTextArea textArea = new AXTextArea();
            AXChoiceBox choiceBox = new AXChoiceBox();
            textArea.setEditable(true);
            for (int i = 0; i < 20; i++) {
                choiceBox.createItem().setText("Item " + i);
            }

            manager.register(button, textArea);

            languageManager.register("test.button", button);
            languageManager.register("test.text_area", textArea);

            Thread.ofVirtual().start(() -> {
                try {
                    while (true) {
                        Thread.sleep(3000);
                        Platform.runLater(() -> languageManager.switchLanguage("en_us"));
                        Thread.sleep(3000);
                        Platform.runLater(() -> languageManager.switchLanguage("zh_cn"));
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });

            AXProgressBar progressBar = new AXProgressBar();

//            Thread.ofVirtual().start(() -> {
//                try {
//                    double i = 0;
//                    while (true) {
//                        Thread.sleep(80);
//
//                        i += 0.01;
//
//                        progressBar.setProgress(i % 1.0);
//                    }
//                } catch (InterruptedException _) {}
//            });
            Platform.runLater(() -> progressBar.setProgress(0.5));

            AXButton b1 = new AXButton("B1");
            AXButton b2 = new AXButton("B2");
            AXButton b3 = new AXButton("B3");

            AXButtonGroup group = new AXButtonGroup(b1, b2, b3);

            root.setPosition(b1, true, 0, 0.9, 0, 0.9);
            root.setPosition(b2, true, 0.1, 0.8, 0, 0.9);
            root.setPosition(b3, true, 0.2, 0.7, 0, 0.9);

            root.getChildren().addAll(button, textArea, choiceBox, b1, b2, b3, progressBar);
            root.setPosition(button, true, true, true, true, 0.1, 0.8, 0.1, 0.8);
            root.setPosition(textArea, true, true, true, true, 0.6, 0.1, 0.3, 0.2);
            root.setPosition(choiceBox, true, true, true, true, 0.8, 0.05, 0.05, 0.1);
            root.setPosition(progressBar, false, 50, 50, 70, 50);
            root.flipRelativeMode(choiceBox, AutoPane.Motion.BOTTOM);
            root.flipRelativeMode(progressBar, AutoPane.Motion.TOP);
        }

        {
            DefaultAXTreeItemStyle fileStyle = new DefaultAXTreeItemStyle();
            DefaultAXTreeItemStyle folderStyle = new DefaultAXTreeItemStyle();
            fileStyle.setIcon(new Image("textures/icons/file.png"));
            folderStyle.setIcon(new Image("textures/icons/folder.png"));

            AXTreeItem treeItem = new AXTreeItem("root");

            AXTreeItem c1 = new AXTreeItem("c1");
            AXTreeItem c2 = new AXTreeItem("c2");
            AXTreeItem c3 = new AXTreeItem("c3");

            AXTreeItem cc1 = new AXTreeItem("cc1");
            AXTreeItem cc2 = new AXTreeItem("cc2");

            treeItem.add(c1, c2, c3);
            c1.add(cc1, cc2);

            treeItem.setTheme(folderStyle);
            c1.setTheme(folderStyle);
            c2.setTheme(fileStyle);
            c3.setTheme(fileStyle);
            cc1.setTheme(fileStyle);
            cc2.setTheme(fileStyle);
            //treeItem.setStyle("-fx-background-color: #ff000020");
            manager.register(treeItem, c1, c2, c3, cc1, cc2);

            AXButtonGroup group = new AXButtonGroup(treeItem.getButton(), c1.getButton(), c2.getButton(), c3.getButton(), cc1.getButton(), cc2.getButton());
            //root.getChildren().add(treeItem);
            treeItem.setLayoutY(100);
        }

        {
            AXTreeView<String> treeView = new AXTreeView<>();
            manager.register(treeView);
            languageManager.register("test.treeview", treeView);

            //treeView.add(treeView.getRoot(), treeView.createFileItem("file1", "A"), treeView.createFileItem("file2", "B"), treeView.createFileItem("file3", "C"));

            AXTreeItem folder1 = treeView.createFolderItem("folder1");
            treeView.add(treeView.getRoot(), folder1, treeView.createFolderItem("folder2"));
            treeView.add(folder1, treeView.createFolderItem("folder3"), treeView.createFileItem("file4aaaaaaaaaa", "D"), treeView.createFileItem("file5", "E"));
            treeView.add(folder1, treeView.createFileItem("file4", "D"), treeView.createFileItem("file5", "E"));

            root.getChildren().add(treeView);
            root.setPosition(treeView, true, 0.05, 0.55, 0.3, 0.15);
        }

        {
            AXSlider slider = new AXSlider(0, 5, 0.5, 0.5);
            manager.register(slider);
            Label value = new Label();
            slider.valueProperty().addListener((o, ov, nv) -> {
                value.setText(nv.toString());
            });


            root.getChildren().addAll(slider, value);
            root.setPosition(slider, true, 0.4, 0.05, 0.2, 0.25);
            root.setPosition(value, AutoPane.AlignmentMode.LEFT_CENTER, AutoPane.LocateMode.RELATIVE, 0.4, 0.15);
            root.flipRelativeMode(slider, AutoPane.Motion.BOTTOM);
            //slider.setStyle("-fx-background-color: #00ff0020;");
        }

        {
            AXSwitch axSwitch = new AXSwitch(true);
            manager.register(axSwitch);
            //axSwitch.setStyle("-fx-background-color: #00ff0020;");

            root.getChildren().add(axSwitch);
            root.setPosition(axSwitch, true, 0.4, 0.5, 0.05, 0.1);
            root.flipRelativeMode(axSwitch, AutoPane.Motion.BOTTOM);

        }

        manager.flushAll();


        AutoPane root2 = new AutoPane();
        HDoubleSplitPane splitPane = new HDoubleSplitPane(20, 0.3);
        root2.getChildren().add(splitPane);
        root2.setPosition(splitPane, false, 0, 0, 10, 10);

        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
