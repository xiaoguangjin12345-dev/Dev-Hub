package com.xgj.devpulse.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 开发人员简历表
 * </p>
 *
 * @author XiaoGuangjin
 * @since 2026-04-23
 */
@Getter
@Setter
@TableName("dev_profile")
public class DevProfileEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 简历编号，主键自增
     */
    @TableId(value = "ProfileID", type = IdType.AUTO)
    private Integer ProfileID;

    /**
     * 开发人员编号，对应User表的UserID
     */
    @TableField("UserID")
    private Integer UserID;

    /**
     * 是否首次登录（1-是, 2-否）
     */
    @TableField("IsFirst")
    private Byte IsFirst;

    /**
     * 个人简历
     */
    @TableField("ResumeText")
    private String ResumeText;

    /**
     * 个人技能标签
     */
    @TableField("Skills")
    private String Skills;
}
