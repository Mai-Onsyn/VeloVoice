package mai_onsyn.VeloVoice2.FrameFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import mai_onsyn.AnimeFX2.Module.AXButton;
import mai_onsyn.AnimeFX2.Module.AXTextField;
import mai_onsyn.AnimeFX2.Utls.AXButtonGroup;
import mai_onsyn.AnimeFX2.layout.AXScrollPane;
import mai_onsyn.AnimeFX2.layout.AXTextInputPopup;
import mai_onsyn.AnimeFX2.layout.AutoPane;
import mai_onsyn.AnimeFX2.layout.HDoubleSplitPane;
import mai_onsyn.VeloVoice2.App.Config;

import java.util.Objects;

import static mai_onsyn.VeloVoice2.App.Constants.*;
import static mai_onsyn.VeloVoice2.App.Runtime.sources;

//[{"name":"test","content":[{"start":"\\n[^ ]a","end":"\n"}]},{"name":"node","content":[{"start":"testStart","end":"testEnd"},{"start":"tests","end":"fa"}]}]
public class LocalTXTHeadersEditor extends HDoubleSplitPane {

    private final JSONArray rules;

    private final Config.ConfigBox leftBox;
    private final AutoPane rSuperBox = new AutoPane();
    private final AXButtonGroup buttonGroup = new AXButtonGroup();
    private int selectedIndex = 0;

    private final Config config = sources.get("LocalTXT").getConfig();
    private final AXTextInputPopup textInputPopup = new AXTextInputPopup();

