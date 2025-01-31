package mai_onsyn.AnimeFX2.Utls;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.util.Duration;
import mai_onsyn.AnimeFX2.AutoUpdatable;
import mai_onsyn.AnimeFX2.LanguageSwitchable;
import mai_onsyn.AnimeFX2.Styles.DefaultAXLangLabelStyle;
import mai_onsyn.AnimeFX2.Styles.AXLangLabelStyle;

import java.util.Map;

public class AXLangLabel extends Label implements LanguageSwitchable, AutoUpdatable {

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
    public void switchLanguage(String str) {
        setText(str);
    }

    @Override
    public Map<LanguageSwitchable, String> getLanguageElements() {
        return Map.of();
    }

    @Override
    public void update() {
        timeline.stop();
        timeline = new Timeline(new KeyFrame(Duration.millis(200 * style.getAnimeRate()),
                new KeyValue(this.textFillProperty(), style.getFill())
        ));
        timeline.play();
    }
}
