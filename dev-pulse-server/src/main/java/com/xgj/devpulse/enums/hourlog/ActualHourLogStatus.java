package com.xgj.devpulse.enums.hourlog;

import lombok.Getter;

@Getter
public enum ActualHourLogStatus {
    Editable((byte)1),
    Readonly((byte)2);

    private final byte value;

    // 构造方法
    ActualHourLogStatus(byte value) {
        this.value = value;
    }

    // 根据数据库的字段类型，获取值
    public static ActualHourLogStatus fromValue(byte value) {
        for (ActualHourLogStatus status : values()) {
            if (status.value == value) {
                return status;
            }
        }
        return null;
    }
}
