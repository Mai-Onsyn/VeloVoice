package mai_onsyn.AnimeFX.Utls;

public class AXDataTreeItem<T> extends AXTreeItem {

    private T data;
    private final AXTreeviewCopyRule<T> rule;

    public AXDataTreeItem(String name, T data, AXTreeviewCopyRule<T> rule) {
        super(name);
        this.rule = rule;
        setData(data);
    }

    public T getData() {
        return data;
    }

    public T getCopiedData() {
        return rule.copy(data);
    }

    public void setData(T data) {
        this.data = data;
    }

    public interface AXTreeviewCopyRule<T> {
        T copy(T src);
    }
}
