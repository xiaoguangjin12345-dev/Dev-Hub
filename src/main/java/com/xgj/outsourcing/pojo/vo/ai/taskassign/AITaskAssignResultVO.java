package com.xgj.outsourcing.pojo.vo.ai.taskassign;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class AITaskAssignResultVO{
    private Byte Status;
    private List<AITaskAssignVO> results;
    private LocalDateTime resultTime;

    public AITaskAssignResultVO() { }

    public AITaskAssignResultVO(Byte Status) {
        this.Status = Status;
        this.results = null;
        this.resultTime = null;
    }

    public AITaskAssignResultVO(Byte Status, List<AITaskAssignVO> results, LocalDateTime resultTime) {
        this.Status = Status;
        this.results = results;
        this.resultTime = resultTime;
    }

}

