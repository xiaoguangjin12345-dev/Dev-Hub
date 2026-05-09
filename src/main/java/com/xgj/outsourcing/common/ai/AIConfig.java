package com.xgj.outsourcing.common.ai;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "ai-service")
public class AIConfig {
    // 需要填充的参数：messages（在应用层完成构建并序列化为数组字符串）
    public static String chatRequestTemplate = """
                                        {
                                            "model": "%%s",
                                            "messages": %s
                                        }
                                        """;

    // 需要填充的参数：messages（在应用层完成构建并序列化为数组字符串）
    //              tools（一个tool定义数组，应用层需要按照标准格式，写成json字符串）
    //              tool_choice（tool选定，应用层需要按照标准写json字符串，选定调用哪个tool）
    public static String toolCallRequestTemplate = """
                                                    {
                                                        "model": "%%s",
                                                        "messages": %s,
                                                        "tools": %s,
                                                        "tool_choice": %s
                                                    }
                                                    """;

    private Map<String, Map<String, Object>> providers;

    public Map<String, Map<String, Object>> getProviders() {
        return providers;
    }

    public void setProviders(Map<String, Map<String, Object>> providers) {
        this.providers = providers;
    }

}
