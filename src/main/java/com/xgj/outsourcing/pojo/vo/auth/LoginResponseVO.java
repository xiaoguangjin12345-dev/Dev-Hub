package com.xgj.outsourcing.pojo.vo.auth;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponseVO {
    private String token;
    private int userId;
    private String userName;
    private Byte role;
    private String realName;
}
