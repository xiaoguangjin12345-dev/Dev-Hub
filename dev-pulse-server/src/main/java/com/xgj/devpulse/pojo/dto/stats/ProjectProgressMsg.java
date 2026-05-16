package com.xgj.devpulse.pojo.dto.stats;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ProjectProgressMsg {
    private Integer userId;
    private Byte role;
    private List<Integer> projectIds;
    private String redisKey;

    public ProjectProgressMsg() { }

    public ProjectProgressMsg(Integer userId, Byte role, List<Integer> projectIds, String redisKey) {
        this.userId = userId;
        this.role = role;
        this.projectIds = projectIds;
        this.redisKey = redisKey;
    }

}
