package com.xgj.devpulse.pojo.dto.performance;

import com.xgj.devpulse.pojo.dto.common.BasePageDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ProjectPerformanceQueryDTO extends BasePageDTO {
    private String projectName;
    private String pmName;
    private String pmoName;

    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
