package com.xgj.devpulse.pojo.vo.stats;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class DevEfficiencyVO {
    @ExcelProperty("开发人员编号")
//    @ExcelIgnore
    private Integer devId;

    @ExcelProperty("开发人员姓名")
    private String devName;

    @ExcelProperty("完成任务总数")
    private Integer finishedTasksCount;

    @ExcelProperty("平均绩效总分")
    private BigDecimal totalScoreAverage;

    @ExcelProperty("总工时")
    private BigDecimal actualHoursSum;
}
