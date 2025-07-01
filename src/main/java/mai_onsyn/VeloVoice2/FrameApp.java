package mai_onsyn.VeloVoice2;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import mai_onsyn.AnimeFX2.I18N;
import mai_onsyn.AnimeFX2.layout.AutoPane;
import mai_onsyn.VeloVoice2.FrameFactory.LogFactory;
import mai_onsyn.VeloVoice2.FrameFactory.MainFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static mai_onsyn.VeloVoice2.App.Runtime.themeManager;

public class FrameApp extends Application {
    private static final Logger log = LogManager.getLogger(FrameApp.class);

    @Override
    public void start(Stage stage) throws Exception {

        AutoPane root = new AutoPane();
        root.setOnMousePressed(_ -> root.requestFocus());

        MainFactory.drawMainFrame(root);
        LogFactory.drawLogFrame();
        I18N.setLanguage("en_us");
        themeManager.flushAll();

        Scene scene = new Scene(root, 1280, 720);
        stage.setMinWidth(900);
        stage.setMinHeight(500);
        stage.getIcons().add(new Image("textures/icon.png"));
        stage.setTitle("VeloVoice");
        stage.setScene(scene);

        log.info("Application started");
        root.requestFocus();

//        Thread.ofVirtual().name("Test").start(() -> {
//
//            try {
//
//                for (int i = 0; i < 16; i++) {
//                    Thread.sleep(5);
//
//                    log.debug("test " + i);
//                }
//
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        });

//        LocalTXTHeaderEditor2 headerItemsEditor = new LocalTXTHeaderEditor2(JSONArray.parseArray(Runtime.sources.get("LocalTXT").getConfig().getString("HeaderItems")));
//        stage.setScene(new Scene(headerItemsEditor, 600, 450));
//        headerItemsEditor.requestFocus();
        stage.show();

    }
}
