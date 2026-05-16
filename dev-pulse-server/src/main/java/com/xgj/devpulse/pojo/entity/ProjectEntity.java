package com.xgj.devpulse.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 项目表
 * </p>
 *
 * @author XiaoGuangjin
 * @since 2026-04-23
 */
@Getter
@Setter
@TableName("project")
public class ProjectEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 项目编号，主键自增
     */
    @TableId(value = "ProjectID", type = IdType.AUTO)
    private Integer ProjectID;

    /**
     * 项目名称
     */
    @TableField("ProjectName")
    private String ProjectName;

    /**
     * 项目经理编号，外键关联 User.UserID
     */
    @TableField("PMID")
    private Integer PMID;

    /**
     * 状态（1-待审核, 2-待修改, 3-进行中, 4-待结项, 5-已归档）
     */
    @TableField("Status")
    private Byte Status;

    /**
     * 甲方代表姓名或公司名称
     */
    @TableField("ClientName")
    private String ClientName;

    /**
     * 甲方电子邮箱
     */
    @TableField("ClientEmail")
    private String ClientEmail;

    /**
     * 甲方联系电话
     */
    @TableField("ClientPhone")
    private String ClientPhone;

    /**
     * 项目描述
     */
    @TableField("ProjectDescription")
    private String ProjectDescription;

    /**
     * 预算金额
     */
    @TableField("Budget")
    private BigDecimal Budget;

    /**
     * 预计人力数量
     */
    @TableField("Personnel")
    private Integer Personnel;

    /**
     * 项目需求文档链接
     */
    @TableField("RequirementDocUrl")
    private String RequirementDocUrl;

    /**
     * 结项报告链接
     */
    @TableField("FinalReportUrl")
    private String FinalReportUrl;

    /**
     * 设定开始日期
     */
    @TableField("StartDate")
    private LocalDate StartDate;

    /**
     * 设定结束日期
     */
    @TableField("EndDate")
    private LocalDate EndDate;

    /**
     * 创建时间
     */
    @TableField("CreateTime")
    private LocalDateTime CreateTime;

    /**
     * 立项时间
     */
    @TableField("ApprovalTime")
    private LocalDateTime ApprovalTime;

    /**
     * 结项归档时间
     */
    @TableField("FinishTime")
    private LocalDateTime FinishTime;
}
