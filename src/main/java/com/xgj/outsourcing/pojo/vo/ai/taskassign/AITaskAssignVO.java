package com.xgj.outsourcing.pojo.vo.ai.taskassign;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AITaskAssignVO {
    private String taskName;
    private String taskDescription;
    private Integer estimatedHours;
    private String skillAnalysis;

    public AITaskAssignVO() { }

    public AITaskAssignVO(String taskName, String taskDescription, Integer estimatedHours, String skillAnalysis) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.estimatedHours = estimatedHours;
        this.skillAnalysis = skillAnalysis;
    }

}