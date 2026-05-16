package com.xgj.devpulse.service.hourlog.listener;

import com.xgj.devpulse.common.cache.RedisService;
import com.xgj.devpulse.common.mq.RabbitConfig;
import com.xgj.devpulse.pojo.dto.hourlog.ActualHourLogMsg;
import com.xgj.devpulse.service.hourlog.HourLogTransactionalService;
import com.xgj.devpulse.service.hourlog.HourWarningService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@RabbitListener(queues = RabbitConfig.actual_hour_log,
        containerFactory = RabbitConfig.hour_log_submit_container_factory)
public class HourLogListener {
    private final HourLogTransactionalService hourLogTransactionalService;
    private final RedisService redisService;
    private final HourWarningService hourWarningService;

    // 接收工时填报请求并调用 工时填报执行模块 处理
    @RabbitHandler
    public void recordHourLog(ActualHourLogMsg msg) {
        try{
            // 调用 工时填报执行模块（核心）
            hourLogTransactionalService.executeActualHourLogSubmit(msg);

            // Redis删除相应任务详情键（异步）
            redisService.deleteTaskDetailsKey(msg.getDto().getTaskId());
            // 调用工时预警的判断与执行模块（异步）
            hourWarningService.checkAndExecuteHourWarning(msg.getDto().getTaskId(), 1);

        }catch (Exception e){
            log.error("工时日志填报失败", e);
        }

    }

}
