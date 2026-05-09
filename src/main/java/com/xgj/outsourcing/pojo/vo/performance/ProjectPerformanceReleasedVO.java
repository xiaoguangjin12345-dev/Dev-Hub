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
public class ProjectPerformanceReleasedVO extends ProjectPerformancePendingVO{
    private String pmoName;
    private BigDecimal pmoScore;
    private BigDecimal totalScore;
    private String comment;
    private LocalDateTime evaluateTime;
}
