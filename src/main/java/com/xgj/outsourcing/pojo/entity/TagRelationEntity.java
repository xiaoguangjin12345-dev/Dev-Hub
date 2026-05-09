package com.xgj.outsourcing.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 技能标签关联表
 * </p>
 *
 * @author XiaoGuangjin
 * @since 2026-04-23
 */
@Getter
@Setter
@TableName("tag_relation")
public class TagRelationEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 标签关联编号，主键，自增
     */
    @TableId(value = "RelationID", type = IdType.AUTO)
    private Integer RelationID;

    /**
     * 关联的标签编号，外键，关联 Dict_Tag.TagID
     */
    @TableField("TagID")
    private Integer TagID;

    /**
     * 关联对象类型（1-开发人员, 2-任务）
     */
    @TableField("TargetType")
    private Byte TargetType;

    /**
     * 关联对象编号（根据类型对应 UserID 或 TaskID）
     */
    @TableField("TargetID")
    private Integer TargetID;
}
