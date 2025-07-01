package mai_onsyn.VeloVoice2.FrameFactory;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;
import mai_onsyn.AnimeFX2.I18N;
import mai_onsyn.AnimeFX2.Module.AXButton;
import mai_onsyn.AnimeFX2.Module.AXChoiceBox;
import mai_onsyn.AnimeFX2.Module.AXTextField;
import mai_onsyn.AnimeFX2.Utls.AXButtonGroup;
import mai_onsyn.AnimeFX2.Utls.AXTreeItem;
import mai_onsyn.AnimeFX2.layout.AXContextPane;
import mai_onsyn.AnimeFX2.layout.AutoPane;
import mai_onsyn.VeloVoice2.App.Config;
import mai_onsyn.VeloVoice2.App.Constants;
import mai_onsyn.VeloVoice2.NetWork.Item.Source;
import mai_onsyn.VeloVoice2.Text.ExecuteItem;
import mai_onsyn.VeloVoice2.Text.TextUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static mai_onsyn.VeloVoice2.App.Constants.*;
import static mai_onsyn.VeloVoice2.App.Runtime.*;
import static mai_onsyn.VeloVoice2.FrameFactory.MainFactory.treeView;

public class TextProcessFactory {


    private static final Logger log = LogManager.getLogger(TextProcessFactory.class);

    static Config.ConfigBox mkTextProcessArea() {
        Config.ConfigBox textProcessArea = new Config.ConfigBox(UI_SPACING, UI_HEIGHT);
        {
            Config.ConfigBox loadBox = getLoadBox();
            Config.ConfigBox saveBox = getSaveBox();
            Config.ConfigBox ordinalBox = getOrdinalBox();

            textProcessArea.widthProperty().addListener((o, ov, nv) -> {
                loadBox.setPrefWidth(nv.doubleValue());
                saveBox.setPrefWidth(nv.doubleValue());
                ordinalBox.setPrefWidth(nv.doubleValue());
            });

            AXButton modeSwitchButton = new AXButton();
            {
                modeSwitchButton.setTheme(FrameThemes.TRANSPARENT_BUTTON);
                themeManager.register(modeSwitchButton);

                AXContextPane modeSwitchPane = new AXContextPane();
                modeSwitchPane.setTheme(FrameThemes.CONTEXT_PANE);

                AXButton loadItem = modeSwitchPane.createItem();
                AXButton saveItem = modeSwitchPane.createItem();
                AXButton ordinalItem = modeSwitchPane.createItem();

                AXButtonGroup buttonGroup = new AXButtonGroup(loadItem, saveItem, ordinalItem);
                {
                    loadItem.setI18NKey("frame.main.item.label.text_process.title.load");
                    I18N.registerComponent(loadItem);
                    loadItem.setUserData(loadBox);

                    saveItem.setI18NKey("frame.main.item.label.text_process.title.save");
                    I18N.registerComponent(saveItem);
                    saveItem.setUserData(saveBox);

                    ordinalItem.setI18NKey("frame.main.item.label.text_process.title.ordinal");
                    I18N.registerComponent(ordinalItem);
                    ordinalItem.setUserData(ordinalBox);
                }
                buttonGroup.setFreeStyle(FrameThemes.TRANSPARENT_BUTTON);
                buttonGroup.setSelectedStyle(FrameThemes.SELECTED_BUTTON);

                buttonGroup.addOnSelectChangedListener((o, ov, nv) -> {
                    modeSwitchButton.setText(nv.getText());

                    textProcessArea.getChildren().removeLast();
                    textProcessArea.getChildren().add((Config.ConfigBox) nv.getUserData());
                });

                //init modeSwitchButton after language initialized
                Platform.runLater(() -> buttonGroup.selectButton(loadItem));

                modeSwitchButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                    if (event.getButton() == MouseButton.PRIMARY) {
                        modeSwitchPane.show(modeSwitchButton.getScene().getWindow(), event.getScreenX(), event.getScreenY());
                    }
                });
            }

