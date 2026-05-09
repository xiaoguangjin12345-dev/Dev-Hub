package com.xgj.outsourcing.pojo.vo.performance;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Data
@SuperBuilder
@NoArgsConstructor
public class TaskPerformancePendingVO {
    protected Integer perfId;
    protected String taskName;
    protected String devName;
    protected BigDecimal quality;
    protected BigDecimal efficiency;
}
