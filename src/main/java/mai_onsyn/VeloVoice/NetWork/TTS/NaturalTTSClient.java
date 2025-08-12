package mai_onsyn.VeloVoice.NetWork.TTS;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;
import mai_onsyn.VeloVoice.Audio.AudioEncodeUtils;
import mai_onsyn.VeloVoice.Text.Sentence;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

import static mai_onsyn.VeloVoice.App.Runtime.naturalTTSConfig;

public class NaturalTTSClient implements TTSClient {
    private static final Logger log = LogManager.getLogger(NaturalTTSClient.class);
    private static String voiceName = "Microsoft Huihui Desktop";
    private static int voiceRate = 0;
    private static double voiceVolume = 1.0;

    private static final Object lock = new Object();

    private final ActiveXComponent sapObj;
    private final Dispatch audioFormat;

    public static String getVoice() {
        return voiceName;
    }

    private byte[] textToSpeech(String text) throws Exception {
        ActiveXComponent localStream = new ActiveXComponent("SAPI.SpMemoryStream");
        Dispatch.putRef(localStream, "Format", audioFormat);
        Dispatch.putRef(sapObj, "AudioOutputStream", localStream);

        try {
            synchronized (lock) {
                Thread.sleep(Math.round(0.9 * naturalTTSConfig.getInteger("ThreadCount") + 6.4));
            }
            Dispatch.call(sapObj, "Speak", new Variant(text), new Variant(8));
            Dispatch.call(sapObj, "WaitUntilDone", new Variant(-1));

            return Dispatch.call(localStream, "GetData")
                    .toSafeArray()
                    .toByteArray();
            //return new byte[] {1,1,4,5,1,4,1,9,1,9,8,1,0};
        } finally {
            localStream.safeRelease();
        }
    }

    public static List<String> getVoiceList() {
        ActiveXComponent tempoObj = new ActiveXComponent("SAPI.SpVoice");
        List<String> result = new ArrayList<>();

        Dispatch voices = Dispatch.call(tempoObj, "GetVoices").toDispatch();
        int voiceCount = Dispatch.get(voices, "Count").getInt();
        for (int i = 0; i < voiceCount; i++) {
            Dispatch voice = Dispatch.call(voices, "Item", new Variant(i)).toDispatch();
            String voiceName = Dispatch.call(voice, "GetAttribute", "Name").getString();
            result.add(voiceName);
        }
        return result;
    }

    public static void setVoice(String voiceName) {
        NaturalTTSClient.voiceName = voiceName;
    }

    public static void setVoiceRate(int voiceRate) {
        NaturalTTSClient.voiceRate = voiceRate;
    }

    public static void setVoiceVolume(double voiceVolume) {
        NaturalTTSClient.voiceVolume = voiceVolume;
    }

    public NaturalTTSClient() {
        ComThread.InitSTA();

        synchronized (lock) {
            sapObj = new ActiveXComponent("SAPI.SpVoice");
            audioFormat = new ActiveXComponent("SAPI.SpAudioFormat").getObject();
        }

        Dispatch.put(audioFormat, "Type", new Variant(22));
    }

    @Override
    public void establish() throws Exception {
        synchronized (lock) {
            Thread.sleep(32);
            Dispatch.put(sapObj, "Rate", new Variant(voiceRate));
            Dispatch.put(sapObj, "Volume", new Variant(voiceVolume * 100));

            Dispatch voices = Dispatch.call(sapObj, "GetVoices").toDispatch();
            int voiceCount = Dispatch.get(voices, "Count").getInt();
            for (int i = 0; i < voiceCount; i++) {
                Dispatch voice = Dispatch.call(voices, "Item", new Variant(i)).toDispatch();
                String voiceName = Dispatch.call(voice, "GetAttribute", "Name").getString();
                if (voiceName.contains(NaturalTTSClient.voiceName)) {
                    Dispatch.putRef(sapObj, "Voice", voice);
                    return;
                }
            }
        }
        log.error("Voice not found: {}", NaturalTTSClient.voiceName);
    }

    @Override
    public void terminate() {
        try {
            sapObj.safeRelease();
        } finally {
            ComThread.Release();
        }
    }

    @Override
    public Sentence process(String s) throws Exception {
        byte[] bytes = textToSpeech(s);
        Sentence.Word word = new Sentence.Word(s, 0, bytes.length);
        return new Sentence(s, bytes, List.of(word), AudioEncodeUtils.AudioFormat.WAV_22KHZ_16BIT);
    }

    @Override
    public boolean isActive() {
        return true;
    }
}