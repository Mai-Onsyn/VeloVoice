package mai_onsyn.VeloVoice.NetWork.LoadTarget;

import javafx.scene.image.Image;
import mai_onsyn.AnimeFX.Module.AXButton;
import mai_onsyn.AnimeFX.Utls.AXTreeItem;
import mai_onsyn.VeloVoice.App.Config;

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

    public abstract Config.ConfigBox mkConfigFrame();

    public void drawItemButton(AXButton button) {}
}
