package com.xgj.devpulse.controller;

import com.xgj.devpulse.common.operationlog.annotation.Log;
import com.xgj.devpulse.common.response.APIResponse;
import com.xgj.devpulse.pojo.dto.performance.PerformanceSubjectiveScoreDTO;
import com.xgj.devpulse.pojo.dto.performance.TaskPerformanceQueryDTO;
import com.xgj.devpulse.pojo.vo.common.PageResultVO;
import com.xgj.devpulse.pojo.vo.performance.TaskPerformancePendingVO;
import com.xgj.devpulse.pojo.vo.performance.TaskPerformanceReleasedVO;
import com.xgj.devpulse.service.performance.PerformanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tasks/performances")
@RequiredArgsConstructor
public class TaskPerformanceController {
    @Qualifier("taskPerformanceService")
    private final PerformanceService performanceService;

    // 项目经理提交主观评分
    @Log("提交任务绩效主观分")
    @PutMapping("/{id}")
    public APIResponse<Boolean> submitSubjectiveScore(@PathVariable Integer id,
                                                      @RequestBody PerformanceSubjectiveScoreDTO dto){
        performanceService.updatePerformanceScore(id, dto);
        return APIResponse.success(true, "任务级绩效主观评分提交成功");
    }

    // 查询待评分任务级绩效列表
    @Log("查询待评分任务绩效")
    @GetMapping("/pending")
    public APIResponse<PageResultVO<TaskPerformancePendingVO>> getPendingPerformances(@RequestParam Integer pageNum,
                                                                                      @RequestParam Integer pageSize){
        PageResultVO<TaskPerformancePendingVO> perfs = performanceService.getPendingPerformances(pageNum, pageSize);
        return APIResponse.success(perfs, "待评分任务级绩效列表查询成功");
    }

    // 查询已发布任务级绩效列表
    @Log("查询已发布任务绩效")
    @GetMapping("/")
    public APIResponse<PageResultVO<TaskPerformanceReleasedVO>> getReleasedPerformances(@ModelAttribute TaskPerformanceQueryDTO dto){
        PageResultVO<TaskPerformanceReleasedVO> perfs = performanceService.getReleasedPerformances(dto);
        return APIResponse.success(perfs, "已发布任务级绩效列表查询成功");
    }

}
