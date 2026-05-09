package com.xgj.outsourcing.enums.project;

import lombok.Getter;

@Getter
public enum ProjectStatus {
    Pending((byte)1),
    Revision((byte)2),
    Ongoing((byte)3),
    Closing((byte)4),
    Archived((byte)5),;

    private final byte value;

    // 构造方法
    ProjectStatus(byte value) {
        this.value = value;
    }

    // 根据数据库的字段类型，获取值
    public static ProjectStatus fromValue(byte value) {
        for (ProjectStatus status : values()) {
            if (status.value == value) {
                return status;
            }
        }
        return null;
    }
}
