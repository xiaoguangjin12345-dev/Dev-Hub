package com.xgj.devpulse.controller;

import com.xgj.devpulse.common.operationlog.annotation.Log;
import com.xgj.devpulse.common.response.APIResponse;
import com.xgj.devpulse.pojo.dto.performance.PerformanceSubjectiveScoreDTO;
import com.xgj.devpulse.pojo.dto.performance.ProjectPerformanceQueryDTO;
import com.xgj.devpulse.pojo.vo.common.PageResultVO;
import com.xgj.devpulse.pojo.vo.performance.ProjectPerformancePendingVO;
import com.xgj.devpulse.pojo.vo.performance.ProjectPerformanceReleasedVO;
import com.xgj.devpulse.service.performance.PerformanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/projects/performances")
@RequiredArgsConstructor
public class ProjectPerformanceController {
    @Qualifier("projectPerformanceService")
    private final PerformanceService performanceService;

    // PMO提交主观评分
    @Log("提交项目绩效主观分")
    @PutMapping("/{id}")
    public APIResponse<Boolean> submitSubjectiveScore(@PathVariable Integer id,
                                                      @RequestBody PerformanceSubjectiveScoreDTO dto){
        performanceService.updatePerformanceScore(id, dto);
        return APIResponse.success(true, "项目级绩效主观评分提交成功");
    }

    // 查询待评分项目级绩效列表
    @Log("查询待评分项目绩效")
    @GetMapping("/pending")
    public APIResponse<PageResultVO<ProjectPerformancePendingVO>> getPendingPerformances(@RequestParam Integer pageNum,
                                                                                 @RequestParam Integer pageSize){
        PageResultVO<ProjectPerformancePendingVO> perfs = performanceService.getPendingPerformances(pageNum, pageSize);
        return APIResponse.success(perfs, "待评分项目级绩效列表查询成功");
    }

    // 查询已发布项目级绩效列表
    @Log("查询已发布项目绩效")
    @GetMapping("/")
    public APIResponse<PageResultVO<ProjectPerformanceReleasedVO>> getReleasedPerformances(@ModelAttribute ProjectPerformanceQueryDTO dto){
        PageResultVO<ProjectPerformanceReleasedVO> perfs = performanceService.getReleasedPerformances(dto);
        return APIResponse.success(perfs, "已发布项目级绩效列表查询成功");
    }

}
