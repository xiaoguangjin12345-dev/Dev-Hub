package com.xgj.outsourcing.enums.taskapplication;

import lombok.Getter;

@Getter
public enum TaskApplicationType {
    PM((byte)1),
    DEV((byte)2);

    private final byte value;

    // 构造方法
    TaskApplicationType(byte value) {
        this.value = value;
    }

    // 根据数据库的字段类型，获取值
    public static TaskApplicationType fromValue(byte value) {
        for (TaskApplicationType status : values()) {
            if (status.value == value) {
                return status;
            }
        }
        return null;
    }
}
