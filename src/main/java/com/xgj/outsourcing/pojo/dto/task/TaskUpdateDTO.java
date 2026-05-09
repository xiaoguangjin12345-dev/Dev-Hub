package com.xgj.outsourcing.pojo.dto.task;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TaskUpdateDTO {
    private Integer projectId;
    private String taskName;
    private String taskDescription;
    private List<Byte> requiredSkills;
    private Integer estimatedHours;
}
