package mai_onsyn.VeloVoice2.Text;

import javafx.beans.property.SimpleStringProperty;
import mai_onsyn.AnimeFX2.Utls.AXDataTreeItem;
import mai_onsyn.AnimeFX2.Utls.AXTreeItem;
import mai_onsyn.VeloVoice2.NetWork.TTSPool;

import java.io.File;

import static mai_onsyn.VeloVoice2.App.Runtime.*;
import static mai_onsyn.VeloVoice2.FrameFactory.LogFactory.logger;

public class TTS {

    public static TTSPool pool;


    public static void start(AXTreeItem root, File outputFolder) throws InterruptedException {

        pool = new TTSPool(TTSPool.ClientType.EDGE, edgeTTSConfig.getInteger("ThreadCount"));
        pool.connect();

        startItem(root, outputFolder, null);

        pool.close();
    }

    private static void startItem(AXTreeItem item, File folder, String name) throws InterruptedException {
        if (item instanceof AXDataTreeItem<?> d) {
            if (d.getData() instanceof SimpleStringProperty s) {
                String content = s.get();

                logger.debug("Start file: %s", name);
                pool.execute(TextUtil.splitText(content), folder, String.format("%s", name));
                logger.debug("Complected file: %s", name);
            }
        }
        else {
            for (AXTreeItem i : item.getChildrenAsItem()) {
                if (i instanceof AXDataTreeItem<?>) startItem(i, folder, i.getHeadName());
                else startItem(i, new File(folder, i.getHeadName()), null);
            }
        }
    }
}
