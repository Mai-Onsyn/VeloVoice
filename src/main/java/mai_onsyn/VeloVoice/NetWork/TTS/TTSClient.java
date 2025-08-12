package mai_onsyn.VeloVoice.NetWork.TTS;

import mai_onsyn.VeloVoice.Text.Sentence;

public interface TTSClient {
    void establish() throws Exception;
    void terminate();
    Sentence process(String s) throws Exception;
    boolean isActive();
}
