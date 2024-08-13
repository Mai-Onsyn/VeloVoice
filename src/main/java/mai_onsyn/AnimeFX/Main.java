package mai_onsyn.AnimeFX;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import mai_onsyn.AnimeFX.Frame.Layout.AutoPane;
import mai_onsyn.AnimeFX.Frame.Layout.NamePopup;
import mai_onsyn.AnimeFX.Frame.Module.*;
import mai_onsyn.AnimeFX.Frame.Module.Assistant.Cell;
import mai_onsyn.AnimeFX.Frame.Styles.DefaultCellStyle;

import java.util.ArrayList;
import java.util.List;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        //PopupMenu.setStage(stage);
        NamePopup.setStage(stage);
        stage.setWidth(1280);
        stage.setHeight(720);

        AutoPane root = new AutoPane();
        root.setOnMousePressed(event -> root.requestFocus());
        Scene scene = new Scene(root);
        stage.setScene(scene);
        //Toolkit.addBackGroundImage(root, Theme.BACKGROUND_IMAGE);


        SmoothTreeView treeView = new SmoothTreeView(new DefaultCellStyle())
                .borderShape(40)
                .borderRadius(1)
                .animeDuration(100)
                .borderColor(Color.DEEPPINK)
                .init();

        List<Cell> items = new ArrayList<>();
        for (int i = 0; i < 13; i++) {
            Cell cell = new Cell(20, 15, new Image("textures/folder_light.png"), "Test-" + i);
            items.add(cell);
        }

        treeView.addItem(treeView.getRoot().getData(), items.getFirst());
        treeView.addItem(treeView.getRoot().getData(), items.get(1));
        treeView.addItem(treeView.getRoot().getData(), items.get(2));
        treeView.addItem(treeView.getRoot().getData(), items.get(3));
        treeView.addItem(items.get(0), items.get(4));
        treeView.addItem(items.get(0), items.get(5));
        treeView.addItem(items.get(0), items.get(6));
        treeView.addItem(items.get(1), items.get(7));
        treeView.addItem(items.get(1), items.get(8));
        treeView.addItem(items.get(1), items.get(9));
        treeView.addItem(items.get(2), items.get(10));
        treeView.addItem(items.get(2), items.get(11));
        treeView.addItem(items.get(2), items.get(12));

        SmoothTextArea area = new SmoothTextArea()
                .borderRadius(1)
                .borderColor(Color.DEEPPINK)
                .font(new Font(20))
                .borderShape(40)
                .init();

        SmoothTextField field = new SmoothTextField()
                .borderRadius(1)
                .borderColor(Color.DEEPPINK)
                .subLineColor(Color.GREEN)
                .font(new Font(20))
                .borderShape(30)
                .init();

        SmoothChoiceBox button = new SmoothChoiceBox(20);
        List<DiffusionButton> popupButtons = new ArrayList<>();
        for (int i = 0; i < 32; i++) {
            DiffusionButton item = new DiffusionButton()
                    .name("Test-" + i)
                    .height(20)
                    .bgColor(new Color(0, 0, 0, 0.1))
                    .init();
            popupButtons.add(item);

            item.setOnMouseClicked(event -> button.setText(item.getText()));
        }
        button.addItem(popupButtons.toArray(new DiffusionButton[0]));
        button.name("Test");
        button.animeDuration(200);
        button.popupBorderShape(40);
        button.popupMaxHeight(500);
        button.borderShape(40);
        button.borderColor(Color.DEEPPINK);
        button.borderRadius(1);
        button.init();

        Circle circle = new Circle(10);
        circle.setStroke(Color.DEEPPINK);
        circle.setStrokeWidth(1);
        SmoothSlider slider = new SmoothSlider(0, 4, 1)
                .thumb(circle)
                .thumbColor(Color.rgb(248, 182, 152, 0.5))
                .init();

        SmoothSwitch toggleSwitch = new SmoothSwitch()
                .borderColor(Color.DEEPPINK)
                .borderRadius(1)
                .thumbBorderColor(Color.DEEPPINK)
                .thumbBorderRadius(1)
                .animeDuration(200)
                .init();

        //root.setPosition(slider, false, 0.45, 0.06666);
        //root.setPosition(toggleSwitch, false, 0.6, 0.06666);
        root.setPosition(treeView, false, true, false, false, 50, 0.7, 200, 50);
        root.setPosition(area, true, false, false, false, 0.35, 50, 200, 50);
        root.setPosition(button, true, false, true, true, 0.75, 50, 0.06666, 0.865);
        root.setPosition(field, false, true, true, false, 50, 0.75, 0.06666, 0);

        root.getChildren().addAll(treeView, area, field, button, slider, toggleSwitch);

        //root.setStyle("-fx-background-color: #40c0c0ff");
        stage.show();
    }

    public static void main(String[] args) {
        launch(Main.class);
    }
}