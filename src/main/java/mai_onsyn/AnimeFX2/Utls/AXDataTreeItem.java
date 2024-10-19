package mai_onsyn.AnimeFX2.Utls;

public class AXDataTreeItem<T> extends AXTreeItem {

    private T data;

    public AXDataTreeItem(String name, T data) {
        super(name);
        setData(data);
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
