package com.xgj.outsourcing.service.common;

import com.xgj.outsourcing.pojo.vo.common.SelectOptionVO;

import java.util.List;

public interface SelectOptionService {

    // 获取下拉框列表
    List<SelectOptionVO<Integer>> getSelectOptions(String type);

}
