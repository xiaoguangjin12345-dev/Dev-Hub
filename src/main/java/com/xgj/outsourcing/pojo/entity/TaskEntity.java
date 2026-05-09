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
 * 任务表
 * </p>
 *
 * @author XiaoGuangjin
 * @since 2026-04-23
 */
@Getter
@Setter
@TableName("task")
public class TaskEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 任务编号，主键，自增
     */
    @TableId(value = "TaskID", type = IdType.AUTO)
    private Integer TaskID;

    /**
     * 关联项目编号，外键，关联 Project.ProjectID
     */
    @TableField("ProjectID")
    private Integer ProjectID;

    /**
     * 任务名称
     */
    @TableField("TaskName")
    private String TaskName;

    /**
     * 任务描述
     */
    @TableField("TaskDescription")
    private String TaskDescription;

    /**
     * 任务技能标签
     */
    @TableField("RequiredSkills")
    private String RequiredSkills;

    /**
     * 开发人员编号，外键，关联 User.UserID
     */
    @TableField("DevID")
    private Integer DevID;

    /**
     * 状态（1-待分配, 2-进行中, 3-待验收, 4-已完成）
     */
    @TableField("Status")
    private Byte Status;

    /**
     * 任务总版次
     */
    @TableField("Revision")
    private Integer Revision;

    /**
     * 预估工时
     */
    @TableField("EstimatedHours")
    private Integer EstimatedHours;

    /**
     * 实际工时
     */
    @TableField("ActualHours")
    private Integer ActualHours;

    /**
     * 创建时间
     */
    @TableField("CreateTime")
    private LocalDateTime CreateTime;

    /**
     * 成功匹配开发人员的时间
     */
    @TableField("AssignTime")
    private LocalDateTime AssignTime;

    /**
     * 完成时间
     */
    @TableField("FinishTime")
    private LocalDateTime FinishTime;
}
