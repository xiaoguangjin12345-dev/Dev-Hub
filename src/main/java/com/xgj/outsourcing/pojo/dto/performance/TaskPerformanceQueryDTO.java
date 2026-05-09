package com.xgj.outsourcing.pojo.dto.performance;

import com.xgj.outsourcing.pojo.dto.common.BasePageDTO;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TaskPerformanceQueryDTO extends BasePageDTO {
    private String taskName;
    private String devName;
    private String pmName;

    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
