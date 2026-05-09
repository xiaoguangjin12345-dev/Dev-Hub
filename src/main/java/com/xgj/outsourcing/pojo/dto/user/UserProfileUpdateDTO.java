package com.xgj.outsourcing.pojo.dto.user;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserProfileUpdateDTO {
    // 邮箱
    private String email;
    // 电话
    private String phone;
    // 个人简介
    private String resumeText;
    // 技能标签
    private List<Byte> skills;
}
