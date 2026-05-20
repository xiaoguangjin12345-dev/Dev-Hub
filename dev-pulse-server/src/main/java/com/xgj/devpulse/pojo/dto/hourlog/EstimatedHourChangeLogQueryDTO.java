package com.xgj.devpulse.pojo.dto.hourlog;

import com.xgj.devpulse.pojo.dto.common.BasePageDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class EstimatedHourChangeLogQueryDTO extends BasePageDTO {
    private String taskName;
    private String pmName;
    private LocalDate startDate;
    private LocalDate endDate;
}
