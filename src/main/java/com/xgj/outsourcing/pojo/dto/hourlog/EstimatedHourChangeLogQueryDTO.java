package com.xgj.outsourcing.pojo.dto.hourlog;

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
public class EstimatedHourChangeLogQueryDTO extends BasePageDTO {
    private String taskName;
    private String pmName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
