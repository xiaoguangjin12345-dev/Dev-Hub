package com.xgj.devpulse.pojo.dto.hourlog;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EstimatedHourUpdateDTO {
    private Integer newHours;
    private String updateReason;
}
