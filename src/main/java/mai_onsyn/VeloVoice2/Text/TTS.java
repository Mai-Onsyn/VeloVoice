package mai_onsyn.VeloVoice2.Text;

import javafx.beans.property.SimpleStringProperty;
import mai_onsyn.AnimeFX2.Utls.AXDataTreeItem;
import mai_onsyn.AnimeFX2.Utls.AXTreeItem;
import mai_onsyn.VeloVoice2.NetWork.TTSPool;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static mai_onsyn.VeloVoice2.App.Runtime.*;
import static mai_onsyn.VeloVoice2.FrameFactory.LogFactory.logger;
import static mai_onsyn.VeloVoice2.FrameFactory.LogFactory.totalProgressBar;

public class TTS {

    public static TTSPool pool;

    private static final List<ExecuteItem> executeItems = new ArrayList<>();

    public static void start(AXTreeItem root, File outputFolder) throws InterruptedException {

        pool = new TTSPool(TTSPool.ClientType.EDGE, edgeTTSConfig.getInteger("ThreadCount"));
        pool.connect();

        executeItems.clear();

        initItem(root, outputFolder, null);
        totalFinished.set(0);
        totalCount = countPieces();
        startItem();

        pool.close();
    }

    private static void initItem(AXTreeItem item, File folder, String name) {
        if (item instanceof AXDataTreeItem<?> d) {
            if (d.getData() instanceof SimpleStringProperty s) {
                String content = s.get();

                executeItems.add(new ExecuteItem(TextUtil.splitText(content), folder, String.format("%s", name)));
            }
        }
        else {
            for (AXTreeItem i : item.getChildrenAsItem()) {
                if (i instanceof AXDataTreeItem<?>) initItem(i, folder, i.getHeadName());
                else initItem(i, new File(folder, i.getHeadName()), null);
            }
        }
    }

    private static void startItem() throws InterruptedException {
        for (int i = 0; i < executeItems.size(); i++) {
            ExecuteItem e = executeItems.get(i);
            currentFinished.set(0);
            currentTotalCount = e.texts.size();

            logger.debug("Start file: %s", e.name);
            pool.execute(e.texts, e.folder, e.name);
            logger.debug("Complected file: %s", e.name);
        }
    }

    private static int countPieces() {
        int count = 0;
        for (ExecuteItem e : executeItems) {
            count += e.texts.size();
        }
        return count;
    }

    private record ExecuteItem(List<String> texts, File folder, String name) {}
}
