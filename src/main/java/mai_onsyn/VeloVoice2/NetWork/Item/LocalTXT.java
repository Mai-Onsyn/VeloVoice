package mai_onsyn.VeloVoice2.NetWork.Item;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.image.Image;
import mai_onsyn.AnimeFX2.Module.AXTreeView;
import mai_onsyn.AnimeFX2.Utls.AXTreeItem;
import mai_onsyn.VeloVoice2.Text.TextUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class LocalTXT extends Source {

    public LocalTXT() {
        super();
        super.config.registerBoolean("ParseHtmlCharacters", false);
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

        addToTree(folder, root, attribution);
    }

    private void addToTree(File fileOrFolder, AXTreeItem parent, AXTreeView<?> attribution) {
        if (fileOrFolder.isFile() && fileOrFolder.getName().endsWith(".txt")) {
            // 如果是文件且为 .txt 文件，直接添加到树
            SimpleStringProperty data = (SimpleStringProperty) attribution.getDataCreator().create();
            AXTreeItem fileItem = attribution.createFileItem(fileOrFolder.getName(), data);
            parent.add(fileItem);

            // 设置文件内容
            data.set(TextUtil.load(fileOrFolder));
        } else if (fileOrFolder.isDirectory()) {
            // 如果是文件夹，添加到树并递归处理子文件/文件夹
            AXTreeItem folderItem = attribution.createFolderItem(fileOrFolder.getName());
            parent.add(folderItem);

            File[] files = fileOrFolder.listFiles();
            if (files != null) {
                for (File file : files) {
                    addToTree(file, folderItem, attribution);
                }
            }
        }
    }
}
