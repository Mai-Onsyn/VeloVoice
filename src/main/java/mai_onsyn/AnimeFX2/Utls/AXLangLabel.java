package mai_onsyn.AnimeFX2.Utls;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.util.Duration;
import mai_onsyn.AnimeFX2.AutoUpdatable;
import mai_onsyn.AnimeFX2.Localizable;
import mai_onsyn.AnimeFX2.Styles.DefaultAXLangLabelStyle;
import mai_onsyn.AnimeFX2.Styles.AXLangLabelStyle;

import java.util.List;
import java.util.Map;

public class AXLangLabel extends Label implements Localizable, AutoUpdatable {

    private AXLangLabelStyle style = new DefaultAXLangLabelStyle();
    private Timeline timeline = new Timeline();

    public AXLangLabel(String text) {
        super(text);
        super.setWrapText(true);
        super.setAlignment(Pos.CENTER_LEFT);
    }

    public AXLangLabel() {
        super();
        super.setWrapText(true);
        super.setAlignment(Pos.CENTER_LEFT);
    }


    @Override
    public void update() {
        timeline.stop();
        timeline = new Timeline(new KeyFrame(Duration.millis(200 * style.getAnimeRate()),
                new KeyValue(this.textFillProperty(), style.getFill())
        ));
        timeline.play();
    }


    private String langKey = "";

    @Override
    public String getI18NKey() {
        return langKey;
    }

    @Override
    public List<Localizable> getChildrenLocalizable() {
        return List.of();
    }

    @Override
    public void setI18NKey(String key) {
        langKey = key;
    }

    @Override
    public void setChildrenI18NKeys(Map<String, String> keyMap) {

    }

    @Override
    public void localize(String text) {
        setText(text);
    }
}
