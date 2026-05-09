package com.xgj.outsourcing.pojo.vo.stats;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ProjectProgressVO {
    @ExcelProperty("项目编号")
//    @ExcelIgnore
    private Integer projectId;

    @ExcelProperty("项目名称")
    private String projectName;

    @ExcelProperty("项目状态")
    private String projectStatus;

    @ExcelProperty("总任务数")
    private Integer totalTaskCount;

    @ExcelProperty("已完成任务数")
    private Integer completedTaskCount;

    @ExcelProperty("项目完成进度")
    private BigDecimal completedTaskPercent;
}
