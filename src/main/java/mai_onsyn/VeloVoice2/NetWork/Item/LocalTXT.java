package mai_onsyn.VeloVoice2.NetWork.Item;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.util.Pair;
import mai_onsyn.AnimeFX2.Module.AXTreeView;
import mai_onsyn.AnimeFX2.Utls.AXDataTreeItem;
import mai_onsyn.AnimeFX2.Utls.AXTreeItem;
import mai_onsyn.VeloVoice2.Text.TextUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LocalTXT extends Source {

    private final List<Pair<String, String>> identifiers = new ArrayList<>();


    public LocalTXT() {
        super();
        super.config.registerBoolean("ParseHtmlCharacters", false);
        super.config.registerBoolean("ParseStructures", false);

        JSONArray jsonArray = new JSONArray();
        jsonArray.add(JSONObject.parseObject("""
                {
                    "name": "test",
                    "content": [
                        {
                            "start": "\\n[^ ]",
                            "end": "\\n"
                        }
                    ]
                }
                """));
        super.config.registerString("HeaderItems", JSONObject.toJSONString(jsonArray));
        super.config.registerString("SelectedHeaderItem", "test");
        super.config.registerBoolean("IgnoreEmptyParsedFile", true);
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
            throw new IllegalArgumentException("Invalid folder path: " + uri);
        }

        updateRegexes();

        addToTree(folder, root, attribution);
    }

    private void addToTree(File fileOrFolder, AXTreeItem parent, AXTreeView<?> attribution) {
        String name = fileOrFolder.getName();
        if (fileOrFolder.isFile() && name.endsWith(".txt")) {
            // 如果是文件且为 .txt 文件，直接添加到树
            SimpleStringProperty data = (SimpleStringProperty) attribution.getDataCreator().create();
            String content = TextUtil.load(fileOrFolder);
            AXTreeItem fileItem;
            if (super.config.getBoolean("ParseStructures")) {
                fileItem = attribution.createFolderItem(name);
                String s = clearString(content);
                parseLevels(s, fileItem, parseLevelsIndex(s), attribution);
            }
            else {
                data.set(content);
                fileItem = attribution.createFileItem(name, data);
            }
            parent.add(fileItem);

            // 设置文件内容
        } else if (fileOrFolder.isDirectory()) {
            // 如果是文件夹，添加到树并递归处理子文件/文件夹
            AXTreeItem folderItem = attribution.createFolderItem(name);
            parent.add(folderItem);

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
//        headers.forEach(e -> {
//            System.out.print(content.substring(e.start(), e.end()).trim().replaceAll("\\n+", ""));
//            System.out.println(e);
//        });
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
                AXDataTreeItem<?> fileItem = attribution.createFileItem(name, new SimpleStringProperty(fileContent));
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
