package com.xgj.devpulse.pojo.vo.stats;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class DevCapabilityVO {
    @ExcelProperty("技能标签")
    private String tagName;

    @ExcelProperty("平均质量分")
    private BigDecimal qualityAverage;

    @ExcelProperty("平均工时效率分")
    private BigDecimal efficiencyAverage;

    @ExcelProperty("平均绩效总分")
    private BigDecimal totalScoreAverage;

    @ExcelProperty("该标签完成任务数")
    private Integer taskCount;
}
