package mai_onsyn.VeloVoice.NetWork.LoadTarget;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Pair;
import mai_onsyn.AnimeFX.I18N;
import mai_onsyn.AnimeFX.Module.AXButton;
import mai_onsyn.AnimeFX.Module.AXSwitch;
import mai_onsyn.AnimeFX.Module.AXTreeView;
import mai_onsyn.AnimeFX.Utls.AXDataTreeItem;
import mai_onsyn.AnimeFX.Utls.AXTreeItem;
import mai_onsyn.AnimeFX.layout.AutoPane;
import mai_onsyn.VeloVoice.App.WindowManager;
import mai_onsyn.VeloVoice.App.Config;
import mai_onsyn.VeloVoice.App.ResourceManager;
import mai_onsyn.VeloVoice.FrameFactory.LocalTXTHeaderEditor;
import mai_onsyn.VeloVoice.Text.TextUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static mai_onsyn.VeloVoice.App.Constants.UI_HEIGHT;
import static mai_onsyn.VeloVoice.App.Constants.UI_SPACING;
import static mai_onsyn.VeloVoice.App.Runtime.themeManager;
import static mai_onsyn.VeloVoice.FrameFactory.FrameThemes.BUTTON;

public class LocalTXT extends Source {

    private static final Logger log = LogManager.getLogger(LocalTXT.class);

    private Stage headerStage;

    public LocalTXT() {
        super();
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(JSONObject.parseObject("{\"name\":\"轻小说文库-全集\",\"description\":\"用于解析轻小说文库下载的全集txt文件\",\"content\":[{\"name\":\"Chapter\",\"start\":\"\\\\n[^\\\\s]\",\"end\":\"\\\\n\"}]}"));
        jsonArray.add(JSONObject.parseObject("{\"name\":\"轻小说文库-分卷\",\"description\":\"用于解析轻小说文库下载的分卷txt文件\",\"content\":[{\"name\":\"Chapter\",\"start\":\"\\\\n\\\\s\\\\s[^\\\\s]\",\"end\":\"\\\\n\"}]}"));

        super.config.registerBoolean("ParseHtmlCharacters", true);
        super.config.registerBoolean("ParseStructures", false);
        super.config.registerString("HeaderItems", JSONObject.toJSONString(jsonArray));
        super.config.registerString("SelectedHeaderItem", "轻小说文库-全集");
        super.config.registerBoolean("IgnoreEmptyParsedFile", true);
        super.config.registerBoolean("MarkTitle", true);
    }

    @Override
    protected String getHomePage() {
        return "";
    }

    @Override
    protected String getDetails() {
        return "Local";
    }

    @Override
    public String getNameSpace() {
        return "source.local_txt.name";
    }

    @Override
    protected Image getIcon() {
        return ResourceManager.pc;
    }

