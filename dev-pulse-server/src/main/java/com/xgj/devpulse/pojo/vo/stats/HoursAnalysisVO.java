package com.xgj.devpulse.pojo.vo.stats;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class HoursAnalysisVO {
    @ExcelProperty("维度名称")
    private String dimension;

    @ExcelProperty("预估工时总计")
    private Integer estimatedHoursSum;

    @ExcelProperty("实际工时总计")
    private Integer actualHoursSum;

    @ExcelProperty("绝对偏差值")
    private Integer variance;

    @ExcelProperty("相对偏差比例")
    private BigDecimal variancePercent;
}
