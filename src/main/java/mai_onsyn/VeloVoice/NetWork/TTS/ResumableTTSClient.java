package mai_onsyn.VeloVoice.NetWork.TTS;

import mai_onsyn.VeloVoice.Text.Sentence;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static mai_onsyn.VeloVoice.App.Runtime.CLIENT_TYPE;
import static mai_onsyn.VeloVoice.App.Runtime.config;

public class ResumableTTSClient implements TTSClient {

    private static final Logger log = LogManager.getLogger(ResumableTTSClient.class);

    public enum ClientType {
        EDGE("Edge TTS"),
        NATURAL("Natural TTS"),
        MULTI("Multi TTS"),
        GPT_SOVITS("GPT-SoVITS TTS");

        private final String name;

        ClientType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    private record ClientFactory(ClientType type) {
        public TTSClient createClient() {
            return switch (type) {
                case EDGE -> new EdgeTTSClient();
                case NATURAL -> new NaturalTTSClient();
                case MULTI -> new MultiTTSClient();
                case GPT_SOVITS -> new GPTSoVITSClient();
            };
        }
    }

    private TTSClient client;
    private final ClientFactory clientFactory;

    public ResumableTTSClient() {
        this(CLIENT_TYPE);
    }

    public ResumableTTSClient(ClientType clientType) {
        clientFactory = new ClientFactory(clientType);
        client = clientFactory.createClient();
    }

    @Override
    public void establish() throws Exception {
        int retry = 0;
        int maxRetries = config.getInteger("MaxRetries");
        while (retry++ <= maxRetries) {
            try {
                client.establish();
                return;
            } catch (Exception e) {
                log.debug("Failed to connect to EdgeTTS, retrying({})...", retry, e);
            }
        }
        throw new TTSException("Out of retries: " + retry);
    }

    @Override
    public void terminate() {
        client.terminate();
    }

    @Override
    public Sentence process(String s) throws Exception {
        int retry = 0;
        int maxRetries = config.getInteger("MaxRetries");
        while (retry++ <= maxRetries) {
            try {
                if (!client.isActive()) client.establish();
                return client.process(s);
            } catch (Exception e) {
                //if (retry == maxRetries) break;
                log.debug("Failed to processing, retrying({})...", retry, e);
                if (client.isActive()) client.terminate();
                client = clientFactory.createClient();
            }
        }
        throw new TTSException("Out of retries: " + retry);
    }

    @Override
    public boolean isActive() {
        return client.isActive();
    }
}
