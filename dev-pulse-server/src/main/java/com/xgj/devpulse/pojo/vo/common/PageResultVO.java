package com.xgj.devpulse.pojo.vo.common;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PageResultVO<T> {
    private long total;
    private List<T> list;

    public PageResultVO() { }

    public PageResultVO(long total, List<T> list) {
        this.total = total;
        this.list = list;
    }

}
