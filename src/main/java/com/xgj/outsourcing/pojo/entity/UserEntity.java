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
 * 用户表
 * </p>
 *
 * @author XiaoGuangjin
 * @since 2026-04-23
 */
@Getter
@Setter
@TableName("user")
public class UserEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键，自增，用户唯一标识
     */
    @TableId(value = "UserID", type = IdType.AUTO)
    private Integer UserID;

    /**
     * 登录用户名，唯一
     */
    @TableField("Username")
    private String Username;

    /**
     * 登录密码（加密存储）
     */
    @TableField("Password")
    private String Password;

    /**
     * 用户真实姓名
     */
    @TableField("RealName")
    private String RealName;

    /**
     * 用户角色（1-PMO, 2-PM, 3-开发人员, 4-系统管理员）
     */
    @TableField("Role")
    private Byte Role;

    /**
     * 电子邮箱
     */
    @TableField("Email")
    private String Email;

    /**
     * 联系电话
     */
    @TableField("Phone")
    private String Phone;

    /**
     * 状态（1-待验证, 2-已验证, 3-未通过）
     */
    @TableField("Status")
    private Byte Status;

    /**
     * 注册时间
     */
    @TableField("CreateTime")
    private LocalDateTime CreateTime;
}
