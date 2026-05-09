package com.xgj.outsourcing.service.stats.listener;

import com.xgj.outsourcing.common.mq.RabbitConfig;
import com.xgj.outsourcing.pojo.dto.stats.DevCapabilityMsg;
import com.xgj.outsourcing.pojo.dto.stats.DevEfficiencyMsg;
import com.xgj.outsourcing.pojo.dto.stats.HoursAnalysisMsg;
import com.xgj.outsourcing.pojo.dto.stats.ProjectProgressMsg;
import com.xgj.outsourcing.service.stats.StatsHandleDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RabbitListener(queues = RabbitConfig.stats_data,
        containerFactory = RabbitConfig.stats_container_factory)
@RequiredArgsConstructor
public class StatsListener {
    private final StatsHandleDataService statsHandleDataService;

    // 获取项目进度大盘结果
    @RabbitHandler
    public void handleProjectProgress(ProjectProgressMsg msg){
        try{
            // 调用数据库聚合计算模块
            statsHandleDataService.handleProjectProgress(msg);

        }catch (Exception e){
            log.error("项目进度大盘结果获取失败", e);
        }

    }

    // 获取工时偏差分析结果
    @RabbitHandler
    public void handleHoursAnalysis(HoursAnalysisMsg msg){
        try{
            // 调用数据库聚合计算模块
            statsHandleDataService.handleHoursAnalysis(msg);

        }catch (Exception e){
            log.error("工时偏差分析结果获取失败", e);
        }
    }

    // 获取开发人员能力画像结果
    @RabbitHandler
    public void handleDevCapability(DevCapabilityMsg msg){
        try{
            // 调用数据库聚合计算模块
            statsHandleDataService.handleDevCapability(msg);

        }catch (Exception e){
            log.error("开发人员能力画像结果获取失败", e);
        }
    }

    // 获取开发人员效能结果
    @RabbitHandler
    public void handleDevEfficiency(DevEfficiencyMsg msg){
        // 获取Redis键
        String redisKey = msg.getRedisKey();
        try{
            // 调用数据库聚合计算模块
            statsHandleDataService.handleDevEfficiency(msg);

        }catch (Exception e){
            log.error("开发人员效能结果获取失败", e);
        }
    }

}
