package com.xgj.outsourcing.common.exception;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthenticationException extends RuntimeException {
    private int code;
    private String msg;

    public AuthenticationException() {
        this.code = 401;
        this.msg = "用户未验证";
    }
    public AuthenticationException(String msg) {
        this.code = 401;
        this.msg = msg;
    }
    public AuthenticationException(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