    @Override
    public Config.ConfigBox mkConfigFrame() {
        Config.ConfigBox localTXTBox = new Config.ConfigBox(UI_SPACING, UI_HEIGHT);

        headerStage = new Stage();

        Config.ConfigItem parseHtmlCharacters = config.genSwitchItem("ParseHtmlCharacters");
        Config.ConfigItem parseStructures = config.genSwitchItem("ParseStructures");
        Config.ConfigItem ignoreEmptyParsedFile = config.genSwitchItem("IgnoreEmptyParsedFile");
        Config.ConfigItem markTitle = config.genSwitchItem("MarkTitle");

        AXButton editButton = new AXButton(); {
            editButton.setTheme(BUTTON);
            themeManager.register(editButton);

            ImageView editIcon = new ImageView(ResourceManager.edit);
            editIcon.setFitWidth(UI_HEIGHT * 0.8);
            editIcon.setFitHeight(UI_HEIGHT * 0.8);
            editButton.getChildren().add(editIcon);
            editButton.setPosition(editIcon, AutoPane.AlignmentMode.CENTER, AutoPane.LocateMode.RELATIVE, 0.5, 0.5);
        }

        Config.ConfigItem rulesEdit = new Config.ConfigItem("ParseRules", editButton, true, 0.4, 0.475);

        parseHtmlCharacters.setI18NKey("source.local_txt.label.html_char");
        parseStructures.setI18NKey("source.local_txt.label.parse_structures");
        ignoreEmptyParsedFile.setI18NKey("source.local_txt.label.ignore_empty_unit");
        markTitle.setI18NKey("source.local_txt.label.mark_title");
        rulesEdit.setI18NKey("source.local_txt.label.parse_rules");
        I18N.registerComponents(parseHtmlCharacters, parseStructures, ignoreEmptyParsedFile, markTitle, rulesEdit);

        localTXTBox.addConfigItem(parseHtmlCharacters, parseStructures, ignoreEmptyParsedFile, markTitle, rulesEdit);

        {
            headerStage.setTitle("Edit LocalTXT Parse Rules");
            LocalTXTHeaderEditor headerItemsEditor = new LocalTXTHeaderEditor(JSONArray.parseArray(config.getString("HeaderItems")));
            headerStage.setScene(new Scene(headerItemsEditor, 800, 600));
            headerItemsEditor.requestFocus();
            headerStage.getIcons().add(ResourceManager.icon);
            WindowManager.register(headerStage);
            I18N.addOnChangedAction(() -> headerStage.setTitle(I18N.getCurrentValue("stage.local_txt.title")));
        }
        editButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                if (headerStage.isShowing()) headerStage.toFront();
                else headerStage.show();
            }
        });

        ((AXSwitch) parseStructures.getContent().getChildren().getFirst()).stateProperty().addListener((o, ov, nv) -> {
            if (nv) {
                ignoreEmptyParsedFile.setDisable(false);
                markTitle.setDisable(false);
                rulesEdit.setDisable(false);
            }
            else {
                ignoreEmptyParsedFile.setDisable(true);
                markTitle.setDisable(true);
                rulesEdit.setDisable(true);
            }
        });
        if (!config.getBoolean("ParseStructures")) {
            ignoreEmptyParsedFile.setDisable(true);
            markTitle.setDisable(true);
            rulesEdit.setDisable(true);
        }

        return localTXTBox;
    }

    @Override
    public void process(String uri, AXTreeItem root) {
        AXTreeView<?> attribution = root.getAttribution();
        File folder = new File(uri);

        if (!folder.exists()) {
            log.error(I18N.getCurrentValue("log.local_txt.error.folder_not_exist"), uri);
            return;
        }

        JSONArray jsonArray = JSONArray.parseArray(super.config.getString("HeaderItems"));
        String selectedItem = super.config.getString("SelectedHeaderItem");
        JSONArray items = null;
        for (Object o : jsonArray) {
            if (((JSONObject) o).getString("name").equals(selectedItem)) {
                items = ((JSONObject) o).getJSONArray("content");
                break;
            }
        }
        if (items == null) {
            log.error("No parse rule selected!");
            return;
        }

        // 提取配置项
        boolean parseStructures = super.config.getBoolean("ParseStructures");
        boolean ignoreEmptyParsedFile = super.config.getBoolean("IgnoreEmptyParsedFile");
        boolean markTitle = super.config.getBoolean("MarkTitle");

        List<Pair<String, String>> regexIdentifiers = buildRegexIdentifiers(items);

        // 传递参数
        addToTree(folder, root, attribution, parseStructures, ignoreEmptyParsedFile, markTitle, regexIdentifiers);

//        log.info(I18N.getCurrentValue("log.local_txt.info.processed"), uri);
    }


    void addToTree(File fileOrFolder, AXTreeItem parent, AXTreeView<?> attribution,
                           boolean parseStructures, boolean ignoreEmptyParsedFile,
                           boolean markTitle, List<Pair<String, String>> identifiers) {
        String name = fileOrFolder.getName();
        if (fileOrFolder.isFile() && name.endsWith(".txt")) {
            SimpleStringProperty data = (SimpleStringProperty) attribution.getDataCreator().create();
            String content = TextUtil.load(fileOrFolder);
            AXTreeItem fileItem;
            String itemName = name.substring(0, name.length() - 4);
            if (parseStructures) {
                fileItem = attribution.createFolderItem(itemName);
                String s = clearString(content);
                List<Header> headers = parseLevelsIndex(s, identifiers);
                if (!checkLevels(headers)) return;
                mergeLevelsTree(s, fileItem, headers, 0, 0, identifiers.size(), attribution, ignoreEmptyParsedFile, markTitle);
            } else {
                data.set(content);
                fileItem = attribution.createFileItem(itemName, data);
            }
            Platform.runLater(() -> parent.add(fileItem));
        } else if (fileOrFolder.isDirectory()) {
            AXTreeItem folderItem = attribution.createFolderItem(name);
            Platform.runLater(() -> parent.add(folderItem));

            File[] files = fileOrFolder.listFiles();
            if (files != null) {
                for (File file : files) {
                    addToTree(file, folderItem, attribution, parseStructures, ignoreEmptyParsedFile, markTitle, identifiers);
                }
            }
        }
    }


    String clearString(String content) {
        String[] lines = content.split("\n");
        StringBuilder sb = new StringBuilder("\n");
        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                sb.append(line).append("\n");
            }
        }
        return sb.toString();
    }

    int mergeLevelsTree(String s, AXTreeItem parent, List<Header> headers,
                                int index, int level, int textLevel, AXTreeView<?> attribution,
                                boolean ignoreEmptyParsedFile, boolean markTitle) {
        int i = index;
        while (i < headers.size()) {
            Header header = headers.get(i);

            String name = s.substring(header.start, header.end).trim();
            if (header.level == textLevel) {
                String fileContent;
                if (++i == headers.size()) {
                    fileContent = s.substring(header.end);
                } else {
                    int start = header.end;
                    int end = headers.get(i).start;
                    fileContent = s.substring(Math.min(start, end), Math.max(start, end));
                }
                if (fileContent.trim().isEmpty() && ignoreEmptyParsedFile) continue;
                AXDataTreeItem<?> fileItem = attribution.createFileItem(name, new SimpleStringProperty(
                        markTitle ? name + "\n" + fileContent : fileContent));
                parent.add(fileItem);
                continue;
            }

            if (header.level > level) {
                AXTreeItem child = attribution.createFolderItem(name);
                parent.add(child);
                i = mergeLevelsTree(s, child, headers, ++i, header.level, textLevel, attribution, ignoreEmptyParsedFile, markTitle);
            } else {
                i++;
                break;
            }
        }
        return i;
    }

    List<Header> parseLevelsIndex(String content, List<Pair<String, String>> identifiers) {
        List<Header> result = new ArrayList<>();
        int level = 1;

        for (Pair<String, String> item : identifiers) {
            Matcher ms = Pattern.compile(item.getKey()).matcher(content);
            Matcher me = Pattern.compile(item.getValue()).matcher(content);

            int i = 0;
            while (ms.find(i)) {
                int start = ms.start();
                i = ms.end();
                if (me.find(ms.end())) {
                    int end = me.end();
                    result.add(new Header(level, start, end));
                }
            }
            level++;
        }

        result.sort(Comparator.comparingInt(a -> a.start));
        return result;
    }

    boolean checkLevels(List<Header> headers) {
        int lastLevel = 0;
        for (Header header : headers) {
            if (header.level > lastLevel + 1) {
                log.error("Level jumped at {}", header.start);
                return false;
            }
            lastLevel = header.level;
        }
        return true;
    }

    private List<Pair<String, String>> buildRegexIdentifiers(JSONArray items) {
        List<Pair<String, String>> list = new ArrayList<>();
        for (Object o : items) {
            JSONObject item = (JSONObject) o;
            list.add(new Pair<>(item.getString("start"), item.getString("end")));
        }
        return list;
    }

    public record Header(int level, int start, int end) {

        @Override
        public int level() {
            return level;
        }

        @Override
        public int start() {
            return start;
        }

        @Override
        public int end() {
            return end;
        }

        @Override
        public String toString() {
            return "Header{" + "level=" + level + ", start=" + start + ", end=" + end + '}';
        }
    }
}
