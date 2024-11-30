package mai_onsyn.gecProxy;

import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

public class Constants {

    public static int mitmproxyPort;
    public static int serverPort;
    public static int flushPeriod;
    public static String edgePath;
    public static String htmlPath = System.getProperty("user.dir") + "\\index.html";

    static {
        try {
            JSONObject json = JSONObject.parseObject(Files.readString(new File(System.getProperty("user.dir") + "\\config.json").toPath()));

            mitmproxyPort = json.getInteger("MITMProxyPort");
            serverPort = json.getInteger("ServerPort");
            edgePath = json.getString("EdgePath");
            flushPeriod = json.getInteger("FlushPeriod");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
