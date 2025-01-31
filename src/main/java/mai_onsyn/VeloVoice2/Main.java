package mai_onsyn.VeloVoice2;

import javafx.application.Application;
import mai_onsyn.VeloVoice2.NetWork.TTS.FixedEdgeTTSClient;

import static mai_onsyn.VeloVoice2.App.Runtime.*;

public class Main {

    public static void main(String[] args) {
        init();
        Application.launch(FrameApp.class);
    }


    private static void init() {
        FixedEdgeTTSClient.setVoice(edgeTTSConfig.getString("SelectedModel"));
        FixedEdgeTTSClient.setVoicePitch(edgeTTSConfig.getDouble("VoicePitch"));
        FixedEdgeTTSClient.setVoiceRate(edgeTTSConfig.getDouble("VoiceRate"));
        FixedEdgeTTSClient.setVoiceVolume(edgeTTSConfig.getDouble("VoiceVolume"));
    }

}
