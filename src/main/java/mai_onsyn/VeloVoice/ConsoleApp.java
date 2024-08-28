package mai_onsyn.VeloVoice;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import mai_onsyn.VeloVoice.App.AppConfig;
import mai_onsyn.VeloVoice.NetWork.Crawler.Websites.LiNovel;
import mai_onsyn.VeloVoice.NetWork.Crawler.Websites.WenKu8;
import mai_onsyn.VeloVoice.NetWork.TTS.EdgeTTSClient;
import mai_onsyn.VeloVoice.NetWork.Voice;
import mai_onsyn.VeloVoice.Text.TTS;
import mai_onsyn.VeloVoice.Text.TextFactory;
import mai_onsyn.VeloVoice.Text.TextLoader;
import mai_onsyn.VeloVoice.Utils.Structure;
import mai_onsyn.VeloVoice.Utils.Util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static mai_onsyn.VeloVoice.App.AppConfig.isAppendOrdinal;
import static mai_onsyn.VeloVoice.App.AppConfig.loadType;
import static mai_onsyn.VeloVoice.App.Runtime.logger;

public class ConsoleApp {

    private static final String srcPath;
    private static final String audioPath;
    private static final String textPath;
    private static final boolean saveText;
    private static final JSONObject model;
    private static final double voiceRate;
    private static final double voicePitch;
    private static final double voiceVolume;

    private static final String configPath = System.getProperty("user.dir") + "\\tts_config.json";

    static {
        try {
            JSONObject configJson = JSON.parseObject(Files.readString(new File(configPath).toPath()), Feature.AllowComment);

            loadType = AppConfig.LoadType.valueOf(configJson.getInteger("LoadType"));
            srcPath = configJson.getString("SourcePath");
            audioPath = configJson.getString("AudioSavePath");
            textPath = configJson.getString("TextSavePath");
            saveText = configJson.getBoolean("IsSaveText");
            model = Voice.get(configJson.getString("VoiceModel"));
            voiceRate = configJson.getDouble("VoiceRate");
            voicePitch = configJson.getDouble("VoicePitch");
            voiceVolume = configJson.getDouble("VoiceVolume");

            if (
                    srcPath == null ||
                    audioPath == null ||
                    textPath == null ||
                    model == null
                    )
                throw new NullPointerException("存在配置文件，但缺少必要的配置信息");


        } catch (Exception e) {
            System.out.println("无法读取正确的TTS配置文件，因为：" + e.getMessage());
            try (Scanner scanner = new Scanner(System.in)) {
                System.out.println("是否覆盖TTS配置文件? (y/n)");
                if (scanner.nextLine().equals("y")) {
                    writeTTSConfig();
                    System.out.println("已覆盖TTS配置文件");
                }
            }
            System.exit(114514);
            throw new RuntimeException();
        }
    }

