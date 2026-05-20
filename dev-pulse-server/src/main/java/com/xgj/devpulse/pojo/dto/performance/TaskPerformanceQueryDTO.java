package com.xgj.devpulse.pojo.dto.performance;

import com.xgj.devpulse.pojo.dto.common.BasePageDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TaskPerformanceQueryDTO extends BasePageDTO {
    private String taskName;
    private String devName;
    private String pmName;

    private LocalDate startDate;
    private LocalDate endDate;
}
