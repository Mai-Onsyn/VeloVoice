package mai_onsyn.AnimeFX.Frame.Module.Assistant;

import java.util.ArrayList;
import java.util.List;

public class Item<T> {
    private final T data;
    private final List<Item<T>> children;

    public Item(T data) {
        this.data = data;
        this.children = new ArrayList<>();
    }

    public void add(Item<T> child) {
        children.add(child);
    }


    @SafeVarargs
    public final void addAll(Item<T>... child) {
        children.addAll(List.of(child));
    }

    public T getData() {
        return data;
    }

    public List<Item<T>> getChildren() {
        return children;
    }

    public Item<T> lookup(T key) {
        if (this.data.equals(key)) return this;

        for (Item<T> child : children) {
            Item<T> result = child.lookup(key);
            if (result != null) {
                return result;
            }
        }

        return null;
    }

    public static void delete(Item<?> root, Item<?> children) {
        for (Item<?> child : root.getChildren()) {
            if (child == children) {
                root.getChildren().remove(child);
                return;
            }
            else delete(child, children);
        }
    }
}
