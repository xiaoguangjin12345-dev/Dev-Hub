package com.xgj.outsourcing.pojo.vo.performance;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@SuperBuilder // 子类也要换成 SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TaskPerformanceReleasedVO extends TaskPerformancePendingVO{
    private String pmName;
    private BigDecimal pmScore;
    private BigDecimal totalScore;
    private String comment;
    private LocalDateTime evaluateTime;
}
