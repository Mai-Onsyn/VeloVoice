package mai_onsyn.AnimeFX2;

import java.util.ArrayList;
import java.util.List;

public class ThemeManager {

    private final List<AutoUpdatable> nodes;

    public ThemeManager() {
        this.nodes = new ArrayList<>();
    }

    public void register(AutoUpdatable node) {
        nodes.add(node);
    }

    public void register(AutoUpdatable... node) {
        nodes.addAll(List.of(node));
    }

    public void flushAll() {
        for (AutoUpdatable autoUpdatable : nodes) {
            autoUpdatable.update();
        }
    }
}
