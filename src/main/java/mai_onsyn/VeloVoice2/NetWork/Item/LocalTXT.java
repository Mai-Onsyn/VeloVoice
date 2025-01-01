package mai_onsyn.VeloVoice2.NetWork.Item;

import javafx.scene.image.Image;
import mai_onsyn.AnimeFX2.Utls.AXTreeItem;

public class LocalTXT extends Source {
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
        return "source.local_txt";
    }

    @Override
    protected Image getIcon() {
        return null;
    }

    @Override
    public void process(String uri, AXTreeItem root) {

    }

    public LocalTXT() {
        super();
        super.config.registerBoolean("ParseHtmlCharacters", false);
    }
}
