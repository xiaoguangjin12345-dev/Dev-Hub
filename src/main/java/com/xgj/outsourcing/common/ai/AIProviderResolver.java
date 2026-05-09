package com.xgj.outsourcing.common.ai;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AIProviderResolver {

    private final AIConfig config;

    public AIProviderResolver(AIConfig config) {
        this.config = config;
    }

    public String getUrl(String type){
        Map<String, Object> provider = config.getProviders().get(type);
        return provider.get("url").toString();
    }

    public String getApiKey(String type){
        Map<String, Object> provider = config.getProviders().get(type);
        return provider.get("api_key").toString();
    }

    public String getModel(String type, String level){
        Map<String, Object> provider = config.getProviders().get(type);
        Map<String, Object> models = (Map<String, Object>)provider.get("models");

        return models.get(level).toString();

    }
}
