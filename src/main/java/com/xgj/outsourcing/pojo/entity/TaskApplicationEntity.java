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
 * 任务申请记录表
 * </p>
 *
 * @author XiaoGuangjin
 * @since 2026-04-23
 */
@Getter
@Setter
@TableName("task_application")
public class TaskApplicationEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 任务申请记录编号，主键，自增
     */
    @TableId(value = "ApplicationID", type = IdType.AUTO)
    private Integer ApplicationID;

    /**
     * 关联任务编号，外键，关联Task.TaskID
     */
    @TableField("TaskID")
    private Integer TaskID;

    /**
     * 项目经理编号，外键，关联User.UserID
     */
    @TableField("PMID")
    private Integer PMID;

    /**
     * 开发人员编号，外键，关联User.UserID
     */
    @TableField("DevID")
    private Integer DevID;

    /**
     * 发起方类型（1-PM邀请, 2-开发人员申请）
     */
    @TableField("Type")
    private Byte Type;

    /**
     * 状态（1-待处理, 2-已同意, 3-已失效）
     */
    @TableField("Status")
    private Byte Status;

    /**
     * 申请发起时间
     */
    @TableField("ApplyTime")
    private LocalDateTime ApplyTime;

    /**
     * 处理申请时间
     */
    @TableField("DealTime")
    private LocalDateTime DealTime;
}
