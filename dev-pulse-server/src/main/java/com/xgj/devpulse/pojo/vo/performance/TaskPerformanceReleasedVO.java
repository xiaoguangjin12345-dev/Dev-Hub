package com.xgj.devpulse.pojo.vo.performance;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TaskPerformanceReleasedVO extends TaskPerformancePendingVO{
    private String pmName;
    private BigDecimal pmScore;
    private BigDecimal totalScore;
    private String comment;
    private LocalDateTime evaluateTime;
}
