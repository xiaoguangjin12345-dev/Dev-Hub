package com.xgj.devpulse.controller;

import com.xgj.devpulse.common.operationlog.annotation.Log;
import com.xgj.devpulse.enums.common.ProcessStatus;
import com.xgj.devpulse.pojo.vo.stats.StatsResultVO;
import com.xgj.devpulse.pojo.vo.stats.DevCapabilityVO;
import com.xgj.devpulse.pojo.vo.stats.DevEfficiencyVO;
import com.xgj.devpulse.pojo.vo.stats.HoursAnalysisVO;
import com.xgj.devpulse.pojo.vo.stats.ProjectProgressVO;
import com.xgj.devpulse.service.stats.StatsService;
import com.xgj.devpulse.service.user.UserService;
import com.xgj.devpulse.service.common.FileService;
import com.xgj.devpulse.common.response.APIResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stats")
@RequiredArgsConstructor
public class StatsController {
    private final StatsService statsService;
    private final FileService fileService;
    private final UserService userService;

    // 项目进度大盘请求
    @Log("请求查询项目进度")
    @GetMapping("/project-progress")
    public APIResponse<String> projectProgress(@RequestParam List<Integer> projectIds) {
        String redisKey = statsService.requestProjectProgress(projectIds);
        return APIResponse.success(redisKey, "项目进度大盘请求提交成功");
    }

    // 项目进度大盘结果查询
    // 轮询较多，不设置日志记录
    @GetMapping("/project-progress/result")
    public APIResponse<StatsResultVO<ProjectProgressVO>> getProjectProgressResult(@RequestParam String redisKey) {
        StatsResultVO<ProjectProgressVO> result = statsService.projectProgressResponse(redisKey);
        String msg = (result.getStatus() == ProcessStatus.Success.getValue())?"项目进度大盘数据获取成功":"数据正在导出中";
        return APIResponse.success(result, msg);
    }


    // 工时偏差分析请求
    @Log("请求查询工时偏差")
    @GetMapping("/hour-analysis")
    public APIResponse<String> hoursAnalysis(@RequestParam byte dimension) {
        String redisKey = statsService.requestHoursAnalysis(dimension);
        return APIResponse.success(redisKey, "工时偏差分析请求提交成功");
    }

    // 工时偏差分析结果查询
    // 轮询较多，不设置日志记录
    @GetMapping("/hour-analysis/result")
    public APIResponse<StatsResultVO<HoursAnalysisVO>> getHoursAnalysisResult(@RequestParam String redisKey) {
        StatsResultVO<HoursAnalysisVO> result = statsService.hoursAnalysisResponse(redisKey);
        String msg = (result.getStatus() == ProcessStatus.Success.getValue())?"工时偏差分析数据获取成功":"数据正在导出中";
        return APIResponse.success(result, msg);
    }


    // 开发人员能力画像请求
    @Log("请求查询能力画像")
    @GetMapping("/dev-capability/{devId}")
    public APIResponse<String> devCapability(@PathVariable Integer devId) {
        String redisKey = statsService.requestDevCapability(devId);
        return APIResponse.success(redisKey, "开发人员能力画像请求提交成功");
    }

    // 开发人员能力画像结果查询
    // 轮询较多，不设置日志记录
    @GetMapping("/dev-capability/result")
    public APIResponse<StatsResultVO<DevCapabilityVO>> getDevCapabilityResult(@RequestParam String redisKey) {
        StatsResultVO<DevCapabilityVO> result = statsService.devCapabilityResponse(redisKey);
        String msg = (result.getStatus() == ProcessStatus.Success.getValue())?"开发人员能力画像数据获取成功":"数据正在导出中";
        return APIResponse.success(result, msg);
    }


    // 开发人员效能
    @Log("查询开发效能")
    @GetMapping("/dev-efficiency")
    public APIResponse<String> devEfficiency(){
        String redisKey = statsService.requestDevEfficiency();
        return APIResponse.success(redisKey, "开发人员效能请求提交成功");
    }

    // 开发人员效能结果查询
    // 轮询较多，不设置日志记录
    @GetMapping("/dev-efficiency/result")
    public APIResponse<StatsResultVO<DevEfficiencyVO>> getDevEfficiencyResult(@RequestParam String redisKey) {
        StatsResultVO<DevEfficiencyVO> result = statsService.devEfficiencyResponse(redisKey);
        String msg = (result.getStatus() == ProcessStatus.Success.getValue())?"开发人员效能数据获取成功":"数据正在导出中";
        return APIResponse.success(result, msg);
    }


    // 各统计数据报表导出接口
    @Log("导出项目进度报表")
    @GetMapping("/project-progress/excel")
    public void getProjectProgressExport(HttpServletResponse response){
        statsService.getProjectProgressExcel(response);
    }

    @Log("导出工时偏差报表")
    @GetMapping("/hour-analysis/excel")
    public void hoursAnalysis(@RequestParam byte dimension, HttpServletResponse response) {
        statsService.getHoursAnalysisExcel(dimension, response);
    }

    @Log("导出能力画像报表")
    @GetMapping("/dev-capability/{devId}/excel")
    public void devCapability(@PathVariable Integer devId, HttpServletResponse response) {
        statsService.getDevCapabilityExcel(devId, response);
    }

    @Log("导出开发效能报表")
    @GetMapping("/dev-efficiency/excel")
    public void devEfficiency(HttpServletResponse response){
        statsService.getDevEfficiencyExcel(response);
    }

}
