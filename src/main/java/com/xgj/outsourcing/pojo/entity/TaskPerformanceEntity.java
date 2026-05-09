package com.xgj.outsourcing.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 任务级绩效记录表
 * </p>
 *
 * @author XiaoGuangjin
 * @since 2026-04-23
 */
@Getter
@Setter
@TableName("task_performance")
public class TaskPerformanceEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 任务级绩效记录编号，主键，自增
     */
    @TableId(value = "TaskPerfID", type = IdType.AUTO)
    private Integer TaskPerfID;

    /**
     * 关联任务编号，外键，关联Task.TaskID
     */
    @TableField("TaskID")
    private Integer TaskID;

    /**
     * 评价人（PM）编号，外键，关联User.UserID
     */
    @TableField("PMID")
    private Integer PMID;

    /**
     * 被评价人（开发人员）编号，外键，关联User.UserID
     */
    @TableField("DevID")
    private Integer DevID;

    /**
     * 质量分
     */
    @TableField("Quality")
    private BigDecimal Quality;

    /**
     * 工时效率分
     */
    @TableField("Efficiency")
    private BigDecimal Efficiency;

    /**
     * PM主观评分
     */
    @TableField("PMScore")
    private BigDecimal PMScore;

    /**
     * 绩效总分
     */
    @TableField("TotalScore")
    private BigDecimal TotalScore;

    /**
     * 绩效评语
     */
    @TableField("Comment")
    private String Comment;

    /**
     * 绩效状态（1-未发布, 2-已发布）
     */
    @TableField("Status")
    private Byte Status;

    /**
     * 绩效评价/发布时间
     */
    @TableField("EvaluateTime")
    private LocalDateTime EvaluateTime;
}
