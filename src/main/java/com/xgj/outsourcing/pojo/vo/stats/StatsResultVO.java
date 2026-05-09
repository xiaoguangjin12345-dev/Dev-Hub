package com.xgj.outsourcing.pojo.vo.stats;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class StatsResultVO<T> {
    private Byte status;
    private LocalDateTime exportTime;
    private List<T> data;
    private Integer targetDevId;   // 开发人员能力画像专供

    public StatsResultVO() { }

    public StatsResultVO(Byte status) {
        this.status = status;
        this.exportTime = null;
        this.data = null;
        this.targetDevId = null;
    }

    public StatsResultVO(Byte status, LocalDateTime exportTime, List<T> data) {
        this.status = status;
        this.exportTime = exportTime;
        this.data = data;
        this.targetDevId = null;
    }

    public StatsResultVO(Byte status, LocalDateTime exportTime, List<T> data, Integer targetDevId) {
        this.status = status;
        this.exportTime = exportTime;
        this.data = data;
        this.targetDevId = targetDevId;
    }

}
