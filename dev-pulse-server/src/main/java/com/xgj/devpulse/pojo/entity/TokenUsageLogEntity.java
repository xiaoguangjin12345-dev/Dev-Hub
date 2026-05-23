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
 * Token使用日志表
 * </p>
 *
 * @author XiaoGuangjin
 * @since 2026-05-22
 */
@Getter
@Setter
@TableName("Token_Usage_Log")
public class TokenUsageLogEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Token使用日志编号，主键，自增
     */
    @TableId(value = "LogID", type = IdType.AUTO)
    private Integer LogID;

    /**
     * 用户编号，外键，关联 User.UserID
     */
    @TableField("UserID")
    private Integer UserID;

    /**
     * 模型名称
     */
    @TableField("ModelName")
    private String ModelName;

    /**
     * 回答的唯一标识，即响应请求体的id字段
     */
    @TableField("ResponseID")
    private String ResponseID;

    /**
     * 业务类型
     */
    @TableField("Type")
    private String Type;

    /**
     * 输入Token
     */
    @TableField("PromptTokens")
    private Integer PromptTokens;

    /**
     * 缓存命中的Token
     */
    @TableField("CachedTokens")
    private Integer CachedTokens;

    /**
     * 输出Token
     */
    @TableField("CompletionTokens")
    private Integer CompletionTokens;

    /**
     * 总Token
     */
    @TableField("TotalTokens")
    private Integer TotalTokens;

    /**
     * 记录产生时间
     */
    @TableField("CreateTime")
    private LocalDateTime CreateTime;
}