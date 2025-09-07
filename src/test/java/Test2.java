import mai_onsyn.VeloVoice.Audio.AudioEncodeUtils;
import mai_onsyn.VeloVoice.NetWork.TTS.MultiTTSClient;
import mai_onsyn.VeloVoice.Text.Sentence;

import java.io.*;

public class Test2 {

    public static void main(String[] args) throws Exception {
        MultiTTSClient client = new MultiTTSClient();

        Sentence process = client.process("你好");

        FileOutputStream fos = new FileOutputStream("D:/Users/Desktop/test.wav");
        fos.write(AudioEncodeUtils.genWavHeader(process.audioByteArray().length, 24000, 1));
        fos.write(process.audioByteArray());
        fos.close();
    }
}
