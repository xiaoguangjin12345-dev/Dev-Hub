package com.xgj.devpulse.enums.user;

import lombok.Getter;

@Getter
public enum UserStatus {
    PENDING((byte)1),
    VERIFIED((byte)2),
    REJECTED((byte)3);

    private final byte value;

    // 构造方法
    UserStatus(byte value) {
        this.value = value;
    }

    // 根据数据库的字段类型，获取值
    public static UserStatus fromValue(byte value) {
        for (UserStatus status : values()) {
            if (status.value == value) {
                return status;
            }
        }
        return null;
    }
}
