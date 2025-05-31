package mai_onsyn.VeloVoice2.FrameFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;
import mai_onsyn.AnimeFX2.Module.AXButton;
import mai_onsyn.AnimeFX2.Module.AXTextArea;
import mai_onsyn.AnimeFX2.Module.AXTextField;
import mai_onsyn.AnimeFX2.Utls.AXButtonGroup;
import mai_onsyn.AnimeFX2.Utls.AXLangLabel;
import mai_onsyn.AnimeFX2.layout.*;
import mai_onsyn.VeloVoice2.App.Config;
import mai_onsyn.VeloVoice2.App.Runtime;

import static mai_onsyn.VeloVoice2.App.Constants.*;

public class LocalTXTHeaderEditor2 extends HDoubleSplitPane {

    private final JSONArray root;
    private final Config config = Runtime.sources.get("LocalTXT").getConfig();
    private final Config.ConfigBox itemBox = new Config.ConfigBox(0, UI_HEIGHT * 1.5);
    private final AXTextArea descriptionArea = new AXTextArea();
    private final Label titleLabel = new Label("Test");
    private final AXButtonGroup group = new AXButtonGroup();
    private final AXScrollPane rightScrollRoot = new AXScrollPane();

    private Item selectedItem;
    private Timeline titleOverTimeline = new Timeline();

    private static final AXTextInputPopup inputPopup = new AXTextInputPopup();

    public LocalTXTHeaderEditor2(JSONArray initialArray) {
        super(10, 0.15, 50, 50);
        root = initialArray;

//        Item rootTest = new Item("RootTest");
//        Item rootTest2 = new Item("RootTest2");
//        group.register(rootTest, rootTest2);
        group.addOnSelectChangedListener((o, ov, nv) -> {
            if (nv != null) {
                selectedItem = (Item) nv;
                rightScrollRoot.setContent(selectedItem.getContent());
                titleLabel.setText(selectedItem.getName());
                descriptionArea.setText(selectedItem.getDescription());
                config.setString("SelectedHeaderItem", selectedItem.getName());
            }
        });
//        group.selectButton(rootTest);
//        itemBox.addConfigItem(rootTest, rootTest2);

        buildInitFrame();

        descriptionArea.textProperty().addListener((o, ov, nv) -> {
            if (selectedItem != null) {
                selectedItem.setDescription(nv);
            }
        });

        AutoPane leftRoot = super.getLeft();
        AutoPane rightRoot = super.getRight();
        {
            VBox add_item = new VBox();
            ScrollPane itemScroll = new AXScrollPane();
            AutoPane addButtonBox = new AutoPane();
            AXButton addButton = new AXButton("Add");

            leftRoot.getChildren().add(add_item);
            leftRoot.setPosition(add_item, false, 0, 0, 0, 0);

            add_item.getChildren().addAll(addButtonBox, itemScroll);

            addButtonBox.getChildren().add(addButton);
            addButtonBox.setPosition(addButton, true, 0.1, 0.1, 0.1, 0.1);
            addButtonBox.setMinHeight(UI_HEIGHT * 1.5);
            addButtonBox.setMaxHeight(UI_HEIGHT * 1.5);
            leftRoot.setStyle("-fx-background-color: #80808040;");

            addButton.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                if (e.getButton() == MouseButton.PRIMARY) {
                    inputPopup.showOnCenter("Add", this.getScene().getWindow());
                    inputPopup.setOnTextAvailable((o, ov, nv) -> {
                        Item item = new Item(nv);
                        itemBox.addConfigItem(item);
                        group.register(item);
                        group.selectButton(item);
                    });
                }
            });

