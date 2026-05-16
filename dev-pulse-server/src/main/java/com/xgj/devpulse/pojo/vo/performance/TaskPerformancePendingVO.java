package com.xgj.devpulse.pojo.vo.performance;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TaskPerformancePendingVO {
    protected Integer perfId;
    protected String taskName;
    protected String devName;
    protected BigDecimal quality;
    protected BigDecimal efficiency;
}
