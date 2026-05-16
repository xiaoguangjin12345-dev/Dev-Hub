package com.xgj.devpulse.enums.taskreview;

import lombok.Getter;

@Getter
public enum TaskReviewResult {
    Pending((byte)1),
    Approved((byte)2),
    Rework((byte)3);

    private final byte value;

    // 构造方法
    TaskReviewResult(byte value) {
        this.value = value;
    }

    // 根据数据库的字段类型，获取值
    public static TaskReviewResult fromValue(byte value) {
        for (TaskReviewResult status : values()) {
            if (status.value == value) {
                return status;
            }
        }
        return null;
    }
}
