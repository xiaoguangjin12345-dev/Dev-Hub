package com.xgj.outsourcing.enums.taskapplication;

import lombok.Getter;

@Getter
public enum TaskApplicationStatus {
    Pending((byte)1),
    Approved((byte)2),
    Expired((byte)3);

    private final byte value;

    // 构造方法
    TaskApplicationStatus(byte value) {
        this.value = value;
    }

    // 根据数据库的字段类型，获取值
    public static TaskApplicationStatus fromValue(byte value) {
        for (TaskApplicationStatus status : values()) {
            if (status.value == value) {
                return status;
            }
        }
        return null;
    }
}