    public LocalTXTHeadersEditor(JSONArray initialRules) {
        super(10, 0.3, 50, 50);
        rules = initialRules;
        leftBox = new Config.ConfigBox(0, UI_HEIGHT);

        AutoPane left = super.getLeft();
        AutoPane right = super.getRight();

        ScrollPane lScroll = new AXScrollPane(leftBox);
        ScrollPane rScroll = new AXScrollPane(rSuperBox);

        lScroll.setFitToWidth(true);
        rScroll.setFitToWidth(true);

        left.getChildren().add(lScroll);
        right.getChildren().add(rScroll);
        left.setPosition(lScroll, false, 0, 0, 0, 0);
        right.setPosition(rScroll, false, 0, 0, 0, 0);

        buttonGroup.setOnSelectedChanged((o, ov, nv) -> {
            if (nv != null) {
                int index = -1;

                for (int i = 0; i < leftBox.getChildren().size(); i++) {
                    if (((AutoPane) leftBox.getChildren().get(i)).getChildren().getFirst() == nv) {
                        index = i;
                        break;
                    }
                }

                for (int i = 0; i < rSuperBox.getChildren().size(); i++) {
                    rSuperBox.getChildren().get(i).setVisible(i == index);
                }

                config.setString("SelectedHeaderItem", nv.getTextLabel().getText());
                selectedIndex = index;
            }
        });

        buildRulesFrame();


        Thread.ofVirtual().start(() -> {
            while (true) {
                try {
                    Thread.sleep(500);
                    buildRulesJson();

//                    System.out.println(config.getString("HeaderItems"));
//                    System.out.println(JSONObject.toJSONString(rules));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

        });
    }

    private void buildRulesFrame() {
        String selectedName = config.getString("SelectedHeaderItem");
        for (int i = 0; i < rules.size(); i++) {
            JSONObject rule = (JSONObject) rules.get(i);
            JSONArray items = rule.getJSONArray("content");

            String name = rule.getString("name");
            AXButton menuButton = new AXButton(name);
            leftBox.addConfigItem(mkMenuItemBox(menuButton));



            Config.ConfigBox rightBox = new Config.ConfigBox(UI_SPACING, UI_HEIGHT);

            for (int j = 0; j < items.size(); j++) {
                JSONObject item = (JSONObject) items.get(j);

                AutoPane content = mkSEBox(item);
                Config.ConfigItem itemBox = new Config.ConfigItem(String.format("Level %d", j), content, false, 50);
                rightBox.addConfigItem(itemBox);
            }
            rSuperBox.getChildren().add(rightBox);
            rSuperBox.setPosition(rightBox, false, 50, 50, 0, 0);

            if (Objects.equals(name, selectedName)) {
                Platform.runLater(() -> buttonGroup.selectButton(menuButton));
                selectedIndex = i;
            }
        }
    }

    private AutoPane mkMenuItemBox(AXButton button) {
        AutoPane menuItemBox = new AutoPane();
        buttonGroup.register(button);

        AXButton deleteButton = new AXButton("-");
        AXButton addButton = new AXButton("+");

        deleteButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                if (selectedIndex != -1) {
                    rSuperBox.getChildren().remove(leftBox.getChildren().indexOf(menuItemBox));
                    leftBox.getChildren().remove(menuItemBox);
                }
            }
        });
        addButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                if (selectedIndex != -1) {
                    textInputPopup.clear();
                    textInputPopup.showOnCenter("New", this.getScene().getWindow());
                    textInputPopup.setOnTextAvailable((o, ov, nv) -> {
                        AutoPane element = mkMenuItemBox(new AXButton(nv));
                        leftBox.addConfigItem(leftBox.getChildren().indexOf(menuItemBox) + 1, element);

                        JSONObject sampleObject = JSONObject.parseObject("""
                                {
                                    "start": "\\\\n",
                                    "end": "\\\\n",
                                }
                                """);

                        Config.ConfigBox rightBox = new Config.ConfigBox(UI_SPACING, UI_HEIGHT);
                        Config.ConfigItem configItem = new Config.ConfigItem(String.format("Level %d", 0), mkSEBox(sampleObject), false, 50);
                        rightBox.addConfigItem(configItem);
                        rSuperBox.getChildren().add(leftBox.getChildren().indexOf(menuItemBox) + 1, rightBox);
                        rSuperBox.setPosition(rightBox, false, 50, 50, 0, 0);
                    });
                }
            }
        });


        menuItemBox.getChildren().addAll(button, addButton, deleteButton);
        menuItemBox.setPosition(button, false, 0, UI_HEIGHT * 2, 0, 0);
        menuItemBox.setPosition(addButton, false, UI_HEIGHT * 2, UI_HEIGHT, 0, 0);
        menuItemBox.setPosition(deleteButton, false, UI_HEIGHT, 0, 0, 0);
        menuItemBox.flipRelativeMode(deleteButton, Motion.LEFT);
        menuItemBox.flipRelativeMode(addButton, Motion.LEFT);
        return menuItemBox;
    }

    private AutoPane mkSEBox(JSONObject item) {
        AutoPane seBox = new AutoPane();

        AXTextField st = new AXTextField();
        AXTextField et = new AXTextField();

        st.setText(item.getString("start"));
        et.setText(item.getString("end"));

        //System.out.println(Arrays.toString(st.getText().toCharArray()));

        seBox.getChildren().addAll(st, et);

        seBox.setPosition(st, true, 0, 0.55, 0, 0);
        seBox.setPosition(et, true, 0.55, 0, 0, 0);

        return seBox;
    }

    private void buildRulesJson() {
        if (leftBox.getChildren().size() != rSuperBox.getChildren().size()) return;

        rules.clear();

        for (int i = 0; i < leftBox.getChildren().size(); i++) {
            String name = ((AXButton) ((AutoPane) leftBox.getChildren().get(i)).getChildren().getFirst()).getTextLabel().getText();
            Config.ConfigBox rightBox = (Config.ConfigBox) rSuperBox.getChildren().get(i);

            JSONObject item = new JSONObject();
            JSONArray content = parseObject(rightBox);

            item.put("name", name);
            item.put("content", content);

            rules.add(item);
        }

        config.setString("HeaderItems", JSONObject.toJSONString(rules));
    }

    private static JSONArray parseObject(Config.ConfigBox rightBox) {
        JSONArray content = new JSONArray();

        for (Node child : rightBox.getChildren()) {
            Config.ConfigItem configItem = (Config.ConfigItem) child;
            AutoPane contentPane = (AutoPane) configItem.getChildren().getLast();

            AXTextField startField = (AXTextField) contentPane.getChildren().getFirst();
            AXTextField endField = (AXTextField) contentPane.getChildren().get(1);
            JSONObject element = new JSONObject();
            element.put("start", startField.getText());
            element.put("end", endField.getText());
            content.add(element);
        }
        return content;
    }
}
