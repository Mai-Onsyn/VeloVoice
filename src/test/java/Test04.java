import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class Test04 {
    private static final String API_URL = "http://127.0.0.1:9966/tts";

    public static void main(String[] args) {
        try {
            // 构造查询参数
            String query = String.format(
                    "text=%s&prompt=%s&voice=%s&temperature=%.1f&top_p=%.1f&top_k=%d&refine_max_new_token=%s&infer_max_new_token=%s&skip_refine=%d&is_split=%d&custom_voice=%d",
                    URLEncoder.encode("检查音色是否正确，测试", StandardCharsets.UTF_8), // 文本
                    URLEncoder.encode("", StandardCharsets.UTF_8),    // prompt
                    URLEncoder.encode("1111", StandardCharsets.UTF_8), // voice
                    0.3, 0.7, 20, "384", "2048", 0, 1, 0
            );

            // 完整 URL
            String fullUrl = API_URL + "?" + query;
            System.out.println("Request URL: " + fullUrl);

            // 调用 API
            String response = callChatTTSAPI(fullUrl);
            System.out.println("Response: " + response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String callChatTTSAPI(String fullUrl) throws Exception {
        URL url = new URL(fullUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // 设置请求方法
        connection.setRequestMethod("GET");

        // 设置请求头
        connection.setRequestProperty("Accept", "application/json");

        // 读取响应
        int responseCode = connection.getResponseCode();
        System.out.println("Response Code: " + responseCode);

        if (responseCode == HttpURLConnection.HTTP_OK) {
            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
            }
            return response.toString();
        } else {
            throw new Exception("Error: HTTP " + responseCode);
        }
    }
}
