package mai_onsyn.VeloVoice.App;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import javafx.beans.property.SimpleStringProperty;
import mai_onsyn.AnimeFX.I18N;
import mai_onsyn.AnimeFX.Utls.AXDataTreeItem;
import mai_onsyn.AnimeFX.Utls.AXTreeItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;

public class AutoSaveListener {
    private static final Logger log = LogManager.getLogger(AutoSaveListener.class);
    private static AXTreeItem listenerObject;

    private static Thread listenerThread;
    private static String lastData = "";
    public static final File DATA_FILE = new File(System.getProperty("user.dir") + "\\data.json");

    public static void setListenerObject(AXTreeItem listenerObject) {
        AutoSaveListener.listenerObject = listenerObject;
    }

    public static void start() {
        listenerThread = Thread.ofVirtual().start(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(30000);
                    trySave();
                } catch (InterruptedException _) {}
            }
        });
    }

    public static void trySave() {
        if (listenerObject != null) {
            String data = JSONObject.toJSONString(buildJson(listenerObject), JSONWriter.Feature.PrettyFormat);
            if (!data.equals(lastData)) {
                lastData = data;
                try (FileOutputStream fos = new FileOutputStream(DATA_FILE)) {
                    fos.write(data.getBytes(StandardCharsets.UTF_8));
                } catch (IOException e) {
                    log.error(I18N.getCurrentValue("log.auto_save_listener.error.save_failed"), e);
                }
            }
        }
    }

    public static void tryLoad() {
        if (listenerObject == null) return;
        if (!DATA_FILE.exists()) return;

        try (FileInputStream fis = new FileInputStream(DATA_FILE)) {
            String data = new String(fis.readAllBytes(), StandardCharsets.UTF_8);
            JSONObject rootJson = JSONObject.parseObject(data);

            for (Map.Entry<String, Object> entry : rootJson.entrySet()) {
                listenerObject.add(buildTree(entry.getKey(), entry.getValue()));
            }
        } catch (IOException e) {
            log.error(I18N.getCurrentValue("log.auto_save_listener.error.load_failed"), e);
        }
    }

    private static AXTreeItem buildTree(String name, Object value) {
        if (value instanceof String text) {
            SimpleStringProperty prop = new SimpleStringProperty(text);
//            return new AXDataTreeItem<>(name, prop, src -> new SimpleStringProperty(src.get()));
            return listenerObject.getAttribution().createFileItem(name, prop);
        }

        if (value instanceof JSONObject obj) {
            AXTreeItem node = listenerObject.getAttribution().createFolderItem(name);

            for (Map.Entry<String, Object> entry : obj.entrySet()) {
                AXTreeItem child = buildTree(entry.getKey(), entry.getValue());
                node.add(child);
            }
            return node;
        }

        throw new IllegalStateException("Unsupported json value type: " + value);
    }

    private static Object buildJson(AXTreeItem target) {
        if (target instanceof AXDataTreeItem<?> dataTreeItem) {
            if (dataTreeItem.getData() instanceof SimpleStringProperty data) {
                return data.get();
            }
        }

        JSONObject subObj = new JSONObject(new LinkedHashMap<>());
        for (AXTreeItem child : target.getChildrenAsItem()) {
            subObj.put(child.getHeadName(), buildJson(child));
        }
        return subObj;
    }

    public static void stop() {
        if (listenerThread != null) {
            listenerThread.interrupt();
        }
    }
}
