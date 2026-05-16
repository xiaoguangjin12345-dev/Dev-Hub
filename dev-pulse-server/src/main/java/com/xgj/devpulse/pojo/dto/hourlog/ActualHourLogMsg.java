package com.xgj.devpulse.pojo.dto.hourlog;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ActualHourLogMsg {
    private String redisKey;
    private Integer userId;
    private Byte role;
    private ActualHourLogSubmitDTO dto;

    public ActualHourLogMsg() { }

    public ActualHourLogMsg(String redisKey, Integer userId, Byte role, ActualHourLogSubmitDTO dto) {
        this.redisKey = redisKey;
        this.userId = userId;
        this.role = role;
        this.dto = dto;
    }

}
