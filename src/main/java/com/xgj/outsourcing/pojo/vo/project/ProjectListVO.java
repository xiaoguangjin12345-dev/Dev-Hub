package com.xgj.outsourcing.pojo.vo.project;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
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
