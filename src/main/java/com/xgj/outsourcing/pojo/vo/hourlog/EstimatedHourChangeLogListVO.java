package com.xgj.outsourcing.pojo.vo.hourlog;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class EstimatedHourChangeLogListVO {
    private Integer changeId;
    private String taskName;
    private String pmName;
    private Integer oldHours;
    private Integer newHours;
    private String changeReason;
    private LocalDateTime changeTime;
}
