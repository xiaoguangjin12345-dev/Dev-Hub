package com.xgj.outsourcing.pojo.vo.project;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
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
