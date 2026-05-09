package com.xgj.outsourcing.pojo.vo.task;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder // 子类也要换成 SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TaskDetailsVO extends TaskListVO{
    private String taskDescription;
    private Integer actualHours;
    private LocalDateTime createTime;
    private LocalDateTime assignTime;
    private LocalDateTime finishTime;
}
