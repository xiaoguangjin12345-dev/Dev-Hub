package com.xgj.outsourcing.service.stats;

import com.xgj.outsourcing.pojo.dto.stats.DevCapabilityMsg;
import com.xgj.outsourcing.pojo.dto.stats.DevEfficiencyMsg;
import com.xgj.outsourcing.pojo.dto.stats.HoursAnalysisMsg;
import com.xgj.outsourcing.pojo.dto.stats.ProjectProgressMsg;

public interface StatsHandleDataService {
    // 获取项目进度大盘结果
    boolean handleProjectProgress(ProjectProgressMsg msg);
    // 获取工时偏差分析结果
    boolean handleHoursAnalysis(HoursAnalysisMsg msg);
    // 获取开发人员能力画像结果
    boolean handleDevCapability(DevCapabilityMsg msg);
    // 获取开发人员效能结果
    boolean handleDevEfficiency(DevEfficiencyMsg msg);

}
