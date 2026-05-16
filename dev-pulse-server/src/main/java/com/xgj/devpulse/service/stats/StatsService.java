package com.xgj.devpulse.service.stats;

import com.xgj.devpulse.pojo.vo.stats.StatsResultVO;
import com.xgj.devpulse.pojo.vo.stats.DevCapabilityVO;
import com.xgj.devpulse.pojo.vo.stats.DevEfficiencyVO;
import com.xgj.devpulse.pojo.vo.stats.HoursAnalysisVO;
import com.xgj.devpulse.pojo.vo.stats.ProjectProgressVO;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

public interface StatsService {
    // 项目进度大盘请求
    String requestProjectProgress(List<Integer> projectIds);
    // 项目进度大盘结果查询
    StatsResultVO<ProjectProgressVO> projectProgressResponse(String redisKey);

    // 工时偏差分析（预估工时与实际工时）请求
    String requestHoursAnalysis(byte dimensionNum);
    // 工时偏差分析结果查询
    StatsResultVO<HoursAnalysisVO> hoursAnalysisResponse(String redisKey);

    // 开发人员能力画像（按照开发人员参与过的任务的技能标签聚合分析）请求
    String requestDevCapability(int targetDevId);
    // 开发人员能力画像结果查询
    StatsResultVO<DevCapabilityVO> devCapabilityResponse(String redisKey);

    // 开发人员效能（任务完成数量、绩效平均分、总工时）
    String requestDevEfficiency();
    // 开发人员效能结果查询
    StatsResultVO<DevEfficiencyVO> devEfficiencyResponse(String redisKey);

    // 项目进度大盘Excel导出
    void getProjectProgressExcel(HttpServletResponse response);
    // 工时偏差分析Excel导出
    void getHoursAnalysisExcel(byte dimensionNum, HttpServletResponse response);
    // 开发人员能力画像Excel导出
    void getDevCapabilityExcel(Integer targetDevId, HttpServletResponse response);
    // 开发人员效能Excel导出
    void getDevEfficiencyExcel(HttpServletResponse response);

}
