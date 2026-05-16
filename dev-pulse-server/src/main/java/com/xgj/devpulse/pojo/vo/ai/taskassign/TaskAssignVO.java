package com.xgj.devpulse.pojo.vo.ai.taskassign;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TaskAssignVO {
    private String taskName;
    private String taskDescription;
    private Integer estimatedHours;
    private String skillAnalysis;

    public TaskAssignVO() { }

    public TaskAssignVO(String taskName, String taskDescription, Integer estimatedHours, String skillAnalysis) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.estimatedHours = estimatedHours;
        this.skillAnalysis = skillAnalysis;
    }

}