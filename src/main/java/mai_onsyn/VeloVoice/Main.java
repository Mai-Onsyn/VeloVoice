package mai_onsyn.VeloVoice;

import javafx.application.Application;
import mai_onsyn.VeloVoice.NetWork.TTS.FixedEdgeTTSClient;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import static mai_onsyn.VeloVoice.App.Runtime.*;

public class Main {


    private static final Logger log = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        init();
        Application.launch(FrameApp.class);
    }


    private static void init() {
        FixedEdgeTTSClient.setVoice(edgeTTSConfig.getString("SelectedModel"));
        FixedEdgeTTSClient.setVoicePitch(edgeTTSConfig.getDouble("VoicePitch"));
        FixedEdgeTTSClient.setVoiceRate(edgeTTSConfig.getDouble("VoiceRate"));
        FixedEdgeTTSClient.setVoiceVolume(edgeTTSConfig.getDouble("VoiceVolume"));

        Configurator.setRootLevel(Level.toLevel(config.getString("LogLevel"), Level.INFO));
    }

}
