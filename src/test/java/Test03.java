import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

public class Test03 {
    public static void main(String[] args) throws IOException {
        // Python Flask API 获取 Sec-MS-GEC 的地址
        URL url = new URL("http://192.168.0.10:5000/get-sec-gec");

        // 创建连接
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");

        // 获取响应
        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            System.out.println("Response: " + response);

            // 解析 JSON 响应
            JSONObject jsonResponse = JSONObject.parseObject(response.toString());
            if (jsonResponse.containsKey("Sec-MS-GEC")) {
                String secGecValue = jsonResponse.getString("Sec-MS-GEC");
                System.out.println("Sec-MS-GEC: " + secGecValue);
            } else {
                System.out.println("Error: " + jsonResponse.getString("message"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}