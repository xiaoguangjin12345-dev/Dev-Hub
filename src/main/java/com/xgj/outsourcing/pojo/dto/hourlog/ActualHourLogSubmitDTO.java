package com.xgj.outsourcing.pojo.dto.hourlog;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class ActualHourLogSubmitDTO {
    private Integer taskId;
    private Integer hours;
    private String description;
    private LocalDate workDate;
}
