package com.xgj.devpulse.pojo.vo.user;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UserDetailsVO extends UserListVO {
    // 开发人员特有信息
    private String resumeText;
    private LocalDateTime createTime;
}
