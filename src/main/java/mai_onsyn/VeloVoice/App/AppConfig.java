package mai_onsyn.VeloVoice.App;

import com.alibaba.fastjson.annotation.JSONField;
import mai_onsyn.VeloVoice.Utils.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AppConfig {
    public enum LoadType {
        LOCAL_DIRECTLY(0),
        LOCAL_FULL(1),
        LOCAL_VOLUMED(2),
        WEN_KU8(3),
        LI_NOVEL(4),
        KAKUYOMU(5);

        LoadType(int i) {}

        public static LoadType valueOf(int value) {
            for (LoadType a : LoadType.values()) {
                if (a.ordinal() == value) {
                    return a;
                }
            }
            throw new IllegalArgumentException("No enum constant with value " + value);
        }
    }

    public static boolean pausing = false;
    public static boolean isWindowSupport = Util.isWindowSupport() && Theme.enableWinUI;

    public static LoadType loadType = LoadType.LOCAL_DIRECTLY;
    public static String voiceModel = "zh-CN-XiaoyiNeural";
    public static double configWindowChangeWidth = 820;

    public static int logLevel = 0;

    public static int maxConnectThread = 16;
    public static int retryCount = 5;
    public static int timeoutSeconds = 10;

    public static int textPieceSize = 256;
    public static List<Character> textSplitSymbols = new ArrayList<>(Arrays.asList('。', '？', '！', '…', '，', ' '));
    public static boolean isAppendVolumeName = true;    //为网络小说的每个章节的章节名字前加上该章节所属卷的卷名
    public static boolean isAppendOrdinal = true;   //生成音频文件时是否为文件以及文件夹添加序号
    public static boolean isAppendExtraChapterName = false;   //为网络章节添加额外的章(前面添加"第%d章")
    public static boolean isAppendExtraVolumeName = false;   //为网络章节添加额外的卷名(前面添加"第%d卷")

    public static String previewText = "全名制作人们大家好，我是练习时长2.5年的个人练习生0d00，喜欢唱、跳、ciallo、0b0000001011010001";
    public static boolean splitChapter = false; //分割生成的音频
    public static double maxAudioDuration = 20; //每个音频文件的最大时长
    public static boolean isAppendNameForSplitChapter = true;   //分割的每个音频文件的开头都添加该章节的名称
}