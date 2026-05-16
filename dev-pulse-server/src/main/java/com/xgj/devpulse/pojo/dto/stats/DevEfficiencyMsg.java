package com.xgj.devpulse.pojo.dto.stats;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DevEfficiencyMsg {
    private Integer userId;
    private Byte role;
    private String redisKey;

    public DevEfficiencyMsg() { }

    public DevEfficiencyMsg(Integer userId, Byte role, String redisKey) {
        this.userId = userId;
        this.role = role;
        this.redisKey = redisKey;
    }

}
