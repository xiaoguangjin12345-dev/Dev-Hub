package com.xgj.outsourcing.pojo.vo.performance;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Data
@SuperBuilder
@NoArgsConstructor
public class ProjectPerformancePendingVO {
    protected Integer perfId;
    protected String projectName;
    protected String pmName;
    protected BigDecimal resource;
    protected BigDecimal modify;
}
