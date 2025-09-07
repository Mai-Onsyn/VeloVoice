package mai_onsyn.VeloVoice;

import javafx.application.Application;
import mai_onsyn.AnimeFX.I18N;
import mai_onsyn.VeloVoice.NetWork.TTS.EdgeTTSClient;
import mai_onsyn.VeloVoice.NetWork.TTS.MultiTTSClient;
import mai_onsyn.VeloVoice.NetWork.TTS.NaturalTTSClient;
import mai_onsyn.VeloVoice.NetWork.TTS.ResumableTTSClient;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import java.util.List;
import java.util.Objects;

import static mai_onsyn.VeloVoice.App.Runtime.*;

public class Main {

    private static final Logger log = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        init();
        Application.launch(FrameApp.class);
    }

    private static void init() {
        I18N.setLanguage(windowConfig.getString("Language"));

        //Edge TTS
        EdgeTTSClient.setVoice(edgeTTSConfig.getString("SelectedModel"));
        EdgeTTSClient.setVoicePitch(edgeTTSConfig.getDouble("VoicePitch"));
        EdgeTTSClient.setVoiceRate(edgeTTSConfig.getDouble("VoiceRate"));
        EdgeTTSClient.setVoiceVolume(edgeTTSConfig.getDouble("VoiceVolume"));

        //Natural TTS
        if (isDllLoadable("jacob-1.21-x64")) {
            List<String> naturalTTSVoiceList = NaturalTTSClient.getVoiceList();
            if (naturalTTSVoiceList.isEmpty()) {
                log.error("No voice found in NaturalTTS");
                disableNaturalTTS = true;
            }
            else {
                String naturalTTSSelectedModel = naturalTTSConfig.getString("SelectedModel");
                if (!naturalTTSVoiceList.contains(naturalTTSSelectedModel)) {
                    String firstModel = naturalTTSVoiceList.getFirst();
                    NaturalTTSClient.setVoice(firstModel);
                    log.warn("Selected NaturalTTS voice not found, using first available voice: {}", firstModel);
                    naturalTTSConfig.setString("SelectedModel", firstModel);
                }
                else NaturalTTSClient.setVoice(naturalTTSSelectedModel);
            }
        } else {
            log.warn(I18N.getCurrentValue("log.main.warn.natural_tts_dll_not_loadable"));
            disableNaturalTTS = true;
        }
        NaturalTTSClient.setVoiceRate(naturalTTSConfig.getInteger("VoiceRate"));
        NaturalTTSClient.setVoiceVolume(naturalTTSConfig.getDouble("VoiceVolume"));

        //Multi TTS
        MultiTTSClient.setVoiceModel(multiTTSConfig.getString("VoiceModel"));
        MultiTTSClient.setVoiceRate(multiTTSConfig.getInteger("VoiceRate"));
        MultiTTSClient.setVoiceVolume(multiTTSConfig.getInteger("VoiceVolume"));
        MultiTTSClient.setVoicePitch(multiTTSConfig.getInteger("VoicePitch"));

        //Universal
        for (ResumableTTSClient.ClientType clientType : ResumableTTSClient.ClientType.values()) {
            if (Objects.equals(clientType.getName(), config.getString("TTSEngine"))) CLIENT_TYPE = clientType;
        }

        Configurator.setRootLevel(Level.toLevel(config.getString("LogLevel"), Level.INFO));
    }

    private static boolean isDllLoadable(String dllName) {
        try {
            System.loadLibrary(dllName);
            return true;
        } catch (UnsatisfiedLinkError e) {
            return false;
        }
    }

}