            itemScroll.setFitToWidth(true);
            itemScroll.setContent(itemBox);
        }

        {
            AXLangLabel desLabel = new AXLangLabel("Description");
            VDoubleSplitPane content_description = new VDoubleSplitPane(10, 0.8, 200, 50);


            rightRoot.getChildren().add(content_description);
            rightRoot.setPosition(content_description, false, 0, 0, 0, 0);

            AutoPane topRoot = content_description.getTop();
            AutoPane bottomRoot = content_description.getBottom();

            bottomRoot.getChildren().addAll(desLabel, descriptionArea);
            bottomRoot.setPosition(desLabel, AlignmentMode.TOP_LEFT, LocateMode.ABSOLUTE, 20, -20);
            bottomRoot.setPosition(descriptionArea, false, 20, 20, 0, 20);


            rightScrollRoot.setContent(selectedItem.getContent());
            rightScrollRoot.setFitToWidth(true);

            titleLabel.setFont(new Font(20));

            HBox titleBox = new HBox(UI_SPACING * 0.6);
            titleBox.setAlignment(Pos.CENTER_LEFT);

            AXButton renameButton = new AXButton("R");
            AXButton deleteButton = new AXButton("X");
            AutoPane.lockSize(renameButton, UI_HEIGHT * 0.6, UI_HEIGHT * 0.6);
            AutoPane.lockSize(deleteButton, UI_HEIGHT * 0.6, UI_HEIGHT * 0.6);
            renameButton.setOpacity(0);
            deleteButton.setOpacity(0);
            titleBox.getChildren().addAll(titleLabel, renameButton, deleteButton);

            titleBox.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
                titleOverTimeline.stop();
                titleOverTimeline = new Timeline(new KeyFrame(Duration.millis(200),
                        new KeyValue(renameButton.opacityProperty(), 1.0),
                        new KeyValue(deleteButton.opacityProperty(), 1.0)
                ));
                titleOverTimeline.play();
            });

            titleBox.addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
                titleOverTimeline.stop();
                titleOverTimeline = new Timeline(new KeyFrame(Duration.millis(200),
                        new KeyValue(renameButton.opacityProperty(), 0),
                        new KeyValue(deleteButton.opacityProperty(), 0)
                ));
                titleOverTimeline.play();
            });


            renameButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                if (event.getButton() == MouseButton.PRIMARY) {
                    inputPopup.showOnCenter("Rename", this.getScene().getWindow());
                    inputPopup.setOnTextAvailable((o, ov, nv) -> {
                        titleLabel.setText(nv);
                        selectedItem.setName(nv);
                    });
                }
            });

            group.addOnSelectChangedListener((o, ov, nv) -> {
                renameButton.setVisible(true);
                deleteButton.setVisible(true);
            });

            deleteButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                if (event.getButton() == MouseButton.PRIMARY) {
                    int targetIndex = itemBox.getChildren().indexOf(selectedItem);
                    itemBox.getChildren().remove(selectedItem);
                    group.remove(selectedItem);
                    if (!itemBox.getChildren().isEmpty()) {
                        group.selectButton((AXButton) itemBox.getChildren().get(Math.min(targetIndex, itemBox.getChildren().size() - 1)));
                    } else {
                        group.selectButton(null);
                        rightScrollRoot.setContent(null);
                        titleLabel.setText("Null");
                        renameButton.setVisible(false);
                        deleteButton.setVisible(false);
                    }

                }
            });


            topRoot.getChildren().addAll(titleBox, rightScrollRoot);
            topRoot.setPosition(titleBox, false, 20, 20, 20, 60);
            topRoot.setPosition(rightScrollRoot, false, 20, 20, 60, 20);
            topRoot.flipRelativeMode(titleBox, Motion.BOTTOM);

        }

