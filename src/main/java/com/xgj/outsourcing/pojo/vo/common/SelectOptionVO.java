package com.xgj.outsourcing.pojo.vo.common;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SelectOptionVO<E> {
    private E value;
    private String label;

    // 无参构造函数，给MyBatis用
    public SelectOptionVO() { }

    public SelectOptionVO(E value, String label) {
        this.value = value;
        this.label = label;
    }

}
