package com.xgj.devpulse.pojo.vo.project;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ProjectListVO {
    protected int projectId;
    protected String projectName;
    protected String clientName;
    protected String projectDescription;
    protected BigDecimal budget;
    protected Byte status;
    protected String pmName;
    protected LocalDate startDate;
    protected LocalDate endDate;
    protected LocalDateTime createTime;
}