//        selectedItem.getContent().getChildren().add(selectedItem.getContent().getChildren().size() - 1, new ItemPane("sub-1", "\\n+", "\\n"));
//        selectedItem.getContent().getChildren().add(selectedItem.getContent().getChildren().size() - 1, new ItemPane("sub-2", "\\n+", "\\n"));
//        selectedItem.add(new ItemPane("sub-1", "\\n+", "\\n"));
//        selectedItem.add(new ItemPane("sub-2", "\\n+", "\\n"));


        Runtime.cycleTasks.add(() -> {
            mkJson();
            config.setString("HeaderItems", JSONArray.toJSONString(root, false));
            //System.out.println(JSONObject.toJSONString(root, true));
        });
    }



    private static class Item extends AXButton {
        private final VBox content = new VBox();
        private String name;
        private String description = "";

        public Item(String text) {
            super(text);
            name = text;

            AutoPane addButtonPane = new AutoPane();
            addButtonPane.setMinHeight(UI_HEIGHT);
            addButtonPane.setMaxHeight(UI_HEIGHT);

            AXButton addButton = new AXButton("+Add");
            AutoPane.lockSize(addButton, UI_HEIGHT * 2, UI_HEIGHT);
            addButtonPane.getChildren().add(addButton);
            addButtonPane.setPosition(addButton, AlignmentMode.TOP_LEFT, LocateMode.RELATIVE, 0, 0);

            addButton.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                inputPopup.showOnCenter("Add", this.getScene().getWindow());
                inputPopup.setOnTextAvailable((o, ov, nv) -> {
                    content.getChildren().add(content.getChildren().size() - 1, new ItemPane(nv));
                });
            });
            content.getChildren().add(addButtonPane);
        }

        public VBox getContent() {
            return content;
        }

        public String getName() {
            return name;
        }

        public void add(ItemPane itemPane) {
            content.getChildren().add(content.getChildren().size() - 1, itemPane);
        }

        public void remove(ItemPane itemPane) {
            content.getChildren().remove(itemPane);
        }

        public void setName(String value) {
            name = value;
            super.getTextLabel().setText(value);
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    private static class ItemPane extends AutoPane {

        private static final Font font = new Font(15);

        private final AXTextField startField = new AXTextField();
        private final AXTextField endField = new AXTextField();
        private final Label nameLabel = new Label();

        private Timeline mouseOverTimeline = new Timeline();

        public ItemPane(String name) {
            this(name, "", "");
        }

        public ItemPane(String name, String start, String end) {
            super.setMinHeight(UI_HEIGHT * 3 + UI_SPACING);
            super.setMaxHeight(UI_HEIGHT * 3 + UI_SPACING);
            nameLabel.setText(name);
            nameLabel.setFont(font);
            HBox nameLabelBox = new HBox(nameLabel);
            nameLabelBox.setAlignment(Pos.CENTER_LEFT);
            nameLabelBox.setSpacing(UI_SPACING * 0.6);
            AutoPane nameLabelPane = new AutoPane();
            nameLabelPane.getChildren().addAll(nameLabelBox);
            nameLabelPane.setPosition(nameLabelBox, false, 0, 0, 0, 0);
            //nameLabelPane.setPosition(nameLabelBox, AlignmentMode.LEFT_CENTER, LocateMode.RELATIVE, 0, 0.5);

            HDoubleSplitPane editField = new HDoubleSplitPane(10, 0.5, UI_HEIGHT, UI_HEIGHT);
            editField.getLeft().getChildren().add(startField);
            editField.getRight().getChildren().add(endField);
            editField.getLeft().setPosition(startField, false, 0, 0, 0, 0);
            editField.getRight().setPosition(endField, false, 0, 0, 0, 0);
            AXLangLabel valueLabel = new AXLangLabel("Value");

            Rectangle split = new Rectangle(1, 1, Color.GRAY);

            super.getChildren().addAll(nameLabelPane, valueLabel, editField, split);

            super.setPosition(nameLabelPane, false, 0, 0, 0, UI_HEIGHT * 2 + UI_SPACING);
            super.setPosition(valueLabel, false, 0, 0, UI_HEIGHT + UI_SPACING, UI_HEIGHT);
            super.setPosition(editField, false, 50, 0, UI_HEIGHT + UI_SPACING, UI_HEIGHT);
            super.setPosition(split, false, 25, 25, UI_HEIGHT * 2.5 + UI_SPACING - 0.25, UI_HEIGHT * 0.5 - 0.25);

            AXButton renameButton = new AXButton("R");
            AXButton deleteButton = new AXButton("X");
            AutoPane.lockSize(renameButton, UI_HEIGHT * 0.6, UI_HEIGHT * 0.6);
            AutoPane.lockSize(deleteButton, UI_HEIGHT * 0.6, UI_HEIGHT * 0.6);
            renameButton.setOpacity(0);
            deleteButton.setOpacity(0);
            nameLabelBox.getChildren().addAll(renameButton, deleteButton);

            nameLabelBox.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
                mouseOverTimeline.stop();
                mouseOverTimeline = new Timeline(new KeyFrame(Duration.millis(200),
                        new KeyValue(renameButton.opacityProperty(), 1.0),
                        new KeyValue(deleteButton.opacityProperty(), 1.0)
                ));
                mouseOverTimeline.play();
            });

            nameLabelBox.addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
                mouseOverTimeline.stop();
                mouseOverTimeline = new Timeline(new KeyFrame(Duration.millis(200),
                        new KeyValue(renameButton.opacityProperty(), 0),
                        new KeyValue(deleteButton.opacityProperty(), 0)
                ));
                mouseOverTimeline.play();
            });


            renameButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                if (event.getButton() == MouseButton.PRIMARY) {
                    inputPopup.showOnCenter("Rename", this.getScene().getWindow());
                    inputPopup.setOnTextAvailable((o, ov, nv) -> {
                        nameLabel.setText(nv);
                    });
                }
            });

            deleteButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                if (event.getButton() == MouseButton.PRIMARY) {
                    VBox parent = (VBox) this.getParent();
                    parent.getChildren().remove(this);
                }
            });



