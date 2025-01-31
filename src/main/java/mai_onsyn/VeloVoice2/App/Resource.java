package mai_onsyn.VeloVoice2.App;

import javafx.scene.image.WritableImage;
import mai_onsyn.AnimeFX2.Utls.Toolkit;

import java.io.IOException;

public class Resource {
    public static final WritableImage horn;


    static {
        try {
            horn = new WritableImage(Toolkit.loadImage("textures/icons/app/horn.png").getPixelReader(), 512, 512);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
