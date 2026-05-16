package com.xgj.devpulse.common.exception;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BusinessException extends RuntimeException {
    private int code;
    private String msg;
    private boolean isLog;

    public BusinessException(){
        this.code = 500;
        this.msg = "系统异常";
        this.isLog = false;
    }

    public BusinessException(String msg){
        this.code = 500;
        this.msg = msg;
        this.isLog = false;
    }

    public BusinessException(int code, String msg) {
        this.code = code;
        this.msg = msg;
        this.isLog = false;
    }

    public BusinessException(int code, String msg, boolean isLog) {
        this.code = code;
        this.msg = msg;
        this.isLog = isLog;
    }

}
