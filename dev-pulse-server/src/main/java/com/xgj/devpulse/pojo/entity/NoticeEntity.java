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
 * 消息通知表
 * </p>
 *
 * @author XiaoGuangjin
 * @since 2026-04-23
 */
@Getter
@Setter
@TableName("notice")
public class NoticeEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 消息通知编号，主键，自增
     */
    @TableId(value = "NoticeID", type = IdType.AUTO)
    private Integer NoticeID;

    /**
     * 接收人员编号，外键，关联 User.UserID
     */
    @TableField("RecieverID")
    private Integer RecieverID;

    /**
     * 发送人员编号，外键，关联 User.UserID
     */
    @TableField("SenderID")
    private Integer SenderID;

    /**
     * 通知标题
     */
    @TableField("Title")
    private String Title;

    /**
     * 通知具体内容
     */
    @TableField("Content")
    private String Content;

    /**
     * 通知类型（1-系统通知, 2-审核结果, 3-任务邀请/申请, 4-工时预警, 5-验收结果, 6-其他）
     */
    @TableField("NoticeType")
    private Byte NoticeType;

    /**
     * 通知状态（1-未读, 2-已读, 3-已删除）
     */
    @TableField("Status")
    private Byte Status;

    /**
     * 消息产生时间
     */
    @TableField("CreateTime")
    private LocalDateTime CreateTime;
}
