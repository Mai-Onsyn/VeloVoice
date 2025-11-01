package mai_onsyn.VeloVoice.NetWork.TTS;

import mai_onsyn.VeloVoice.Audio.AudioEncodeUtils;
import mai_onsyn.VeloVoice.Text.Sentence;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

import static mai_onsyn.VeloVoice.App.Runtime.*;

public class GPTSoVITSClient implements TTSClient {
    private static final Logger log = LogManager.getLogger(GPTSoVITSClient.class);

    @Override
    public void establish() throws Exception {

    }

    @Override
    public void terminate() {

    }

    @Override
    public Sentence process(String s) throws Exception {
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(config.getInteger("TimeoutSeconds")))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(buildUrl(s)))
                .GET()
                .build();
        try {
            HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
            byte[] bytes = response.body();
            byte[] redecoded = AudioEncodeUtils.wavRedecode(AudioEncodeUtils.parseWav(bytes), 24000, 16, 1);
            return new Sentence(
                    s,
                    redecoded,
                    List.of(new Sentence.Word(s, 0, redecoded.length)),
                    AudioEncodeUtils.AudioFormat.WAV_24KHZ_16BIT
            );
        } catch (Exception e) {
            log.error("GPT-SoVITS TTS request failed: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public boolean isActive() {
        return false;
    }

    private String buildUrl(String text) {
        String url = gptSoVITSConfig.getString("Url");
        StringBuilder sb = new StringBuilder(url);
        if (!url.startsWith("http")) sb.insert(0, "http://");
        if (!url.endsWith("/")) sb.append("/");
        sb.append("tts?");

        sb.append("text=").append(encode(text));
        sb.append("&text_lang=").append(gptSoVITSConfig.getString("TextLang"));
        sb.append("&ref_audio_path=").append(encode(gptSoVITSConfig.getString("RefAudioPath")));
        sb.append("&prompt_lang=").append(gptSoVITSConfig.getString("PromptLang"));
        sb.append("&prompt_text=").append(encode(gptSoVITSConfig.getString("PromptText")));
        sb.append("&top_k=").append(gptSoVITSConfig.getInteger("TopK"));
        sb.append("&top_p=").append(gptSoVITSConfig.getDouble("TopP"));
        sb.append("&temperature=").append(gptSoVITSConfig.getDouble("Temperature"));
        sb.append("&text_split_method=").append(gptSoVITSConfig.getString("TextSplitMethod"));
        sb.append("&batch_size=").append(gptSoVITSConfig.getInteger("BatchSize"));
        sb.append("&batch_threshold=").append(gptSoVITSConfig.getInteger("BatchThreshold"));
        sb.append("&split_bucket=").append(gptSoVITSConfig.getBoolean("SplitBucket"));
        sb.append("&speed_factor=").append(gptSoVITSConfig.getDouble("SpeedFactor"));
        sb.append("&fragment_interval=").append(gptSoVITSConfig.getDouble("FragmentInterval"));
        sb.append("&seed=").append(gptSoVITSConfig.getInteger("Seed"));
        sb.append("&media_type=").append(gptSoVITSConfig.getString("MediaType"));
        sb.append("&repetition_penalty=").append(gptSoVITSConfig.getDouble("RepetitionPenalty"));
        sb.append("&sample_steps=").append(gptSoVITSConfig.getInteger("SampleSteps"));
        sb.append("&super_sampling=").append(gptSoVITSConfig.getBoolean("SuperSampling"));

        return sb.toString();
    }

    private static String encode(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }
}
