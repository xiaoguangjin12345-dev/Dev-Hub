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
 * 项目级绩效记录表
 * </p>
 *
 * @author XiaoGuangjin
 * @since 2026-04-23
 */
@Getter
@Setter
@TableName("project_performance")
public class ProjectPerformanceEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 项目级绩效记录编号，主键，自增
     */
    @TableId(value = "ProjectPerfID", type = IdType.AUTO)
    private Integer ProjectPerfID;

    @TableField("ProjectID")
    private Integer ProjectID;

    /**
     * 评价人（PMO）编号，外键，关联 User.UserID
     */
    @TableField("PMOID")
    private Integer PMOID;

    /**
     * 被评价人（PM）编号，外键，关联 User.UserID
     */
    @TableField("PMID")
    private Integer PMID;

    /**
     * 资源控制率得分
     */
    @TableField("Resource")
    private BigDecimal Resource;

    /**
     * 预估工时修改审计扣分
     */
    @TableField("Modify")
    private BigDecimal Modify;

    /**
     * PMO主观评分
     */
    @TableField("PMOScore")
    private BigDecimal PMOScore;

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
