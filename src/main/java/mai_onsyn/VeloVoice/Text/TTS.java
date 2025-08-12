package mai_onsyn.VeloVoice.Text;

import mai_onsyn.AnimeFX.I18N;
import mai_onsyn.AnimeFX.Utls.AXTreeItem;
import mai_onsyn.VeloVoice.NetWork.TTS.NaturalTTSClient;
import mai_onsyn.VeloVoice.NetWork.TTS.ResumableTTSClient;
import mai_onsyn.VeloVoice.NetWork.TTSPool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static mai_onsyn.VeloVoice.App.Runtime.*;

public class TTS {

    private static final Logger log = LogManager.getLogger(TTS.class);
    public static TTSPool pool;


    public static void start(AXTreeItem root, File outputFolder) throws InterruptedException, IOException {

        int threadCount = switch (CLIENT_TYPE) {
            case EDGE -> edgeTTSConfig.getInteger("ThreadCount");
            case NATURAL -> {
                if (NaturalTTSClient.getVoice().contains("Online") && naturalTTSConfig.getInteger("ThreadCount") > 4) {
                    log.warn(I18N.getCurrentValue("log.tts.warn.natural_tts_online"));
                    yield edgeTTSConfig.getInteger("ThreadCount");
                }
                else {
                    yield naturalTTSConfig.getInteger("ThreadCount");
                }
            }
            default -> 1;
        };

        pool = new TTSPool(threadCount);
        pool.connect();

        List<ExecuteItem> executeItems = ExecuteItem.parseStructure(root, outputFolder);
        totalFinished.set(0);
        totalCount = countPieces(executeItems);
        totalStartTime = System.currentTimeMillis();
        startItem(executeItems);

        pool.close();
    }

    private static void startItem(List<ExecuteItem> executeItems) throws IOException, InterruptedException {
        for (int i = 0; i < executeItems.size(); i++) {
            ExecuteItem e = executeItems.get(i);
            List<String> pieces = TextUtil.splitText(e.text());

            currentFinished.set(0);
            currentTotalCount = pieces.size();
            currentFileName = e.name();
            currentStartTime = System.currentTimeMillis();

            log.debug("Current file: {}", e.name());
            pool.execute(pieces, e.folder(), e.name());
            //log.debug(String.format("Complected file: %s", e.name()));

            currentFileName = I18N.getCurrentValue("log.progress.initializing");
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
