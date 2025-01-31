package mai_onsyn.AnimeFX2.layout;

import javafx.scene.control.ScrollPane;
import mai_onsyn.AnimeFX2.Utls.Toolkit;

public class AXScrollPane extends ScrollPane {

    public AXScrollPane() {
        super();
        super.getStylesheets().add("style.css");
        Toolkit.addSmoothScrolling(this);
    }

}
