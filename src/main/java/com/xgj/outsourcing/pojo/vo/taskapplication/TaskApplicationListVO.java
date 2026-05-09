package com.xgj.outsourcing.pojo.vo.taskapplication;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TaskApplicationListVO {
    private Integer applicationId;
    private Integer taskId;
    private String taskName;
    private String pmName;
    private String devName;
    // 问题：开发人员修改技能标签后，不会自动更新
    private String devSkills;
    private Byte type;
    private Byte status;
    private LocalDateTime applyTime;
    private LocalDateTime dealTime;

}
