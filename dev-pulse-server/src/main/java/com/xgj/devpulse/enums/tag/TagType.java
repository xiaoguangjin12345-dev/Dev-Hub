package com.xgj.devpulse.enums.tag;

import lombok.Getter;

@Getter
public enum TagType {
    DEV((byte)1),
    TASK((byte)2);

    private final byte value;

    // 构造方法
    TagType(byte value) {
        this.value = value;
    }

    // 根据数据库的字段类型，获取值
    public static TagType fromValue(byte value) {
        for (TagType type : values()) {
            if (type.value == value) {
                return type;
            }
        }
        return null;
    }
}
