package com.xgj.outsourcing.enums.performance;

import lombok.Getter;

@Getter
public enum PerformanceStatus {
    Pending((byte)1),
    Released((byte)2);

    private final byte value;

    // 构造方法
    PerformanceStatus(byte value) {
        this.value = value;
    }

    // 根据数据库的字段类型，获取值
    public static PerformanceStatus fromValue(byte value) {
        for (PerformanceStatus status : values()) {
            if (status.value == value) {
                return status;
            }
        }
        return null;
    }
}
