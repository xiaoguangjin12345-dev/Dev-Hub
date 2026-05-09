package com.xgj.outsourcing.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 任务实际工时记录表
 * </p>
 *
 * @author XiaoGuangjin
 * @since 2026-04-23
 */
@Getter
@Setter
@TableName("work_log")
public class WorkLogEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 工时日志编号，主键，自增
     */
    @TableId(value = "LogID", type = IdType.AUTO)
    private Integer LogID;

    /**
     * 关联任务编号，外键，关联Task.TaskID
     */
    @TableField("TaskID")
    private Integer TaskID;

    /**
     * 开发人员编号，外键，关联User.UserID
     */
    @TableField("UserID")
    private Integer UserID;

    /**
     * 状态（1-可修改, 2-只读）
     */
    @TableField("Status")
    private Byte Status;

    /**
     * 工作日期
     */
    @TableField("WorkDate")
    private LocalDate WorkDate;

    /**
     * 投入工时
     */
    @TableField("Hours")
    private Integer Hours;

    /**
     * 工作内容描述
     */
    @TableField("Description")
    private String Description;

    /**
     * 最后修改时间
     */
    @TableField("LastTime")
    private LocalDateTime LastTime;
}
