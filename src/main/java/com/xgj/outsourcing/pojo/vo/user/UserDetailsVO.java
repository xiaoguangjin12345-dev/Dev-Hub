package com.xgj.outsourcing.pojo.vo.user;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder // 子类也要换成 SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UserDetailsVO extends UserListVO {
    // 开发人员特有信息
    private String resumeText;
    private LocalDateTime createTime;
}
