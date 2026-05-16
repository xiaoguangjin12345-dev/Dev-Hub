package com.xgj.devpulse.pojo.vo.project;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ProjectDetailsVO extends ProjectListVO {
    private String clientEmail;
    private String clientPhone;
    private Integer personnel;
    private String requirementDocUrl;
//    // 聚合计算指标
//    private int taskCount;
//    private int completedTaskCount;
}
