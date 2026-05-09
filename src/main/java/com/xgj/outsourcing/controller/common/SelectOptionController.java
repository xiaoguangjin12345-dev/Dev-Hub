package com.xgj.outsourcing.controller.common;

import com.xgj.outsourcing.common.response.APIResponse;
import com.xgj.outsourcing.pojo.vo.common.SelectOptionVO;
import com.xgj.outsourcing.service.common.SelectOptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/common")
@RequiredArgsConstructor
public class SelectOptionController {
    private final SelectOptionService selectOptionService;

    // 获取各类下拉框数据源
    // 不设置操作日志AOP，因为该请求明显很多且不构成业务安全
    @GetMapping("/{type}/options")
    public APIResponse<List<SelectOptionVO<Integer>>> getSelectOptions(@PathVariable String type){
        List<SelectOptionVO<Integer>> list = selectOptionService.getSelectOptions(type);
        return APIResponse.success(list, "下拉选项框获取成功");
    }

}
