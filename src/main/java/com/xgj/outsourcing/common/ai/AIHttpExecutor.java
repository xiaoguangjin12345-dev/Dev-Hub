package com.xgj.outsourcing.common.ai;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.xgj.outsourcing.common.cache.RedisService;
import com.xgj.outsourcing.enums.common.ProcessStatus;
import com.xgj.outsourcing.pojo.dto.ai.AIChatMsg;
import com.xgj.outsourcing.pojo.dto.ai.AIToolCallMsg;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AIHttpExecutor {
    private final AIProviderResolver aiProviderResolver;
    private final RedisService redisService;
    // AI连接
    public static final HttpClient AIClient = HttpClient.newBuilder()
                                            .connectTimeout(Duration.ofSeconds(10))
                                            .build();

    // 文本请求的响应结果获取
    public boolean chatResponse(AIChatMsg aiMsg){
        try {
            // 调用封装模块，构建完整Http请求体
            HttpRequest request = this.getHttpRequest(aiMsg.getModelType(), aiMsg.getModelLevel(), aiMsg.getJsonRequest());
            // 发送请求并获取结果，同步阻塞
            HttpResponse<String> response = AIClient.send(request, HttpResponse.BodyHandlers.ofString());

            // 响应成功且状态码正确
            if (response.statusCode() == 200) {
                // 获取结果json对象
                JSONObject jsonResponse = JSON.parseObject(response.body());
                // 获取message的content，作为最终结果
                String result =  jsonResponse.getJSONArray("choices").getJSONObject(0)
                                .getJSONObject("message").getString("content");

                // 设置Redis成功信息、数据、TTL，以便轮询访问
                redisService.hashSet(aiMsg.getRedisKey(), "resultTime", LocalDateTime.now());
                redisService.hashSet(aiMsg.getRedisKey(), "resultStr", result);
                redisService.hashSet(aiMsg.getRedisKey(), "status", ProcessStatus.Success.getValue());
                redisService.expire(aiMsg.getRedisKey(), aiMsg.getRedisTTL());

                return true;

            } else {
                // 设置Redis失败信息
                redisService.hashSet(aiMsg.getRedisKey(), "status", ProcessStatus.Fail.getValue());
                redisService.expire(aiMsg.getRedisKey(), 60);
                // 抛出异常
                throw new RuntimeException("网络请求错误\nHTTP状态码：" + response.statusCode() + "\n详情：" + response.body());
            }
        } catch (Exception e) {
            // 设置Redis失败信息
            redisService.hashSet(aiMsg.getRedisKey(), "status", ProcessStatus.Fail.getValue());
            redisService.expire(aiMsg.getRedisKey(), 60);
            // 抛出异常
            throw new RuntimeException("AI文本请求响应失败", e);
        }
    }

    // ToolCall请求的响应结果获取
    public boolean toolCallResponse(AIToolCallMsg aiMsg){
        try {
            // 调用封装模块，构建完整Http请求体
            HttpRequest request = this.getHttpRequest(aiMsg.getModelType(), aiMsg.getModelLevel(), aiMsg.getJsonRequest());
            // 发送请求并获取结果,同步阻塞
            HttpResponse<String> response = AIClient.send(request, HttpResponse.BodyHandlers.ofString());

            // 响应成功且状态码正确
            if (response.statusCode() == 200) {
                // 获取结果的完整json对象
                JSONObject jsonResponse = JSON.parseObject(response.body());

                // 获取tool-call对象
                JSONObject toolCall = jsonResponse.getJSONArray("choices").getJSONObject(0)
                                      .getJSONObject("message").getJSONArray("tool_calls").getJSONObject(0);
                // 获取结果字符串
                String result = toolCall.getJSONObject("function").getString("arguments");

                // 设置Redis成功信息、数据、TTL，以便轮询访问
                redisService.hashSet(aiMsg.getRedisKey(), "resultTime", LocalDateTime.now());
                redisService.hashSet(aiMsg.getRedisKey(), "resultStr", result);
                redisService.hashSet(aiMsg.getRedisKey(), "status", ProcessStatus.Success.getValue());
                redisService.expire(aiMsg.getRedisKey(), aiMsg.getRedisTTL());

                return true;

            } else {
                // 设置Redis失败信息
                redisService.hashSet(aiMsg.getRedisKey(), "status", ProcessStatus.Fail.getValue());
                redisService.expire(aiMsg.getRedisKey(), 60);
                // 抛出异常
                throw new RuntimeException("网络请求错误\nHTTP状态码：" + response.statusCode() + "\n详情：" + response.body());
            }
        } catch (Exception e) {
            // 设置Redis失败信息
            redisService.hashSet(aiMsg.getRedisKey(), "status", ProcessStatus.Fail.getValue());
            redisService.expire(aiMsg.getRedisKey(), 60);
            // 抛出异常
            throw new RuntimeException("AI ToolCall请求响应失败", e);
        }
    }

    private HttpRequest getHttpRequest(String modelType, String modelLevel, String jsonRequest){
        // 获取该模型类型的 模型名 请求网址 与 API-Key
        String model = aiProviderResolver.getModel(modelType, modelLevel);
        String url = aiProviderResolver.getUrl(modelType);
        String apiKey = aiProviderResolver.getApiKey(modelType);

        // 将最终模型名填充至唯一预留字段
        jsonRequest = String.format(jsonRequest, model);

        // 构建请求体
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
                .timeout(Duration.ofSeconds(60))      // 响应较慢，设置60s等待
                .build();

        return request;
    }

}
