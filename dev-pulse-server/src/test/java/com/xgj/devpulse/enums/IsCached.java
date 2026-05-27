package com.xgj.devpulse.enums;

import lombok.Getter;

@Getter
public enum IsCached {
    Cached((byte)1),
    NoCached((byte)2);

    private final byte value;

    // 构造方法
    IsCached(byte value) {
        this.value = value;
    }

    // 根据数据库的字段类型，获取值
    public static IsCached fromValue(byte value) {
        for (IsCached isCached : values()) {
            if (isCached.value == value) {
                return isCached;
            }
        }
        return null;
    }
}
