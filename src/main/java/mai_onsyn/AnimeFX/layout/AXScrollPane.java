package mai_onsyn.AnimeFX.layout;

import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import mai_onsyn.AnimeFX.Utls.Toolkit;

public class AXScrollPane extends ScrollPane {

    public AXScrollPane() {
        super();
        super.getStylesheets().add("style.css");
        Toolkit.addSmoothScrolling(this);
    }

    public AXScrollPane(Node content) {
        super(content);
        super.getStylesheets().add("style.css");
        Toolkit.addSmoothScrolling(this);
    }

}
