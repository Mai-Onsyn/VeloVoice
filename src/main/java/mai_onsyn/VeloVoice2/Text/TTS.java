package mai_onsyn.VeloVoice2.Text;

import mai_onsyn.AnimeFX2.Utls.AXTreeItem;
import mai_onsyn.VeloVoice2.NetWork.TTSPool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.List;

import static mai_onsyn.VeloVoice2.App.Runtime.*;

public class TTS {

    private static final Logger log = LogManager.getLogger(TTS.class);
    public static TTSPool pool;


    public static void start(AXTreeItem root, File outputFolder) throws Exception {

        pool = new TTSPool(TTSPool.ClientType.EDGE, edgeTTSConfig.getInteger("ThreadCount"));
        pool.connect();

        List<ExecuteItem> executeItems = ExecuteItem.parseStructure(root, outputFolder);
        //initItem(executeItems, root, outputFolder, null);
        totalFinished.set(0);
        totalCount = countPieces(executeItems);
        startItem(executeItems);

        pool.close();
    }

    private static void startItem(List<ExecuteItem> executeItems) throws Exception {
        for (int i = 0; i < executeItems.size(); i++) {
            ExecuteItem e = executeItems.get(i);
            List<String> pieces = TextUtil.splitText(e.text());

            currentFinished.set(0);
            currentTotalCount = pieces.size();

            log.debug(String.format("Start file: %s", e.name()));
            pool.execute(pieces, e.folder(), e.name());
            log.debug(String.format("Complected file: %s", e.name()));
        }
    }

    private static int countPieces(List<ExecuteItem> executeItems) {
        int count = 0;
        for (ExecuteItem e : executeItems) {
            List<String> pieces = TextUtil.splitText(e.text());
            count += pieces.size();
        }
        return count;
    }


}