    public static void main(String[] args) {

        //no-gui mode

        EdgeTTSClient.setVoice(model);
        EdgeTTSClient.setVoiceRate(voiceRate);
        EdgeTTSClient.setVoicePitch(voicePitch);
        EdgeTTSClient.setVoiceVolume(voiceVolume);

        Structure<List<String>> rootStructure;
        try {
            switch (loadType) {
                case LOCAL_DIRECTLY, LOCAL_FULL, LOCAL_VOLUMED -> {
                    File targetFolder = new File(srcPath);
                    if (!targetFolder.exists()) {
                        System.out.println("指定文件夹不存在 - " + srcPath);
                        return;
                    }
                    File[] series = targetFolder.listFiles((_, name) -> name.endsWith(".txt"));
                    if (series == null || series.length == 0) {
                        System.out.println("指定文件夹内没有可读取的txt文件");
                        return;
                    }
                    rootStructure = new Structure<>("Root");
                    for (File file : series) {
                        NativeMethod.collectFilesToStructure(file, rootStructure);
                    }
                }
                case WEN_KU8 -> {
                    if (srcPath.contains("https://www.wenku8.net/book/") || srcPath.contains("https://www.wenku8.net/novel/")) {
                        rootStructure = new WenKu8(srcPath).getContents();
                        System.out.println("已加载 - " + rootStructure.getName());
                    }
                    else {
                        System.out.printf("加载失败 - \"%s\" 不是正确的轻小说文库小说地址%n", srcPath);
                        return;
                    }
                }
                case LI_NOVEL -> {
                    if (srcPath.contains("https://www.linovel.net/book/")) {
                        rootStructure = new LiNovel(srcPath).getContents();
                        System.out.println("已加载 - " + rootStructure.getName());
                    }
                    else {
                        System.out.printf("加载失败 - \"%s\" 不是正确的轻之文库小说地址%n", srcPath);
                        return;
                    }
                }
                default -> {
                    return;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("加载失败 - " + e);
        }

        System.out.println("===============当前结构树===============");
        System.out.println(rootStructure);

        File textOutputFolder = new File(textPath);
        if (saveText) {
            try {
                if (rootStructure.getChildren().size() == 1) Structure.Factory.saveToFile(rootStructure.getChildren().getFirst(), textOutputFolder, false, 0);
                else {
                    for (int i = 0; i < rootStructure.getChildren().size(); i++) {
                        Structure.Factory.saveToFile(rootStructure.getChildren().get(i), textOutputFolder, isAppendOrdinal, i + 1);
                    }
                }
                System.out.println("已保存文件树到 - " + textOutputFolder.getAbsolutePath());
            } catch (Exception e) {
                System.out.println("保存文件树失败 - " + e);
            }
        }

        long startTime = System.currentTimeMillis();
        TTS.startNewTask(rootStructure, new File(audioPath));
        System.out.printf("完成！耗时 %s%n", Util.formatDuration(System.currentTimeMillis() - startTime));

    }

    private static void writeTTSConfig() {
        try (FileWriter writer = new FileWriter(System.getProperty("user.dir") + "\\tts_config.json")) {

            String configJson =
                    """
                    {
                      //加载模式，指定应当如何加载SourcePath指定的文本
                      //0: 加载本地文件夹中所有的txt文件，不进行章节解析，直接加载所有文本
                      //1: 加载本地文件夹中所有的txt文件，进行章节解析，需要整个小说系列作为一个txt文件
                      //2: 加载本地文件夹中所有的txt文件，进行章节解析，需要每一分卷作为一个txt文件
                      //3: 从轻小说文库加载整个小说系列，需要轻小说文库的小说地址
                      //4: 从轻之文库加载整个小说系列
                      "LoadType": 0,
                                        
                                        
                      //小说源地址，根据LoadType的值进行不同的处理
                      //e.g.1 "C:/Users/XXX/Desktop/Novels"
                      //e.g.2 "/storage/emulated/0/Files/"
                      //e.g.3 "https://www.wenku8.net/novel/X/XXX/index.htm"
                      //e.g.4 "https://www.wenku8.net/book/XXX.htm"
                      //e.g.5 "https://www.linovel.net/book/107897.html“
                      "SourcePath": "文件夹路径或网页地址",
                                        
                                        
                      //是否保存加载出来的文本，如果为true，则将文本保存到TextSavePath指定的文件夹中
                      //保存文本会在TTS转换之前进行
                      "IsSaveText": false,
                      "TextSavePath": "文件夹路径",
                                        
                                        
                      //TTS转换结果的输出目录
                      "AudioSavePath": "文件夹路径",
                                        
                                        
                      //TTS配置
                                        
                      //语音模型，以下是可用的中文语音模型
                      //zh-TW-YunJheNeural, zh-TW-HsiaoYuNeural, zh-TW-HsiaoChenNeural,
                      //zh-HK-WanLungNeural, zh-HK-HiuMaanNeural, zh-HK-HiuGaaiNeural,
                      //zh-CN-shaanxi-XiaoniNeural, zh-CN-liaoning-XiaobeiNeural, zh-CN-YunyangNeural,
                      //zh-CN-YunxiaNeural, zh-CN-YunxiNeural, zh-CN-YunjianNeural,
                      //zh-CN-XiaoyiNeural, zh-CN-XiaoxiaoNeural
                      "VoiceModel": "zh-CN-XiaoxiaoNeural",
                                        
                      //语速，范围0.0-2.0
                      "VoiceRate": 1.5,
                                        
                      //音调，范围0.0-1.5
                      "VoicePitch": 1.0,
                                        
                      //音量，范围0.0-2.0
                      "VoiceVolume": 1.0
                                        
                    }
                    """;

            writer.write(configJson);
        } catch (IOException e) {
            throw new RuntimeException("写入TTS配置文件失败", e);
        }
    }
}

class NativeMethod {
    public static void collectFilesToStructure(File file, Structure<List<String>> parent) {
        if (file.isFile()) {
            String fileName = file.getName();
            if (file.getName().endsWith(".txt")) {
                fileName = fileName.substring(0, fileName.length() - 4);
                try {
                    Structure<List<String>> children;
                    switch (loadType) {
                        case LOCAL_FULL -> {
                            children = Structure.Factory.of(TextFactory.parseFromSeries(TextLoader.load(file)));
                            children.setName(fileName);
                        }
                        case LOCAL_VOLUMED -> {
                            children = Structure.Factory.of(TextFactory.parseFromVolume(TextLoader.load(file)));
                        }
                        case LOCAL_DIRECTLY -> {
                            children = new Structure<>(fileName, new ArrayList<>(List.of(TextLoader.load(file))));
                        }
                        default -> children = new Structure<>("null");
                    }
                    System.out.println("已加载 - " + file.getName());
                    parent.getChildren().add(children);
                } catch (Exception e) {
                    System.out.printf("加载失败：%s - %s%n", file.getAbsolutePath(), e);
                }
            }
        }
        else if (file.isDirectory()) {
            Structure<List<String>> subStructure = new Structure<>(file.getName());
            File[] subFiles = file.listFiles();
            if (subFiles != null) {
                for (File subFile : subFiles) {
                    collectFilesToStructure(subFile, subStructure);
                }
            }
            parent.getChildren().add(subStructure);
        }
        else System.out.printf("Not a correct File : %s%n", file.getAbsolutePath());
    }
}