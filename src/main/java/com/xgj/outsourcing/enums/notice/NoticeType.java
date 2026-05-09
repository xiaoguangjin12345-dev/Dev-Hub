package com.xgj.outsourcing.enums.notice;

import lombok.Getter;

@Getter
public enum NoticeType {
    System((byte)1),
    ApproveResult((byte)2),
    TaskApplication((byte)3),
    HourWarning((byte)4),
    ReviewResult((byte)5),
    Other((byte)6);

    private final byte value;

    // 构造方法
    NoticeType(byte value) {
        this.value = value;
    }

    // 根据数据库的字段类型，获取值
    public static NoticeType fromValue(byte value) {
        for (NoticeType status : values()) {
            if (status.value == value) {
                return status;
            }
        }
        return null;
    }
}
