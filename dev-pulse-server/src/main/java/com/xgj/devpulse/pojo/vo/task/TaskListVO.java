package com.xgj.devpulse.pojo.vo.task;

import lombok.Data;

@Data
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
