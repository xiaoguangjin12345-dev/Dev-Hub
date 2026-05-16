package com.xgj.devpulse.pojo.vo.hourlog;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class ActualHourLogListVO {
    private Integer logId;
    private Integer taskId;
    private String taskName;
    private Integer devId;
    private String devName;
    private LocalDate workDate;
    private Integer hours;
    private String description;
    private LocalDateTime lastTime;
    private Byte status;
}
