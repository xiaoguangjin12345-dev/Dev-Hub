package com.xgj.devpulse.pojo.dto.common;

import lombok.Data;

@Data
public class BasePageDTO {
    // 页码
    protected Integer pageNum = 1;
    // 每页条数
    protected Integer pageSize = 10;
}
