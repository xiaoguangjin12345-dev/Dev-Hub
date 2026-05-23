package com.xgj.devpulse.pojo.dto.ai;

import lombok.Data;

@Data
public class TokenUsageDTO {
    private Integer userId;
    private String modelName;
    private String ResponseId;
    private String type;
    private Integer promptTokens;
    private Integer cachedTokens;
    private Integer completionTokens;
    private Integer totalTokens;
}
