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
 * 技能标签表
 * </p>
 *
 * @author XiaoGuangjin
 * @since 2026-04-23
 */
@Getter
@Setter
@TableName("dict_tag")
public class DictTagEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 标签编号，主键，自增
     */
    @TableId(value = "TagID", type = IdType.AUTO)
    private Integer TagID;

    /**
     * 标签名称
     */
    @TableField("TagName")
    private String TagName;
}
