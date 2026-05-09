package com.xgj.outsourcing.service.hourlog.listener;

import com.xgj.outsourcing.common.mq.RabbitConfig;
import com.xgj.outsourcing.pojo.dto.hourlog.ActualHourLogMsg;
import com.xgj.outsourcing.service.hourlog.ExecuteHourLogSubmitService;
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
    private final ExecuteHourLogSubmitService executeHourLogSubmitService;

    // 接收工时填报请求并调用 工时填报执行模块 处理
    @RabbitHandler
    public void recordHourLog(ActualHourLogMsg msg) {
        try{
            // 调用 工时填报执行模块
            executeHourLogSubmitService.recordHourLog(msg);
        }catch (Exception e){
            log.error("工时日志填报失败", e);
        }

    }

}
