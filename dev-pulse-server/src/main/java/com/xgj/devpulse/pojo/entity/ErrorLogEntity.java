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
 * 系统异常日志表结构
 * </p>
 *
 * @author XiaoGuangjin
 * @since 2026-04-23
 */
@Getter
@Setter
@TableName("error_log")
public class ErrorLogEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 异常日志编号，主键，自增
     */
    @TableId(value = "LogID", type = IdType.AUTO)
    private Integer LogID;

    /**
     * 操作用户编号，外键，关联 User.UserID
     */
    @TableField("UserID")
    private Integer UserID;

    /**
     * 抛出异常的 API 接口地址
     */
    @TableField("EndPoint")
    private String EndPoint;

    /**
     * 简短的错误摘要信息
     */
    @TableField("ExceptionMessage")
    private String ExceptionMessage;

    /**
     * 详细的异常堆栈调用详情
     */
    @TableField("StackTrace")
    private String StackTrace;

    /**
     * 异常发生时间
     */
    @TableField("ErrorTime")
    private LocalDateTime ErrorTime;
}
