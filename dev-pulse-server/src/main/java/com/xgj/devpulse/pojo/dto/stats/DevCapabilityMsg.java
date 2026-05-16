package com.xgj.devpulse.pojo.dto.stats;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DevCapabilityMsg {
    private Integer userId;
    private Byte role;
    private Integer targetDevId;
    private String redisKey;

    public DevCapabilityMsg() { }

    public DevCapabilityMsg(Integer userId, Byte role, Integer targetDevId, String redisKey) {
        this.userId = userId;
        this.role = role;
        this.targetDevId = targetDevId;
        this.redisKey = redisKey;
    }
}
