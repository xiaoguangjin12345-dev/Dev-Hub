package com.xgj.devpulse.pojo.dto.ai;

import lombok.Data;

@Data
public class AIRedisDTO<T> {
    private String redisKey;
    private int redisTtl;
    private Byte status;
    private T data;
}
