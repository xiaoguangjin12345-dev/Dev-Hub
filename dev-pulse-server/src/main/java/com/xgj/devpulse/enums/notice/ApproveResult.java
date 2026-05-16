package com.xgj.devpulse.enums.notice;

import lombok.Getter;

@Getter
public enum ApproveResult {
    Approved((byte)1),
    Rejected((byte)2);

    private final byte value;

    // 构造方法
    ApproveResult(byte value) {
        this.value = value;
    }

    // 根据数据库的字段类型，获取值
    public static ApproveResult fromValue(byte value) {
        for (ApproveResult result : values()) {
            if (result.value == value) {
                return result;
            }
        }
        return null;
    }
}