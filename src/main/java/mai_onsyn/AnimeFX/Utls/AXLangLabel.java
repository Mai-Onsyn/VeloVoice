package mai_onsyn.AnimeFX.Utls;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.util.Duration;
import mai_onsyn.AnimeFX.AutoUpdatable;
import mai_onsyn.AnimeFX.Localizable;
import mai_onsyn.AnimeFX.Styles.DefaultAXLangLabelStyle;
import mai_onsyn.AnimeFX.Styles.AXLangLabelStyle;

import java.util.List;
import java.util.Map;

public class AXLangLabel extends Label implements Localizable, AutoUpdatable {

    private AXLangLabelStyle style = new DefaultAXLangLabelStyle();
    private Timeline timeline = new Timeline();

    public AXLangLabel(String text) {
        super(text);
        super.setWrapText(true);
        super.setAlignment(Pos.CENTER_LEFT);
        super.setFont(style.getFont());
    }

    public AXLangLabel() {
        this("");
    }


    @Override
    public void update() {
        timeline.stop();
        timeline = new Timeline(new KeyFrame(Duration.millis(200 * style.getAnimeRate()),
                new KeyValue(this.textFillProperty(), style.getFill())
        ));
        timeline.play();
        super.setFont(style.getFont());
    }

    public void setTheme(AXLangLabelStyle style) {
        this.style = style;
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
