package com.xgj.outsourcing.enums.common;

import lombok.Getter;

@Getter
public enum ProcessStatus {
    Pending((byte)1),
    Success((byte)2),
    Fail((byte)3);

    private final byte value;

    // 构造方法
    ProcessStatus(byte value) {
        this.value = value;
    }

    // 根据数据库的字段类型，获取值
    public static ProcessStatus fromValue(byte value) {
        for (ProcessStatus status : values()) {
            if (status.value == value) {
                return status;
            }
        }
        return null;
    }
}
