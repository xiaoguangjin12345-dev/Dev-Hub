package com.xgj.outsourcing.pojo.vo.task;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
public class TaskListVO {
    protected Integer taskId;
    protected String taskName;
    protected Integer projectId;
    protected String projectName;
    protected Byte status;
    protected Integer estimatedHours;
    protected String requiredSkills;
    protected String pmName;
    protected String devName;
}
