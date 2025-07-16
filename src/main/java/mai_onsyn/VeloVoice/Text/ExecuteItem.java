package mai_onsyn.VeloVoice.Text;


import javafx.beans.property.SimpleStringProperty;
import mai_onsyn.AnimeFX.Utls.AXDataTreeItem;
import mai_onsyn.AnimeFX.Utls.AXTreeItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static mai_onsyn.VeloVoice.App.Runtime.textConfig;

public record ExecuteItem(String text, File folder, String name) {

    public static List<ExecuteItem> parseStructure(AXTreeItem item, File folder) {
        List<ExecuteItem> result = new ArrayList<>();

        if (item instanceof AXDataTreeItem<?> d) parseStructureRecursion(result, d, folder, d.getHeadName());
        else parseStructureRecursion(result, item, folder, null);

        return result;
    }

    private static void parseStructureRecursion(List<ExecuteItem> target, AXTreeItem item, File folder, String name) {
        if (item instanceof AXDataTreeItem<?> d) {
            if (d.getData() instanceof SimpleStringProperty s) {
                String content = s.get();

                target.add(new ExecuteItem(content, folder, String.format("%s", name)));
            }
        }
        else {
            for (AXTreeItem i : item.getChildrenAsItem()) {
                if (i instanceof AXDataTreeItem<?>) parseStructureRecursion(target, i, folder, i.getHeadName());
                else parseStructureRecursion(target, i, new File(folder, i.getHeadName()), null);
            }
        }
    }


    public static void addOrdinal(AXTreeItem root) throws IllegalArgumentException {

        Boolean ordinalStartByZero = textConfig.getBoolean("OrdinalStartByZero");
        addOrdinalRecursion(root, textConfig.getString("OrdinalFormat"), new Ordinal(ordinalStartByZero), ordinalStartByZero);

    }

    private static void addOrdinalRecursion(AXTreeItem parent, String format, Ordinal ordinal, boolean startByZero) throws IllegalArgumentException {

        parent.getChildrenAsItem().forEach(i -> {

            List<String> checked = checkFormat(format);
            if (checked == null) throw new IllegalArgumentException("Invalid format: " + format);
            else if (Objects.equals(checked.getFirst(), "int")) i.rename(String.format(format, ordinal.next(), i.getHeadName()));
            else if (Objects.equals(checked.getFirst(), "string")) i.rename(String.format(format, i.getHeadName(), ordinal.next()));

            addOrdinalRecursion(i, format, new Ordinal(startByZero), startByZero);
        });
    }

    private static List<String> checkFormat(String format) {
        List<String> paramTypes = new ArrayList<>();
        int i = 0;
        int len = format.length();

        while (i < len) {
            if (format.charAt(i) == '%') {
                i++; // 跳过 '%'
                if (i >= len) break; // 防止越界

                // 处理转义 "%%"
                if (format.charAt(i) == '%') {
                    i++;
                    continue;
                }

                // 提取完整的格式说明符（直到遇到转换字符）
                while (i < len && !Character.isLetter(format.charAt(i))) {
                    i++;
                }
                if (i >= len) break; // 未找到转换字符

                char conversion = format.charAt(i);
                i++; // 跳过转换字符

                // 根据转换字符判断类型
                if (conversion == 'd') {
                    paramTypes.add("int");
                } else if (conversion == 's') {
                    paramTypes.add("string");
                } else {
                    paramTypes.add("other"); // 其他类型
                }
            } else {
                i++;
            }
        }

        // 验证参数：必须恰好包含一个int、一个string，且无其他类型
        if (paramTypes.size() == 2 &&
                paramTypes.contains("int") &&
                paramTypes.contains("string") &&
                !paramTypes.contains("other")) {
            return paramTypes;
        }
        return null; // 不符合要求
    }


}