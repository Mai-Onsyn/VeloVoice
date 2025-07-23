package mai_onsyn.VeloVoice.NetWork.LoadTarget;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import mai_onsyn.AnimeFX.Module.AXButton;
import mai_onsyn.AnimeFX.Utls.AXTreeItem;
import mai_onsyn.AnimeFX.layout.AutoPane;
import mai_onsyn.VeloVoice.App.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.net.URI;

import static mai_onsyn.VeloVoice.App.Constants.UI_HEIGHT;

public abstract class Source {

    private static final Logger log = LogManager.getLogger(Source.class);
    protected Config config = new Config();

    public Config getConfig() {
        return config;
    }

    protected abstract String getHomePage();

    protected abstract String getDetails();

    public abstract String getNameSpace();

    protected abstract Image getIcon();

    public abstract Config.ConfigBox mkConfigFrame();

    public abstract void process(String uri, AXTreeItem root) throws Exception;

    public void drawItemButton(AXButton button) {
        button.getTextLabel().setAlignment(Pos.CENTER_LEFT);
        button.setPosition(button.getTextLabel(), false, UI_HEIGHT, 0, 0, 0);
        ImageView icon = new ImageView(getIcon());

        button.setPosition(icon, false, UI_HEIGHT * 0.2, UI_HEIGHT * 0.8, UI_HEIGHT * 0.2, UI_HEIGHT * 0.2);
        button.flipRelativeMode(icon, AutoPane.Motion.RIGHT);


        Label label = new Label(getDetails());

        // 设置初始样式
        label.setTextFill(Color.LIGHTBLUE);
        label.setStyle("-fx-background-color: transparent;");

        // 鼠标悬停效果
        label.setOnMouseEntered(e -> {
            label.setTextFill(Color.LIGHTCYAN);
            label.setUnderline(true);
        });

        // 鼠标移出效果
        label.setOnMouseExited(e -> {
            label.setTextFill(Color.LIGHTBLUE);
            label.setUnderline(false);
        });

        label.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            try {
                Desktop.getDesktop().browse(new URI(getHomePage()));
            } catch (Exception ex) {
                log.error("Can't open browser: {}", ex.getMessage());
            }
        });

        button.setPosition(label, AutoPane.AlignmentMode.BOTTOM_RIGHT, AutoPane.LocateMode.ABSOLUTE, UI_HEIGHT * 0.2, 2);

        button.getChildren().addAll(icon, label);
    }
}
