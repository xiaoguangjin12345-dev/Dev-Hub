package com.xgj.devpulse.pojo.dto.stats;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HoursAnalysisMsg {
    private Integer userId;
    private Byte role;
    private String dimension;
    private String redisKey;

    public HoursAnalysisMsg() { }

    public HoursAnalysisMsg(Integer userId, Byte role, String dimension, String redisKey) {
        this.userId = userId;
        this.role = role;
        this.dimension = dimension;
        this.redisKey = redisKey;
    }

}
