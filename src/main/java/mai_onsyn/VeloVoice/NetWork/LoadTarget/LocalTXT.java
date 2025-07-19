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
import mai_onsyn.VeloVoice.App.Resource;
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
    private final List<Pair<String, String>> identifiers = new ArrayList<>();

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
    protected String getNameSpace() {
        return "source.localTXT";
    }

    @Override
    protected Image getIcon() {
        return null;
    }

    @Override
    public void process(String uri, AXTreeItem root) {
        AXTreeView<?> attribution = root.getAttribution();
        File folder = new File(uri);

        if (!folder.exists()) {
            log.error(I18N.getCurrentValue("log.local_txt.error.folder_not_exist"), uri);
            return;
        }

        updateRegexes();

        addToTree(folder, root, attribution);
        log.info(I18N.getCurrentValue("log.local_txt.info.processed"), uri);
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

            ImageView editIcon = new ImageView(Resource.edit);
            editIcon.setFitWidth(UI_HEIGHT * 0.8);
            editIcon.setFitHeight(UI_HEIGHT * 0.8);
            editButton.getChildren().add(editIcon);
            editButton.setPosition(editIcon, AutoPane.AlignmentMode.CENTER, AutoPane.LocateMode.RELATIVE, 0.5, 0.5);
        }

        Config.ConfigItem rulesEdit = new Config.ConfigItem("ParseRules", editButton, true, 0.4, 0.475);

        parseHtmlCharacters.setI18NKey("custom.local_txt.label.html_char");
        parseStructures.setI18NKey("custom.local_txt.label.parse_structures");
        ignoreEmptyParsedFile.setI18NKey("custom.local_txt.label.ignore_empty_unit");
        markTitle.setI18NKey("custom.local_txt.label.mark_title");
        rulesEdit.setI18NKey("custom.local_txt.label.parse_rules");
        I18N.registerComponents(parseHtmlCharacters, parseStructures, ignoreEmptyParsedFile, markTitle, rulesEdit);

        localTXTBox.addConfigItem(parseHtmlCharacters, parseStructures, ignoreEmptyParsedFile, markTitle, rulesEdit);

        {
            headerStage.setTitle("Edit LocalTXT Parse Rules");
            LocalTXTHeaderEditor headerItemsEditor = new LocalTXTHeaderEditor(JSONArray.parseArray(config.getString("HeaderItems")));
            headerStage.setScene(new Scene(headerItemsEditor, 800, 600));
            headerItemsEditor.requestFocus();
            headerStage.getIcons().add(Resource.icon);
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

    private void addToTree(File fileOrFolder, AXTreeItem parent, AXTreeView<?> attribution) {
        String name = fileOrFolder.getName();
        if (fileOrFolder.isFile() && name.endsWith(".txt")) {
            // 如果是文件且为 .txt 文件，直接添加到树
            SimpleStringProperty data = (SimpleStringProperty) attribution.getDataCreator().create();
            String content = TextUtil.load(fileOrFolder);
            AXTreeItem fileItem;
            String itemName = name.substring(0, name.length() - 4);
            if (super.config.getBoolean("ParseStructures")) {
                fileItem = attribution.createFolderItem(itemName);
                String s = clearString(content);
                parseLevels(s, fileItem, parseLevelsIndex(s), attribution);
            }
            else {
                data.set(content);
                fileItem = attribution.createFileItem(itemName, data);
            }
            Platform.runLater(() -> parent.add(fileItem));

            // 设置文件内容
        } else if (fileOrFolder.isDirectory()) {
            // 如果是文件夹，添加到树并递归处理子文件/文件夹
            AXTreeItem folderItem = attribution.createFolderItem(name);
            Platform.runLater(() -> parent.add(folderItem));

            File[] files = fileOrFolder.listFiles();
            if (files != null) {
                for (File file : files) {
                    addToTree(file, folderItem, attribution);
                }
            }
        }
    }

    private String clearString(String content) {
        String[] lines = content.split("\n");
        StringBuilder sb = new StringBuilder("\n");
        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                sb.append(line).append("\n");
            }
        }
        return sb.toString();
    }

    private void parseLevels(String content, AXTreeItem root, List<Header> headers, AXTreeView<?> attribution) {
        if (!checkLevels(headers)) {
            return;
        }

        mergeLevelsTree(content, root, headers, 0, 0, identifiers.size(), attribution);
    }

    private int mergeLevelsTree(String s, AXTreeItem parent, List<Header> headers, int index, int level, int textLevel, AXTreeView<?> attribution) {
        int i = index;
        while (i < headers.size()) {
            Header header = headers.get(i);

            String name = s.substring(header.start, header.end).trim();
            if (header.level == textLevel) {
                String fileContent = null;
                if (++i == headers.size()) {
                    fileContent = s.substring(header.end);
                } else {
                    int start = header.end;
                    int end = headers.get(i).start;
                    fileContent = s.substring(Math.min(start, end), Math.max(start, end));
                }
                if (fileContent.trim().isEmpty() && super.config.getBoolean("IgnoreEmptyParsedFile")) continue;
                AXDataTreeItem<?> fileItem = attribution.createFileItem(name, new SimpleStringProperty(
                        super.config.getBoolean("MarkTitle") ? name + "\n" + fileContent : fileContent));
                parent.add(fileItem);
                continue;
            }

            if (header.level > level) {
                AXTreeItem child = attribution.createFolderItem(name);
                parent.add(child);
                i = mergeLevelsTree(s, child, headers, ++i, header.level, textLevel, attribution);
            } else {
                i++;
                break;
            }
        }
        return i;
    }

    private boolean checkLevels(List<Header> headers) {
        int lastLevel = 0;
        for (Header header : headers) {
            if (header.level > lastLevel + 1) {
                throw new IllegalArgumentException("Level jumped at " + header.start);
            }
            lastLevel = header.level;
        }
        return true;
    }

    private List<Header> parseLevelsIndex(String content) {
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

    public void updateRegexes() {
        identifiers.clear();

        JSONArray jsonArray = JSONArray.parseArray(super.config.getString("HeaderItems"));
        JSONArray items = null;
        for (Object o : jsonArray) {
            if (((JSONObject) o).getString("name").equals(config.getString("SelectedHeaderItem"))) {
                items = ((JSONObject) o).getJSONArray("content");
                break;
            }
        }
        if (items == null) return;

        for (Object o : items) {
            JSONObject item = (JSONObject) o;
            identifiers.add(new Pair<>(item.getString("start"), item.getString("end")));
        }

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
