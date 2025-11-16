package mai_onsyn.VeloVoice.FrameFactory;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import mai_onsyn.AnimeFX.I18N;
import mai_onsyn.AnimeFX.Module.AXButton;
import mai_onsyn.AnimeFX.Module.AXChoiceBox;
import mai_onsyn.AnimeFX.Module.AXTextField;
import mai_onsyn.AnimeFX.Utls.*;
import mai_onsyn.AnimeFX.layout.AXContextPane;
import mai_onsyn.AnimeFX.layout.AutoPane;
import mai_onsyn.VeloVoice.App.Config;
import mai_onsyn.VeloVoice.NetWork.LoadTarget.Source;
import mai_onsyn.VeloVoice.Text.EpubBook;
import mai_onsyn.VeloVoice.Text.ExecuteItem;
import mai_onsyn.VeloVoice.Text.TextUtil;
import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Metadata;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
                loadText(textLoadThread, sources.get(textConfig.getString("LoadSource")), textConfig.getString("LoadUri"));
            }
        });

        loadBox.addConfigItem(sourceItem, uriItem);
        loadBox.getChildren().addAll(sourceConfigBox, startButton);

        return loadBox;
    }

    static void loadText(AtomicReference<Thread> textLoadThread, Source source, String loadUri) {
        if (isTextLoadRunning.get()) {
            textLoadThread.get().interrupt();
            isTextLoadRunning.set(false);
        } else {
            isTextLoadRunning.set(true);

//            Source source = sources.get(textConfig.getString("LoadSource"));
            AXDatableButtonGroup<AXTreeItem> group = treeView.getGroup();

            AXTreeItem target = (group.getSelectedButton() == null || group.getData(group.getSelectedButton()) instanceof AXDataTreeItem<?>) ? treeView.getRoot() : group.getData(group.getSelectedButton());

            textLoadThread.set(Thread.ofVirtual().name("Text-Load-Thread").start(() -> {
//                String loadUri = textConfig.getString("LoadUri");

                try {
                    if (!new File(loadUri).exists()) {
                        log.error(I18N.getCurrentValue("log.text_process_factory.error.no_such_file_or_directory"), loadUri);
                        return;
                    }
                    source.process(loadUri, target);
                    log.info(I18N.getCurrentValue("log.text_process_factory.info.load_success"), loadUri);
                } catch (InterruptedException ie) {
                    log.info(I18N.getCurrentValue("log.text_process_factory.info.load_interrupted"));
                } catch (Exception ex) {
                    log.error(I18N.getCurrentValue("log.text_process_factory.error.load_failed"), loadUri, ex);
                } finally {
                    isTextLoadRunning.set(false);
                }
            }));
        }
    }

    private static Config.ConfigBox getSaveBox() {
        Config.ConfigBox saveBox = new Config.ConfigBox(UI_SPACING, UI_HEIGHT);
        saveBox.setAlignment(Pos.TOP_RIGHT);

        Config.ConfigItem saveMethod = textConfig.genChooseStringItem("SaveMethod", List.of("TXTTree", "Epub")); {
            saveMethod.setI18NKey("main.text.save.label.method");
            saveMethod.setChildrenI18NKeys(Map.of(
                    "TXTTree", "main.text.save.choice.method.text_tree",
                    "Epub", "main.text.save.choice.method.epub"
            ));
            I18N.registerComponent(saveMethod);
            Platform.runLater(() -> ((AXChoiceBox) saveMethod.getContent()).flushChosenButton());
        }
        Config.ConfigItem saveSelected = textConfig.genSwitchItem("SaveSelected"); {
            saveSelected.setI18NKey("main.text.save.label.save_selected");
            I18N.registerComponent(saveSelected);
        }

        Config.ConfigBox txtTreeBox = new Config.ConfigBox(UI_SPACING, UI_HEIGHT); {
            Config.ConfigItem encodingItem = textConfig.genChooseStringItem("TXTSaveEncoding", List.of("UTF-8", "ISO-8859-1", "US-ASCII", "UTF-16", "UTF-16BE", "UTF-16LE", "UTF-32", "UTF-32BE", "UTF-32LE"));

            encodingItem.setI18NKey("main.text.save.text_tree.label.encoding");
            I18N.registerComponent(encodingItem);

            txtTreeBox.addConfigItem(encodingItem);
        }

        Config.ConfigBox epubBBox = new Config.ConfigBox(UI_SPACING, UI_HEIGHT); {
            Config.ConfigItem epubTitle = textConfig.genInputStringItem("EpubTitle", "main.text.save.epub.input.title");
            Config.ConfigItem epubAuthor = textConfig.genInputStringItem("EpubAuthor", "main.text.save.epub.input.author");
            Config.ConfigItem epubCover = textConfig.genInputStringItem("EpubCover", "main.text.save.epub.input.cover");
            epubTitle.setI18NKey("main.text.save.epub.label.title");
            epubAuthor.setI18NKey("main.text.save.epub.label.author");
            epubCover.setI18NKey("main.text.save.epub.label.cover");
            I18N.registerComponents(epubTitle, epubAuthor, epubCover);

            epubBBox.addConfigItem(epubTitle, epubAuthor, epubCover);
        }
        ((AXChoiceBox) saveMethod.getContent()).getButtonGroup().addOnSelectChangedListener((o, ov, nv) -> {
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
                        if (textConfig.getBoolean("SaveSelected")) {
                            AXTreeItem item = treeView.getGroup().getData(treeView.getGroup().getSelectedButton());
                            if (item == null) log.error(I18N.getCurrentValue("log.text_process_factory.error.no_select"));
                            else Thread.ofVirtual().name("Text-Save-Thread").start(() -> TextUtil.save(item, folder));
                        } else {
                            Thread.ofVirtual().name("Text-Save-Thread").start(() -> TextUtil.save(treeView.getRoot(), folder));
                        }
                    }
                } else if (textConfig.getString("SaveMethod").equals("Epub")) {
                    FileChooser fc = new FileChooser();
                    fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Epub", "*.epub"));
                    File file = fc.showSaveDialog(saveButton.getScene().getWindow());
                    try {
                        EpubBook book = new EpubBook();

                        if (textConfig.getBoolean("SaveSelected")) {
                            AXTreeItem item = treeView.getGroup().getData(treeView.getGroup().getSelectedButton());
                            if (item == null) {
                                log.error(I18N.getCurrentValue("log.text_process_factory.error.no_select"));
                                return;
                            }
                            else book.setBookTree(item);
                        }
                        else {
                            book.setBookTree(treeView.getRoot());
                        }
                        Metadata metadata = book.getMetadata();
                        metadata.addTitle(textConfig.getString("EpubTitle"));
                        metadata.addAuthor(new Author(textConfig.getString("EpubAuthor")));
                        File epubCoverFile = new File(textConfig.getString("EpubCover"));
                        if (epubCoverFile.exists()) {
                            nl.siegmann.epublib.domain.Resource cover = new nl.siegmann.epublib.domain.Resource(new FileInputStream(epubCoverFile), "cover.png");
                            book.setCoverImage(cover);
                            book.addResource(cover);
                        } else {
                            log.warn("Epub cover image not found");
                        }

                        Thread.ofVirtual().name("Epub-Save-Thread").start(() -> {
                            try {
                                book.write(file.getAbsolutePath());
                                log.info(I18N.getCurrentValue("log.text_process_factory.info.save_success"), file.getAbsolutePath());
                            } catch (IOException e) {
                                log.error(I18N.getCurrentValue("log.text_process_factory.error.save_failed"), file.getAbsolutePath(), e);
                            }
                        });
                    } catch (IOException e) {
                        log.error(I18N.getCurrentValue("log.text_process_factory.error.save_failed"), file.getAbsolutePath(), e);
                    }
                }
            });
        }


        saveBox.addConfigItem(saveSelected, saveMethod);
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
