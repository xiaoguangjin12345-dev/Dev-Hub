package com.xgj.devpulse.pojo.dto.performance;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class SumHoursInfoDTO {
    private BigDecimal sumEstimatedHours;
    private BigDecimal sumActualHours;
}
