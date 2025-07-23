package mai_onsyn.VeloVoice.FrameFactory;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import mai_onsyn.AnimeFX.I18N;
import mai_onsyn.AnimeFX.Module.AXButton;
import mai_onsyn.AnimeFX.Module.AXChoiceBox;
import mai_onsyn.AnimeFX.Module.AXTextField;
import mai_onsyn.AnimeFX.Utls.AXButtonGroup;
import mai_onsyn.AnimeFX.Utls.AXDatableButtonGroup;
import mai_onsyn.AnimeFX.Utls.AXTreeItem;
import mai_onsyn.AnimeFX.layout.AXContextPane;
import mai_onsyn.AnimeFX.layout.AutoPane;
import mai_onsyn.VeloVoice.App.Config;
import mai_onsyn.VeloVoice.App.Constants;
import mai_onsyn.VeloVoice.NetWork.LoadTarget.Source;
import mai_onsyn.VeloVoice.Text.ExecuteItem;
import mai_onsyn.VeloVoice.Text.TextUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static mai_onsyn.VeloVoice.App.Constants.*;
import static mai_onsyn.VeloVoice.App.Runtime.*;
import static mai_onsyn.VeloVoice.FrameFactory.MainFactory.treeView;
import static mai_onsyn.VeloVoice.FrameFactory.FrameThemes.*;

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
                modeSwitchButton.setTheme(TITLE_BUTTON);
                themeManager.register(modeSwitchButton);

                AXContextPane modeSwitchPane = new AXContextPane();
                modeSwitchPane.setTheme(CONTEXT_PANE);
                themeManager.register(modeSwitchPane);


                AXButton loadItem = modeSwitchPane.createItem();
                AXButton saveItem = modeSwitchPane.createItem();
                AXButton ordinalItem = modeSwitchPane.createItem();

                AtomicReference<AXButton> mode = new AtomicReference<>(loadItem);

                AXButtonGroup buttonGroup = new AXButtonGroup(loadItem, saveItem, ordinalItem);
                {
                    loadItem.setI18NKey("main.text.load.title");
                    I18N.registerComponent(loadItem);
                    loadItem.setUserData(loadBox);

                    saveItem.setI18NKey("main.text.save.title");
                    I18N.registerComponent(saveItem);
                    saveItem.setUserData(saveBox);

                    ordinalItem.setI18NKey("main.text.ordinal.title");
                    I18N.registerComponent(ordinalItem);
                    ordinalItem.setUserData(ordinalBox);

                    buttonGroup.setFreeStyle(TRANSPARENT_BUTTON);
                    buttonGroup.setSelectedStyle(SELECTED_BUTTON);
                }

                buttonGroup.addOnSelectChangedListener((o, ov, nv) -> {
                    modeSwitchButton.setText(nv.getText());

                    textProcessArea.getChildren().removeLast();
                    textProcessArea.getChildren().add((Config.ConfigBox) nv.getUserData());

                    mode.set(nv);
                });

                //init modeSwitchButton after language initialized
                Platform.runLater(() -> buttonGroup.selectButton(loadItem));

                modeSwitchButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                    if (event.getButton() == MouseButton.PRIMARY) {
                        modeSwitchPane.show(modeSwitchButton.getScene().getWindow(), event.getScreenX(), event.getScreenY());
                    }
                });

                I18N.addOnChangedAction(() -> modeSwitchButton.setText(I18N.getCurrentValue(mode.get().getI18NKey())));
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
        sourceItem.setI18NKey("main.text.load.label.source");
        sourceItem.setChildrenI18NKeys(I18nKeyMaps.SOURCES);
        I18N.registerComponent(sourceItem);

        Config.ConfigBox sourceConfigBox = new Config.ConfigBox(UI_SPACING, UI_HEIGHT);
        {
            List<Config.ConfigBox> configFrames = new ArrayList<>();
            for (Map.Entry<String, Source> sourceEntry : sources.entrySet()) {
                Source source = sourceEntry.getValue();
                Config.ConfigBox configBox = source.mkConfigFrame();
                configBox.setUserData(sourceEntry.getKey());
                configFrames.add(configBox);
                if (sourceEntry.getKey().equals(textConfig.getString("LoadSource"))) sourceConfigBox.getChildren().add(configBox);
            }

            AXChoiceBox choiceBox = (AXChoiceBox) sourceItem.getChildren().getLast();
            choiceBox.getButtonGroup().addOnSelectChangedListener((o, ov, nv) -> {
                String rule = nv.getUserData().toString();
                sourceConfigBox.getChildren().clear();
                for (Node node : configFrames) {
                    //node.setVisible(node.getUserData().toString().equals(rule));
                    if (node.getUserData().toString().equals(rule)) {
                        sourceConfigBox.getChildren().add(node);
                        break;
                    }
                }
            });

            choiceBox.getButtonGroup().getButtonList().forEach(button -> {
                sources.get(button.getUserData().toString()).drawItemButton(button);
            });
        }

        Config.ConfigItem uriItem = textConfig.genInputStringItem("LoadUri", "main.text.load.input.uri");
        uriItem.setI18NKey("main.text.load.label.uri");
        ((AXTextField) uriItem.getContent()).setChildrenI18NKeys(I18nKeyMaps.CONTEXT);
        I18N.registerComponent(uriItem);

        AXButton startButton = new AXButton("Load");
        startButton.setMaxSize(100, UI_SPACING + UI_HEIGHT);
        startButton.setMinSize(100, UI_SPACING + UI_HEIGHT);
        startButton.setTheme(BUTTON);
        themeManager.register(startButton);
        startButton.setI18NKey("main.text.load.title");
        I18N.registerComponent(startButton);

        isTextLoadRunning.addListener((o, ov, nv) -> Platform.runLater(() -> {
            if (nv) {
                startButton.setText(I18N.getCurrentValue("main.general.button.stop"));
                startButton.setI18NKey("main.general.button.stop");
            } else {
                startButton.setText(I18N.getCurrentValue("main.text.load.title"));
                startButton.setI18NKey("main.text.load.title");
            }
        }));

        AtomicReference<Thread> textLoadThread = new AtomicReference<>();
        startButton.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                if (isTextLoadRunning.get()) {
                    textLoadThread.get().interrupt();
                    isTextLoadRunning.set(false);
                } else {
                    isTextLoadRunning.set(true);

                    Source source = sources.get(textConfig.getString("LoadSource"));
                    AXDatableButtonGroup<AXTreeItem> group = treeView.getGroup();

                    AXTreeItem target = group.getSelectedButton() == null ? treeView.getRoot() : group.getData(group.getSelectedButton());

                    textLoadThread.set(Thread.ofVirtual().name("Text-Load-Thread").start(() -> {
                        try {
                            source.process(textConfig.getString("LoadUri"), target);
                        } catch (InterruptedException ie) {
                            log.info("Text load interrupted");
                        } catch (Exception ex) {
                            log.error("Text load failed: {}", ex.getMessage());
                        } finally {
                            isTextLoadRunning.set(false);
                        }
                    }));
                }
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
            saveMethodItem.setI18NKey("main.text.save.label.method");
            saveMethodItem.setChildrenI18NKeys(Map.of(
                    "TXTTree", "main.text.save.choice.method.text_tree",
                    "Epub", "main.text.save.choice.method.epub"
            ));
            I18N.registerComponent(saveMethodItem);
        }
        Platform.runLater(() -> ((AXChoiceBox) saveMethodItem.getContent()).flushChosenButton());

        Config.ConfigBox txtTreeBox = new Config.ConfigBox(UI_SPACING, UI_HEIGHT);
        {
            Config.ConfigItem encodingItem = textConfig.genChooseStringItem("TXTSaveEncoding", List.of("UTF-8", "ISO-8859-1", "US-ASCII", "UTF-16", "UTF-16BE", "UTF-16LE", "UTF-32", "UTF-32BE", "UTF-32LE"));

            encodingItem.setI18NKey("main.text.save.text_tree.label.encoding");
            I18N.registerComponent(encodingItem);

            txtTreeBox.addConfigItem(encodingItem);
        }

        Config.ConfigBox epubBBox = new Config.ConfigBox(UI_SPACING, UI_HEIGHT); {
            //wait for update
            AutoPane autoPane = new AutoPane();
            Label label = new Label("Stay tuned for updates!");
            label.setTextFill(Color.GRAY);
            autoPane.getChildren().add(label);
            epubBBox.addConfigItem(autoPane);
        }
        ((AXChoiceBox) saveMethodItem.getContent()).getButtonGroup().addOnSelectChangedListener((o, ov, nv) -> {
            String oldKey = ov.getUserData().toString();
            String newKey = nv.getUserData().toString();
            //log.debug("Switch from " + oldKey + " to " + newKey);
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
            saveButton.setI18NKey("main.text.save.title");
            I18N.registerComponent(saveButton);
            saveButton.setTheme(BUTTON);
            themeManager.register(saveButton);


            saveButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                if (textConfig.getString("SaveMethod").equals("TXTTree")) {
                    DirectoryChooser dc = new DirectoryChooser();
                    File folder = dc.showDialog(saveButton.getScene().getWindow());
                    if (folder != null) {
                        AXTreeItem item = treeView.getGroup().getData(treeView.getGroup().getSelectedButton());
                        if (item == null) log.error(I18N.getCurrentValue("log.text_process_factory.error.no_select"));
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

        Config.ConfigItem formatItem = textConfig.genInputStringItem("OrdinalFormat", "main.text.ordinal.input.format");
        Config.ConfigItem initNumItem = textConfig.genSwitchItem("OrdinalStartByZero");

        formatItem.setI18NKey("main.text.ordinal.label.format");
        initNumItem.setI18NKey("main.text.ordinal.label.start_by_zero");
        formatItem.setChildrenI18NKeys(I18nKeyMaps.CONTEXT);
        I18N.registerComponents(formatItem, initNumItem);

        AXButton carryButton = new AXButton("Run"); {
            carryButton.setMaxSize(100, UI_HEIGHT + UI_SPACING);
            carryButton.setMinSize(100, UI_HEIGHT + UI_SPACING);
            carryButton.setI18NKey("main.text.ordinal.button.run");
            I18N.registerComponent(carryButton);
            carryButton.setTheme(BUTTON);
            themeManager.register(carryButton);
        }
        carryButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                AXTreeItem item = treeView.getGroup().getData(treeView.getGroup().getSelectedButton());
                if (item == null) log.error(I18N.getCurrentValue("log.text_process_factory.error.no_select"));
                else ExecuteItem.addOrdinal(item);
            }
        });

        ordinalBox.addConfigItem(formatItem, initNumItem);
        ordinalBox.getChildren().add(carryButton);

        return ordinalBox;
    }

}
