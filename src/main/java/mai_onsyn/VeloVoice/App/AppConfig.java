package mai_onsyn.VeloVoice.App;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AppConfig {
    public enum LoadType {
        LOCAL_DIRECTLY,
        LOCAL_FULL,
        LOCAL_VOLUMED,
        WENKU8
    }

    public static boolean pausing = false;

    public static LoadType loadType = LoadType.LOCAL_DIRECTLY;
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

    public static String previewText = "全名制作人们大家好，我是练习时长2.5年的个人练习生0d00，喜欢唱、跳、ciallo、0b0000001011010001";
    public static boolean splitChapter = false;
    public static double maxAudioDuration = 20;
    public static boolean isAppendNameForSplitChapter = true;
}