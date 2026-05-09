package com.xgj.outsourcing.enums.project;

import lombok.Getter;

@Getter
public enum ProjectApproveType {
    Create((byte)1),
    Closure((byte)2);

    private final byte value;

    // 构造方法
    ProjectApproveType(byte value) {
        this.value = value;
    }

    // 根据数据库的字段类型，获取值
    public static ProjectApproveType fromValue(byte value) {
        for (ProjectApproveType type : values()) {
            if (type.value == value) {
                return type;
            }
        }
        return null;
    }
}
