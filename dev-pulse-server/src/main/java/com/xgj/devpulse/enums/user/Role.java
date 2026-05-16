package com.xgj.devpulse.enums.user;

import lombok.Getter;

@Getter
public enum Role {
    None((byte)0),
    PMO((byte)1),
    PM((byte)2),
    DEV((byte)3),
    ADMIN((byte)4);

    private final byte value;

    // 构造方法
    Role(byte value) {
        this.value = value;
    }

    // 根据数据库的字段类型，获取值
    public static Role fromValue(byte value) {
        for (Role role : values()) {
            if (role.value == value) {
                return role;
            }
        }
        return null;
    }
}
