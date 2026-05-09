package com.xgj.outsourcing.pojo.dto.common;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
public class BasePageDTO {
    // 页码
    protected Integer pageNum = 1;
    // 每页条数
    protected Integer pageSize = 10;
}
