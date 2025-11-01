import mai_onsyn.VeloVoice.Audio.AudioEncodeUtils;
import mai_onsyn.VeloVoice.NetWork.TTS.GPTSoVITSClient;
import mai_onsyn.VeloVoice.Text.Sentence;

import java.io.*;

public class Test2 {

    public static void main(String[] args) throws Exception {
        GPTSoVITSClient client = new GPTSoVITSClient();

        Sentence process = client.process("""
                我们讲到解一个m行
                n个未知数的方程
                这是一个m撑n的矩阵
                我们要把这些元素杀死
                每个家伙叫一个
                physical
                这下面就有一个家伙这家伙是一个实体的 就叫physical
                你发现这个事情很不寻常
                听明白了吗""");

        FileOutputStream fos = new FileOutputStream("D:/Users/Desktop/test.wav");
        fos.write(AudioEncodeUtils.genWavHeader(process.audioByteArray().length, 24000, 1));
        fos.write(process.audioByteArray());
        fos.close();
    }
}
