package mai_onsyn.VeloVoice.App;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AppConfig {
    public enum LoadType {
        LOCAL_FULL,
        LOCAL_VOLUMED,
        WENKU8
    }

    public static boolean pausing = false;

    public static LoadType loadType = LoadType.LOCAL_FULL;
    public static String voiceModel = "zh-CN-XiaoyiNeural";
    public static double configWindowChangeWidth = 820;

    public static int logLevel = 0;

    public static int maxConnectThread = 16;
    public static int retryCount = 3;
    public static int timeoutSeconds = 10;

    public static int textPieceSize = 256;
    public static List<Character> textSplitSymbols = new ArrayList<>(Arrays.asList('。', '？', '！', '…', '，', ' '));
    public static boolean isAppendVolumeName = true;
    public static boolean isAppendOrdinal = true;
    public static boolean splitChapter = false;
    public static boolean isAppendNameForSplitChapter = false;
    public static int maxMp3Duration = 20;
}