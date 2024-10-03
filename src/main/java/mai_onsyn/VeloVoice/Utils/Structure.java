package mai_onsyn.VeloVoice.Utils;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import mai_onsyn.AnimeFX.Frame.Module.Assistant.Cell;
import mai_onsyn.AnimeFX.Frame.Module.Assistant.DataCell;
import mai_onsyn.AnimeFX.Frame.Module.Assistant.Item;
import mai_onsyn.AnimeFX.Frame.Module.SmoothTreeView;
import mai_onsyn.VeloVoice.App.AppConfig;
import mai_onsyn.VeloVoice.Text.TextFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Structure<T> {
    private String name;
    private final T data;
    private final List<Structure<T>> children;

    public Structure(String name, T data) {
        this.name = name;
        this.data = data;
        this.children = null; // 不包含子节点
    }

    public Structure(String name) {
        this.name = name;
        this.data = null; // 不包含数据
        this.children = new ArrayList<>(); // 包含子节点列表
    }

    public void add(Structure<T> child) {
        if (children != null) {
            children.add(child);
        } else {
            throw new UnsupportedOperationException("Cannot add children to a data structure");
        }
    }

    public String getName() {
        return name;
    }

    public T getData() {
        return data;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Structure<T>> getChildren() {
        return children;
    }


    @Override
    public String toString() {
        return toString("");
    }

    private String toString(String indent) {
        StringBuilder sb = new StringBuilder();
        if (data != null) {
            if (data instanceof String) {
                sb.append(indent)
                        .append(name)
                        .append(" \"")
                        .append(data)
                        .append("\"\n");
            }
            else {
                sb.append(indent)
                        .append(name)
                        .append(" \"")
                        .append(data.getClass().getName())
                        .append("@")
                        .append(Integer.toHexString(data.hashCode()))
                        .append("\"\n");
            }
        } else {
            sb.append(indent)
                    .append(name)
                    .append("\n");
            if (children != null) {
                for (Structure<T> child : children) {
                    sb.append(child.toString(indent + "  "));
                }
            }
        }
        return sb.toString();
    }

    public static class Factory {

        public static void writeToTreeView(Structure<List<String>> structure, SmoothTreeView treeView, boolean unboxing) {
            Item<Cell> root = treeView.getRoot();

            Platform.runLater(() -> {
                if (unboxing) {
                    for (Structure<List<String>> items : structure.getChildren()) {
                        writeToTreeView(treeView, root.getData(), items);
                    }
                }
                else writeToTreeView(treeView, root.getData(), structure);
            });
        }

        private static void writeToTreeView(SmoothTreeView treeView, Cell parent, Structure<List<String>> colleague) {
            if (colleague.getData() == null) {
                Cell thisStage = treeView.getCellStyle().createFolderCell(colleague.getName(), treeView);
                treeView.addItem(parent, thisStage);

                for (Structure<List<String>> children : colleague.getChildren()) {
                    writeToTreeView(treeView, thisStage, children);
                }
            }
            else {
                StringBuilder sb = new StringBuilder();
                List<String> lines = colleague.getData();
                sb.append("  ");
                sb.append(lines.getFirst());
                for (int i = 1; i < lines.size(); i++) {
                    sb.append("\n\n    ");
                    sb.append(lines.get(i));
                }
                DataCell<SimpleStringProperty> fileCell = (DataCell<SimpleStringProperty>) treeView.getCellStyle().createFileCell(colleague.getName(), treeView);
                treeView.addItem(parent, fileCell);
                fileCell.getData().setValue(sb.toString());
            }
        }

        public static void saveToFile(Structure<List<String>> tree, File structureRoot, boolean ordinal, int counter, int superSize) {
            if (tree.getData() == null) {
                File childFile = new File(structureRoot, ordinal ? String.format("%s. %s", Util.padZero(counter, superSize), tree.getName()) : tree.getName());
                if (!childFile.exists()) {
                    if (!childFile.mkdirs()) throw new RuntimeException("Cannot make dirs: " + childFile.getAbsolutePath());
                }

                if (tree.getChildren().size() == 1) saveToFile(tree.getChildren().getFirst(), childFile, false, 0, 0);
                else {
                    for (int i = 0; i < tree.getChildren().size(); i++) {
                        saveToFile(tree.getChildren().get(i), childFile, AppConfig.isAppendOrdinal, i + 1, tree.getChildren().size());
                    }
                }
            }
            else {
                try (FileWriter fileWriter = new FileWriter(new File(structureRoot, ordinal ? String.format("%s. %s.txt", Util.padZero(counter, superSize), tree.getName()) : String.format("%s.txt", tree.getName())))) {
                    List<String> data = tree.getData();
                    fileWriter.write(data.getFirst());
                    for (int i = 1; i < data.size(); i++) {
                        fileWriter.write("\n\n    ");
                        fileWriter.write(data.get(i));
                    }
                    fileWriter.write("\n");

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        public static Structure<List<String>> of(Volume volume) {
            Structure<List<String>> root = new Structure<>(volume.getVolumeName());
            List<Chapter> chapters = volume.getChapters();
            for (Chapter chapter : chapters) {
                Structure<List<String>> child = new Structure<>(chapter.getTitle(), chapter.getContents());
                root.add(child);
            }
            return root;
        }

        public static Structure<List<String>> of(List<Volume> volumes) {
            Structure<List<String>> root = new Structure<>("Series");
            for (Volume volume : volumes) {
                Structure<List<String>> child = Structure.Factory.of(volume);
                root.add(child);
            }
            return root;
        }

        public static Structure<List<String>> ofItem(Item<Cell> rootItem) {
            Structure<List<String>> rootStructure;

            rootStructure = new Structure<>(rootItem.getData().getText());
            ofItem(rootStructure, rootItem);

            return rootStructure;
        }

        private static void ofItem(Structure<List<String>> structure, Item<Cell> item) {
            for (Item<Cell> childrenItem : item.getChildren()) {
                Structure<List<String>> childrenStructure;
                if (childrenItem.getData() instanceof DataCell<?> dataCell && dataCell.getData() instanceof SimpleStringProperty data) {
                    childrenStructure = new Structure<>(childrenItem.getData().getText().trim(), TextFactory.parseFromFormattedChapter(data.getValue()).getContents());
                }
                else {
                    childrenStructure = new Structure<>(childrenItem.getData().getText().trim());
                    ofItem(childrenStructure, childrenItem);
                }
                structure.getChildren().add(childrenStructure);
            }
        }
    }
}