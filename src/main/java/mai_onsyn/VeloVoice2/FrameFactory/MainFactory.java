package mai_onsyn.VeloVoice2.FrameFactory;

import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import mai_onsyn.AnimeFX2.Module.AXTextArea;
import mai_onsyn.AnimeFX2.Module.AXTreeView;
import mai_onsyn.AnimeFX2.Utls.AXButtonGroup;
import mai_onsyn.AnimeFX2.Utls.AXDataTreeItem;
import mai_onsyn.AnimeFX2.Utls.AXDatableButtonGroup;
import mai_onsyn.AnimeFX2.Utls.AXTreeItem;
import mai_onsyn.AnimeFX2.layout.AutoPane;
import mai_onsyn.AnimeFX2.layout.HDoubleSplitPane;

import static mai_onsyn.VeloVoice2.App.Runtime.*;

public class MainFactory {

    private static final Label textAreaInfo = new Label();
    private static final AXTreeView<SimpleStringProperty> treeView = new AXTreeView<>(SimpleStringProperty::new);
    private static final AXTextArea textArea = new AXTextArea();

    public static void drawMainFrame(AutoPane root) {
        HDoubleSplitPane textEditArea = getTextEditArea();

        root.getChildren().addAll(textEditArea);

        root.setPosition(textEditArea, false, 40, 520, 40, 40);
    }

    private static HDoubleSplitPane getTextEditArea() {

        HDoubleSplitPane root = new HDoubleSplitPane(40, 0.5, 50, 50);
        AutoPane leftPane = root.getLeft();
        AutoPane rightPane = root.getRight();


        themeManager.register(treeView, textArea);
        languageManager.register("frame.main.tree-view", treeView);
        languageManager.register("frame.main.text-edit-area", textArea);


        textAreaInfo.setAlignment(Pos.CENTER);
        textArea.setPosition(textAreaInfo, false, 0, 0, 0, 0);
        textArea.getChildren().add(textAreaInfo);
        AXDatableButtonGroup<AXTreeItem> treeViewGroup = treeView.getGroup();


        treeViewGroup.setOnSelectedChangedDatable((o, ov, nv) -> {
            if (ov != null) {
                if (ov.getValue() instanceof AXDataTreeItem<?> item) {
                    if (item.getData() instanceof SimpleStringProperty property) {
                        textArea.textProperty().unbindBidirectional(property);
                    }
                }
            }
            if (nv != null) {
                if (nv.getValue() instanceof AXDataTreeItem<?> item) {
                    enableTextArea();
                    if (item.getData() instanceof SimpleStringProperty property) {
                        textArea.setText(property.get());
                        textArea.textProperty().bindBidirectional(property);
                    }
                }
                else {
                    disableTextArea("Not a text file");
                    textArea.clear();
                }
            }
        });


        leftPane.getChildren().add(treeView);
        rightPane.getChildren().add(textArea);

        leftPane.setPosition(treeView, false, 0, 0, 0, 0);
        rightPane.setPosition(textArea, false, 0, 0, 0, 0);
        return root;
    }


    private static void disableTextArea(String reason) {
        textArea.setDisable(true);
        textAreaInfo.setText(reason);
        textAreaInfo.setVisible(true);
    }
    private static void enableTextArea() {
        textArea.setDisable(false);
        textAreaInfo.setVisible(false);
    }

}
