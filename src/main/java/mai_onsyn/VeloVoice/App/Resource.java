package mai_onsyn.VeloVoice.App;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import mai_onsyn.AnimeFX.I18N;
import mai_onsyn.AnimeFX.Utls.Toolkit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Resource {

    public static final List<WritableImage> grayResources = new ArrayList<>();


    public static final Image icon;
    public static final WritableImage horn;
    public static final WritableImage setting;
    public static final WritableImage edit;
    public static final WritableImage list;
    private static final Logger log = LogManager.getLogger(Resource.class);


    static {
        try {
            horn = new WritableImage(Toolkit.loadImage("textures/icons/app/horn.png").getPixelReader(), 512, 512);
            setting = new WritableImage(Toolkit.loadImage("textures/icons/app/setting.png").getPixelReader(), 512, 512);
            edit = new WritableImage(Toolkit.loadImage("textures/icons/app/edit.png").getPixelReader(), 512, 512);
            list = new WritableImage(Toolkit.loadImage("textures/icons/app/log.png").getPixelReader(), 512, 512);


            icon = new Image("textures/icons/app/icon.png");

            grayResources.addAll(List.of(horn, setting, edit, list));

            log.debug("Resources loaded");
        } catch (IOException e) {
            log.error(I18N.getCurrentValue("log.resource.error.load_failed"), e);
            throw new RuntimeException(e);
        }
    }
}
