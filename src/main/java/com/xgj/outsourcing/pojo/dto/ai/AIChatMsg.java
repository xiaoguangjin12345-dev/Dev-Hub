package com.xgj.outsourcing.pojo.dto.ai;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AIChatMsg {
    private String redisKey;
    private Long redisTTL;
    private String modelType;
    private String modelLevel;
    private String jsonRequest;

    public AIChatMsg() { }

    public AIChatMsg(String redisKey, Long redisTTL, String modelType, String modelLevel, String jsonRequest) {
        this.redisKey = redisKey;
        this.redisTTL = redisTTL;
        this.modelType = modelType;
        this.modelLevel = modelLevel;
        this.jsonRequest = jsonRequest;
    }

}
