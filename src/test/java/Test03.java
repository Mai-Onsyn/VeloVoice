import mai_onsyn.VeloVoice2.App.Runtime;
import mai_onsyn.VeloVoice2.NetWork.TTS.FixedEdgeTTSClient;
import mai_onsyn.VeloVoice2.NetWork.TTS.FixedEdgeTTSClient;
import mai_onsyn.VeloVoice2.NetWork.TTSPool;
import mai_onsyn.VeloVoice2.Text.Sentence;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class Test03 {


    public static void main(String[] args) throws Exception {

        FixedEdgeTTSClient.setVoice("zh-CN-XiaoxiaoNeural");

        TTSPool client = new TTSPool(TTSPool.ClientType.EDGE, 2);
        client.connect();

        client.execute(List.of("a", "b", "c"), new File("D:/Users/Desktop"), "example");

        client.close();
//        Sentence sentence = client.process("你好，我是小智，很高兴为你服务。");
//
//
//        client.close();
//
//        FileOutputStream fos = new FileOutputStream("D:/Users/Desktop/a.mp3");
//        fos.write(sentence.getAudio());
//        fos.close();

    }
}