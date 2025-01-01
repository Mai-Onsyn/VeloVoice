package mai_onsyn.VeloVoice2.NetWork.Item;

import javafx.scene.image.Image;
import mai_onsyn.AnimeFX2.Module.AXButton;
import mai_onsyn.AnimeFX2.Utls.AXTreeItem;
import mai_onsyn.VeloVoice2.App.Config;

public abstract class Source {

    protected Config config = new Config();

    public Config getConfig() {
        return config;
    }

    protected abstract String getHomePage();

    protected abstract String getDetails();

    protected abstract String getNameSpace();

    protected abstract Image getIcon();

    public abstract void process(String uri, AXTreeItem root);

    public AXButton getItemButton() {
        return null;
    }
}
