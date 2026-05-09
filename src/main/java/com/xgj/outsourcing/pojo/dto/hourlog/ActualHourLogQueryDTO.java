package com.xgj.outsourcing.pojo.dto.hourlog;

import com.xgj.outsourcing.pojo.dto.common.BasePageDTO;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.List;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ActualHourLogQueryDTO extends BasePageDTO {
    private String taskName;
    private String devName;
    // 时间区间
    private LocalDate startDate;
    private LocalDate endDate;
    // 工时记录状态（可修改、只读）
    private List<Byte> statuses;
    // 预留
    private Integer taskId;
    private Integer devId;
}
