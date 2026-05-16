package com.xgj.devpulse.common.exception;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthorizationException extends RuntimeException{
    private int code;
    private String msg;

    public AuthorizationException() {
        this.code = 403;
        this.msg = "权限不足";
    }
    public AuthorizationException(String msg) {
        this.code = 403;
        this.msg = msg;
    }
    public AuthorizationException(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

}
