package com.xgj.devpulse.pojo.vo.performance;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProjectPerformancePendingVO {
    protected Integer perfId;
    protected String projectName;
    protected String pmName;
    protected BigDecimal resource;
    protected BigDecimal modify;
}
