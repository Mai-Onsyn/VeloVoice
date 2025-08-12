import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.SafeArray;
import com.jacob.com.Variant;
import mai_onsyn.VeloVoice.Audio.AudioEncodeUtils;
import mai_onsyn.VeloVoice.NetWork.TTS.NaturalTTSClient;
import mai_onsyn.VeloVoice.Text.Sentence;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class Test1 {
    private static final Logger log = LogManager.getLogger(Test1.class);

    static int length = 0;
    public static void main(String[] args) {
        //byte[] bytes = textToSpeechIO("Hello, this is NaturalVoice speaking through Java JACOB.");

        try (FileOutputStream fos = new FileOutputStream("D:/Users/Desktop/sapiTest.wav")) {
            NaturalTTSClient tts = new NaturalTTSClient();
            NaturalTTSClient.setVoice(NaturalTTSClient.getVoiceList().get(4));
            NaturalTTSClient.setVoiceRate(1);
            NaturalTTSClient.setVoiceVolume(1);


            //tts.connect();

//            Sentence s1 = tts.process("Hello, this is NaturalVoice speaking through Java JACOB.");
//            Sentence s2 = tts.process("I am a Java program.");
//            Sentence s3 = tts.process("我是中国人");
//
//
//            fos.write(AudioEncodeUtils.genWavHeader(s1.audioByteArray().length + s2.audioByteArray().length + s3.audioByteArray().length, 22000, 1));
//            fos.write(s1.audioByteArray());
//            fos.write(s2.audioByteArray());
//            fos.write(s3.audioByteArray());
            int repeat = 1024;
            List<Sentence> sentences = new ArrayList<>(repeat);
            CountDownLatch countDownLatch = new CountDownLatch(repeat);
            for (int i = 0; i < repeat; i++) {
                int finalI = i;
                System.out.println("MakingThread " + i);
                Thread.ofVirtual().start(() -> {
                    try {
                        log.info("Thread {} started", finalI);
                        NaturalTTSClient naturalTTSClient = new NaturalTTSClient();
                        log.info("TTS {} created", finalI);
                        naturalTTSClient.establish();
                        log.info("TTS {} connected", finalI);

                        Sentence result = naturalTTSClient.process(String.format("您在多线程中 为每个线程独立创建 NaturalTTS 对象 的设计思路是正确的，但 0xC0000374 错误（堆损坏）仍然出现，说明问题出在 COM 对象的线程初始化和资源竞争 上。以下是针对性解决方案：\n" +
                                "\n" +
                                "问题根源\n" +
                                "COM 线程模型冲突\n" +
                                "\n" +
                                "即使每个线程有自己的 NaturalTTS 实例，底层 SAPI 的 COM 对象可能仍在进程级别共享资源\n" +
                                "\n" +
                                "虚拟线程（Thread.ofVirtual()）的快速切换加剧竞争\n" +
                                "\n" +
                                "内存流未及时释放\n" +
                                "\n" +
                                "process() 方法中的 memoryStream 可能未完全释放\n" +
                                "\n" +
                                "Jacob 的线程安全性\n" +
                                "\n" +
                                "需要确保每个线程正确初始化 COM 库\n" +
                                "\n"));
                        log.info("TTS {} processed", finalI);
                        sentences.add(result);
                        length += result.audioByteArray().length;
                        countDownLatch.countDown();

                        naturalTTSClient.terminate();

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
            }
            countDownLatch.await();
            fos.write(AudioEncodeUtils.genWavHeader(length, 22000, 1));
            for (Sentence sentence : sentences) {
                fos.write(sentence.audioByteArray());
            }

            tts.terminate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] textToSpeechIO(String text){
        try {
            // 1. 初始化语音引擎
            ActiveXComponent sapObj = new ActiveXComponent("SAPI.SpVoice");

            // 2. 设置语音（选择Xiaoxiao）
            Dispatch voices = Dispatch.call(sapObj, "GetVoices").toDispatch();
            int voiceCount = Dispatch.get(voices, "Count").getInt();
            for (int i = 0; i < voiceCount; i++) {
                Dispatch voice = Dispatch.call(voices, "Item", new Variant(i)).toDispatch();
                String voiceName = Dispatch.call(voice, "GetAttribute", "Name").getString();
                System.out.println(voiceName);
                if (voiceName.contains("Xiaoxiao")) {
                    Dispatch.putRef(sapObj, "Voice", voice);
                    break;
                }
            }

            // 3. 创建内存流对象
            ActiveXComponent memoryStream = new ActiveXComponent("SAPI.SpMemoryStream");

            // 4. 设置音频格式（22kHz 16bit Mono）
            Dispatch audioFormat = new ActiveXComponent("SAPI.SpAudioFormat").getObject();
            Dispatch.put(audioFormat, "Type", new Variant(22));

            Dispatch.putRef(memoryStream, "Format", audioFormat);

            // 5. 将输出重定向到内存流
            Dispatch.putRef(sapObj, "AudioOutputStream", memoryStream);

            // 6. 设置语音属性
            Dispatch.put(sapObj, "Rate", new Variant(1));   // 语速 (-10到10)
            Dispatch.put(sapObj, "Volume", new Variant(100)); // 音量 (0-100)

            // 7. 执行TTS合成
            Dispatch.call(sapObj, "Speak", new Variant(text), new Variant(8)); // 8 = SVSFlagsAsync

            // 8. 等待合成完成
            Dispatch.call(sapObj, "WaitUntilDone", new Variant(-1));

            // 9. 获取内存流数据
            Variant dataVariant = Dispatch.call(memoryStream, "GetData");
            SafeArray safeArray = dataVariant.toSafeArray();

            // 10. 转换为byte[]
            return safeArray.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}