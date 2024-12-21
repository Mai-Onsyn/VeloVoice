package mai_onsyn.VeloVoice2.NetWork.TTS;

import mai_onsyn.VeloVoice2.Text.Sentence;

public interface TTSClient {
    void connect();
    void close();
    Sentence process(String s);
}
