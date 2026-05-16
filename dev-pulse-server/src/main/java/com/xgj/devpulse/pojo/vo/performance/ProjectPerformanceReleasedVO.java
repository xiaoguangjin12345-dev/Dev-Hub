package com.xgj.devpulse.pojo.vo.performance;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ProjectPerformanceReleasedVO extends ProjectPerformancePendingVO{
    private String pmoName;
    private BigDecimal pmoScore;
    private BigDecimal totalScore;
    private String comment;
    private LocalDateTime evaluateTime;
}