//            nameLabel.setStyle("-fx-background-color: #ff20ff80");
//            nameLabelPane.setStyle("-fx-background-color: #8020ff80");
//            valueLabel.setStyle("-fx-background-color: #20ff8080");
//            super.setStyle("-fx-background-color: #00000020");

            startField.setText(start);
            endField.setText(end);
        }

        public String getStartValue() {
            return startField.getText();
        }

        public String getEndValue() {
            return endField.getText();
        }

        public String getName() {
            return nameLabel.getText();
        }
    }

    private void buildInitFrame() {

        String selectedName = Runtime.config.getConfig("LocalTXT").getString("SelectedHeaderItem");
        for (Object o : root) {
            if (o instanceof JSONObject itemJson) {
                String name = itemJson.getString("name");
                Item item = new Item(name);
                item.setDescription(itemJson.getString("description"));
                itemBox.addConfigItem(item);
                group.register(item);

                for (Object o2 : itemJson.getJSONArray("content")) {
                    if (o2 instanceof JSONObject se) {
                        item.add(new ItemPane(se.getString("name"), se.getString("start"), se.getString("end")));
                    }
                }

                if (selectedName.equals(name)) {
                    group.selectButton(item);
                    descriptionArea.setText(item.getDescription());
                }
            }
        }
    }

    private void mkJson() {
        root.clear();
        for (Node childNode : itemBox.getChildren()) {
            Item item = (Item) childNode;
            JSONObject itemRoot = new JSONObject();
            itemRoot.put("name", item.getName());
            itemRoot.put("description", item.getDescription());

            JSONArray itemArray = new JSONArray();
            for (Node child : item.content.getChildren()) {
                if (child instanceof ItemPane itemPane) {
                    JSONObject itemContent = new JSONObject();
                    itemContent.put("name", itemPane.getName());
                    itemContent.put("start", itemPane.getStartValue());
                    itemContent.put("end", itemPane.getEndValue());

                    itemArray.add(itemContent);
                }
            }

            itemRoot.put("content", itemArray);
            root.add(itemRoot);
        }
    }
}
