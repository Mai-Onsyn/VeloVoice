import mai_onsyn.VeloVoice.Main;
import mai_onsyn.VeloVoice.NetWork.TTS.EdgeTTSClient;
import mai_onsyn.VeloVoice.NetWork.TTS.FixedEdgeTTSClient;
import mai_onsyn.VeloVoice.NetWork.TTS.TTSClient;
import mai_onsyn.VeloVoice.NetWork.Voice;

import java.util.List;
import java.util.UUID;

public class Test02 {

    public static void main(String[] args) throws InterruptedException {

        new Main();
        EdgeTTSClient.setVoice(Voice.get("zh-CN-XiaoyiNeural"));

        TTSClient client = new FixedEdgeTTSClient();

        client.connect();
        System.out.println("连接成功");
        List<byte[]> bytes = client.sendText(UUID.randomUUID(), "Test");
        System.out.println("发送成功");

        //Thread.sleep(1000 * 60 * 10);

        System.out.println("关闭连接");
        client.close();

    }


}
