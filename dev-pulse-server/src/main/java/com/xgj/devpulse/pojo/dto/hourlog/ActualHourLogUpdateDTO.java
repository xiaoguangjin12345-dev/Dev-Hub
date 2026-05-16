package com.xgj.devpulse.pojo.dto.hourlog;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ActualHourLogUpdateDTO {
    private Integer hours;
    private String description;
}
