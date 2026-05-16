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
 * 操作日志表
 * </p>
 *
 * @author XiaoGuangjin
 * @since 2026-04-23
 */
@Getter
@Setter
@TableName("operation_log")
public class OperationLogEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 日志编号，主键，自增
     */
    @TableId(value = "LogID", type = IdType.AUTO)
    private Integer LogID;

    /**
     * 操作用户编号，外键，关联 User.UserID
     */
    @TableField("UserID")
    private Integer UserID;

    /**
     * 访问的 API 接口路径
     */
    @TableField("ApiRoute")
    private String ApiRoute;

    /**
     * 接口执行耗时（毫秒）
     */
    @TableField("ExecutionTime")
    private Integer ExecutionTime;

    /**
     * HTTP状态码（200, 401, 404等）
     */
    @TableField("StatusCode")
    private Integer StatusCode;

    /**
     * 操作者的 IP 地址
     */
    @TableField("IpAddress")
    private String IpAddress;

    /**
     * 具体操作时间
     */
    @TableField("CreateTime")
    private LocalDateTime CreateTime;
}