            textProcessArea.addConfigItem(modeSwitchButton);
            textProcessArea.getChildren().add(loadBox);
        }

        return textProcessArea;
    }

    private static Config.ConfigBox getLoadBox() {
        Config.ConfigBox loadBox = new Config.ConfigBox(UI_SPACING, UI_HEIGHT);
        loadBox.setAlignment(Pos.TOP_RIGHT);

        List<String> loadList = new ArrayList<>();
        {
            for (Map.Entry<String, Source> entry : sources.entrySet()) {
                loadList.add(entry.getKey());
            }
        }
        Config.ConfigItem sourceItem = textConfig.genChooseStringItem("LoadSource", loadList);
        sourceItem.setI18NKey("frame.main.item.label.load.load_source");
        I18N.registerComponent(sourceItem);

        Config.ConfigBox sourceConfigBox = new Config.ConfigBox(UI_SPACING, UI_HEIGHT);
        {
            for (Map.Entry<String, Source> sourceEntry : sources.entrySet()) {
                Source source = sourceEntry.getValue();
                Config.ConfigBox configBox = source.mkConfigFrame();
                configBox.setUserData(sourceEntry.getKey());
                sourceConfigBox.getChildren().add(configBox);
            }

            AXChoiceBox choiceBox = (AXChoiceBox) sourceItem.getChildren().getLast();
            choiceBox.getButtonGroup().addOnSelectChangedListener((o, ov, nv) -> {
                String rule = nv.getUserData().toString();
                for (Node node : sourceConfigBox.getChildren()) {
                    node.setVisible(node.getUserData().toString().equals(rule));
                }
            });
        }

        Config.ConfigItem uriItem = textConfig.genInputStringItem("LoadUri", "frame.main.text_field.text_load_uri");
        uriItem.setI18NKey("frame.main.item.label.load.text_load_uri");
        ((AXTextField) uriItem.getContent()).setChildrenI18NKeys(Constants.I18nKeyMaps.CONTEXT_MENU);
        I18N.registerComponent(uriItem);

        AXButton startButton = new AXButton("Load");
        startButton.setMaxSize(100, UI_SPACING + UI_HEIGHT);
        startButton.setMinSize(100, UI_SPACING + UI_HEIGHT);

        startButton.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                Source source = sources.get(textConfig.getString("LoadSource"));
                source.process(textConfig.getString("LoadUri"), treeView.getRoot());
            }
        });

        loadBox.addConfigItem(sourceItem, uriItem);
        loadBox.getChildren().addAll(sourceConfigBox, startButton);

        return loadBox;
    }

    private static Config.ConfigBox getSaveBox() {
        Config.ConfigBox saveBox = new Config.ConfigBox(UI_SPACING, UI_HEIGHT);
        saveBox.setAlignment(Pos.TOP_RIGHT);

        Config.ConfigItem saveMethodItem = textConfig.genChooseStringItem("SaveMethod", List.of("TXTTree", "Epub"));
        {
            saveMethodItem.setI18NKey("frame.main.item.label.load.save_method");
            saveMethodItem.setChildrenI18NKeys(Map.of(
                    "TXTTree", "frame.main.choose_box.save_method.txt_tree",
                    "Epub", "frame.main.choose_box.save_method.epub"
            ));
            I18N.registerComponent(saveMethodItem);
        }
        Platform.runLater(() -> ((AXChoiceBox) saveMethodItem.getContent()).flushChosenButton());

        Config.ConfigBox txtTreeBox = new Config.ConfigBox(UI_SPACING, UI_HEIGHT);
        {
            Config.ConfigItem encodingItem = textConfig.genChooseStringItem("TXTSaveEncoding", List.of("UTF-8", "ISO-8859-1", "US-ASCII", "UTF-16", "UTF-16BE", "UTF-16LE", "UTF-32", "UTF-32BE", "UTF-32LE"));

            txtTreeBox.addConfigItem(encodingItem);
        }

        Config.ConfigBox epubBBox = new Config.ConfigBox(UI_SPACING, UI_HEIGHT); {
            //wait for update
            AutoPane autoPane = new AutoPane();
            autoPane.getChildren().add(new Label("Stay tuned for updates!"));
            epubBBox.addConfigItem(autoPane);
        }
        ((AXChoiceBox) saveMethodItem.getContent()).getButtonGroup().addOnSelectChangedListener((o, ov, nv) -> {
            String oldKey = ov.getUserData().toString();
            String newKey = nv.getUserData().toString();
            log.debug("Switch from " + oldKey + " to " + newKey);
            switch (oldKey) {
                case "TXTTree" -> saveBox.getChildren().remove(txtTreeBox);
                case "Epub" -> saveBox.getChildren().remove(epubBBox);
            }
            switch (newKey) {
                case "TXTTree" -> saveBox.getChildren().add(saveBox.getChildren().size() - 1, txtTreeBox);
                case "Epub" -> saveBox.getChildren().add(saveBox.getChildren().size() - 1, epubBBox);
            }
        });

        AXButton saveButton = new AXButton(); {
            saveButton.setMaxSize(100, UI_HEIGHT + UI_SPACING);
            saveButton.setMinSize(100, UI_HEIGHT + UI_SPACING);
            saveButton.setI18NKey("frame.main.item.label.text_process.title.save");
            I18N.registerComponent(saveButton);


            saveButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                if (textConfig.getString("SaveMethod").equals("TXTTree")) {
                    DirectoryChooser dc = new DirectoryChooser();
                    File folder = dc.showDialog(saveButton.getScene().getWindow());
                    if (folder != null) {
                        AXTreeItem item = treeView.getGroup().getData(treeView.getGroup().getSelectedButton());
                        if (item == null) log.error("No target selected");
                        else Thread.ofVirtual().name("Text-Save-Thread").start(() -> TextUtil.save(item, folder));
                    }
                } else {
                    log.info("Epub is not implemented yet, please wait for the next update");
                }
            });
        }


        saveBox.addConfigItem(saveMethodItem);
        saveBox.getChildren().addAll(switch (textConfig.getString("SaveMethod")) {
            case "TXTTree" -> txtTreeBox;
            case "Epub" -> epubBBox;
            default -> new AutoPane();
        }, saveButton);

        return saveBox;
    }

    private static Config.ConfigBox getOrdinalBox() {
        Config.ConfigBox ordinalBox = new Config.ConfigBox(UI_SPACING, UI_HEIGHT);
        ordinalBox.setAlignment(Pos.TOP_RIGHT);

        Config.ConfigItem formatItem = textConfig.genInputStringItem("OrdinalFormat", "");
        Config.ConfigItem initNumItem = textConfig.genSwitchItem("OrdinalStartByZero");

        AXButton carryButton = new AXButton("Run"); {
            carryButton.setMaxSize(100, UI_HEIGHT + UI_SPACING);
            carryButton.setMinSize(100, UI_HEIGHT + UI_SPACING);
            carryButton.setI18NKey("frame.main.item.label.text_process.ordinal.run");
            I18N.registerComponent(carryButton);
        }
        carryButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                AXTreeItem item = treeView.getGroup().getData(treeView.getGroup().getSelectedButton());
                if (item == null) log.error("No target selected");
                else ExecuteItem.addOrdinal(item);
            }
        });

        ordinalBox.addConfigItem(formatItem, initNumItem);
        ordinalBox.getChildren().add(carryButton);

        return ordinalBox;
    }

}
