package com.xgj.devpulse.pojo.vo.task;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TaskDetailsVO extends TaskListVO{
    private String taskDescription;
    private Integer actualHours;
    private LocalDateTime createTime;
    private LocalDateTime assignTime;
    private LocalDateTime finishTime;
}
