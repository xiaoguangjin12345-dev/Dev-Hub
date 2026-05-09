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
 * 任务预估工时修改记录表
 * </p>
 *
 * @author XiaoGuangjin
 * @since 2026-04-23
 */
@Getter
@Setter
@TableName("task_change_log")
public class TaskChangeLogEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 预估工时修改记录编号，主键，自增
     */
    @TableId(value = "ChangeID", type = IdType.AUTO)
    private Integer ChangeID;

    /**
     * 关联任务编号，外键，关联Task.TaskID
     */
    @TableField("TaskID")
    private Integer TaskID;

    /**
     * 关联项目编号，外键，关联Project.ProjectID
     */
    @TableField("ProjectID")
    private Integer ProjectID;

    /**
     * 项目经理编号，外键，关联User.UserID
     */
    @TableField("PMID")
    private Integer PMID;

    /**
     * 原工时
     */
    @TableField("OldHours")
    private Integer OldHours;

    /**
     * 修改后工时
     */
    @TableField("NewHours")
    private Integer NewHours;

    /**
     * 修改原因
     */
    @TableField("ChangeReason")
    private String ChangeReason;

    /**
     * 修改时间
     */
    @TableField("ChangeTime")
    private LocalDateTime ChangeTime;
}
