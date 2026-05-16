package com.xgj.devpulse.pojo.entity;

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
 * 任务评审记录表
 * </p>
 *
 * @author XiaoGuangjin
 * @since 2026-04-23
 */
@Getter
@Setter
@TableName("task_review")
public class TaskReviewEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 任务评审记录编号，主键，自增
     */
    @TableId(value = "ReviewID", type = IdType.AUTO)
    private Integer ReviewID;

    /**
     * 关联任务编号，外键，关联Task.TaskID
     */
    @TableField("TaskID")
    private Integer TaskID;

    /**
     * 开发人员编号，外键，关联User.UserID
     */
    @TableField("DevID")
    private Integer DevID;

    /**
     * 项目经理编号，外键，关联User.UserID
     */
    @TableField("PMID")
    private Integer PMID;

    /**
     * 交付物——Git链接
     */
    @TableField("GitUrl")
    private String GitUrl;

    /**
     * 交付物——代码压缩包链接
     */
    @TableField("ArchiveUrl")
    private String ArchiveUrl;

    /**
     * 交付物——文档链接
     */
    @TableField("DocUrl")
    private String DocUrl;

    /**
     * 当前提交的版次号
     */
    @TableField("Revision")
    private Integer Revision;

    /**
     * 评审结果（1-待评审, 2-通过, 3-返工）
     */
    @TableField("Result")
    private Byte Result;

    /**
     * 评审意见
     */
    @TableField("Comment")
    private String Comment;

    /**
     * 开发人员提交时间
     */
    @TableField("SubmitTime")
    private LocalDateTime SubmitTime;

    /**
     * 项目经理评审时间
     */
    @TableField("ReviewTime")
    private LocalDateTime ReviewTime;
}
