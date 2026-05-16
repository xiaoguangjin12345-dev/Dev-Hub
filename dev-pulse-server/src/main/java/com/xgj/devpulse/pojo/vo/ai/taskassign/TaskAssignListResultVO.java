package com.xgj.devpulse.pojo.vo.ai.taskassign;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class TaskAssignListResultVO {
    private Byte status;
    private List<TaskAssignVO> results;
    private LocalDateTime resultTime;

    public TaskAssignListResultVO() { }

    public TaskAssignListResultVO(Byte status) {
        this.status = status;
        this.results = null;
        this.resultTime = null;
    }

    public TaskAssignListResultVO(Byte status, List<TaskAssignVO> results, LocalDateTime resultTime) {
        this.status = status;
        this.results = results;
        this.resultTime = resultTime;
    }

}

