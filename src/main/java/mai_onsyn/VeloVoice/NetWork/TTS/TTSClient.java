package mai_onsyn.VeloVoice.NetWork.TTS;

import mai_onsyn.VeloVoice.Text.Sentence;

public interface TTSClient {
    void connect() throws Exception;
    void close();
    Sentence process(String s) throws Exception;
}
