package com.xgj.outsourcing.enums.notice;

import lombok.Getter;

@Getter
public enum NoticeStatus {
    Unread((byte)1),
    Read((byte)2),
    Deleted((byte)3);

    private final byte value;

    // 构造方法
    NoticeStatus(byte value) {
        this.value = value;
    }

    // 根据数据库的字段类型，获取值
    public static NoticeStatus fromValue(byte value) {
        for (NoticeStatus status : values()) {
            if (status.value == value) {
                return status;
            }
        }
        return null;
    }
}
