package com.xgj.outsourcing.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 项目审批记录表
 * </p>
 *
 * @author XiaoGuangjin
 * @since 2026-04-23
 */
@Getter
@Setter
@TableName("project_approval")
public class ProjectApprovalEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 项目审批记录编号，主键自增
     */
    @TableId(value = "ApprovalID", type = IdType.AUTO)
    private Integer ApprovalID;

    /**
     * 项目编号，外键关联 Project.ProjectID
     */
    @TableField("ProjectID")
    private Integer ProjectID;

    /**
     * PMO编号，外键关联 User.UserID
     */
    @TableField("PMOID")
    private Integer PMOID;

    /**
     * 审批类型（1-立项审批, 2-结项审批）
     */
    @TableField("ApprovalType")
    private Byte ApprovalType;

    /**
     * 审批结果（1-通过, 2-驳回）
     */
    @TableField("Result")
    private Byte Result;

    /**
     * 审批意见
     */
    @TableField("Comment")
    private String Comment;

    /**
     * 审批时间
     */
    @TableField("ApprovalTime")
    private LocalDateTime ApprovalTime;
}
