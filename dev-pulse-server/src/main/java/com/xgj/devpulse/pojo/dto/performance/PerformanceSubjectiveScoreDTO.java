package com.xgj.devpulse.pojo.dto.performance;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PerformanceSubjectiveScoreDTO {
    private BigDecimal score;
    private String comment;
}
