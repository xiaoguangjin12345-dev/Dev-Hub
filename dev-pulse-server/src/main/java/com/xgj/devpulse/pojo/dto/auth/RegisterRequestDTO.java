package com.xgj.devpulse.pojo.dto.auth;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterRequestDTO {
    private String username;
    private String password;
    private String passwordConfirm;
    private String realName;
    private Byte role;
    private String email;
    private String phone;
}
