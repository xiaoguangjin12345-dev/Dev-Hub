package com.xgj.devpulse.AITest;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.xgj.devpulse.AITest.DTO.OpenAIRequestDTO;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Service
public class AIServiceTest {
    public static final HttpClient AIClient = HttpClient.newBuilder()
                                            .connectTimeout(Duration.ofSeconds(10))
                                            .build();

    public String response(OpenAIRequestDTO msg){

        // 构造请求体JSON
        String json = JSON.toJSONString(msg);
        String apiKey = "-------------------------------";

        // 3. 构建请求
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .timeout(Duration.ofSeconds(60)) // AI 响应较慢，设置 60s 等待
                .build();

        // 4. 发送请求并获取结果
        try {
            // send 是同步阻塞的，sendAsync 是异步非阻塞的
            HttpResponse<String> response = AIClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
//                // 查看完整格式
//                System.out.println(response.body());
                // 获取json对象
                JSONObject respJson = JSON.parseObject(response.body());
                // 获取单条
                return respJson.getJSONArray("choices").getJSONObject(0)
                        .getJSONObject("message").getString("content");
            } else {
                return "HTTP 错误码: " + response.statusCode() + " 详情: " + response.body();
            }
        } catch (Exception e) {
            return "网络连接失败";
        }
    }

}


