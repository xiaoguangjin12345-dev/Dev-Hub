package com.xgj.devpulse.common.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class APIResponse<E> {
    private int code;
    private String msg;
    private E data;

    public APIResponse(int code, String msg, E data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
    // 成功返回（默认消息重载）
    public static<E> APIResponse<E> success(E data){
        return APIResponse.<E>builder()
                .code(200)
                .msg("操作成功")
                .data(data)
                .build();
    }
    // 成功返回（自定消息重载）
    public static<E> APIResponse<E> success(E data, String msg){
        return APIResponse.<E>builder()
                .code(200)
                .msg(msg)
                .data(data)
                .build();
    }

    // 失败返回
    public static<E> APIResponse<E> fail(int code, String msg){
        return APIResponse.<E>builder()
                .code(code)
                .msg(msg)
                .data(null)
                .build();
    }

}
