package com.xgj.devpulse.pojo.dto.ai;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TaskAssignMsg {
    private String redisKey;
    private int redisTtl;
    private String projectName;
    private String projectDescription;
    private String userPrompt;
}
