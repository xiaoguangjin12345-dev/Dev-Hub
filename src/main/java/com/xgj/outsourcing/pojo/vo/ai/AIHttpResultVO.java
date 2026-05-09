package com.xgj.outsourcing.pojo.vo.ai;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AIHttpResultVO {
    private Byte status;
    private LocalDateTime createTime;
    private LocalDateTime resultTime;
    private String resultStr;

    public AIHttpResultVO() { }

    public AIHttpResultVO(Byte status, LocalDateTime createTime) {
        this.status = status;
        this.createTime = createTime;
        this.resultTime = null;
        this.resultStr = null;
    }

    public AIHttpResultVO(Byte status, LocalDateTime createTime, LocalDateTime resultTime, String resultStr) {
        this.status = status;
        this.createTime = createTime;
        this.resultTime = resultTime;
        this.resultStr = resultStr;
    }

}


