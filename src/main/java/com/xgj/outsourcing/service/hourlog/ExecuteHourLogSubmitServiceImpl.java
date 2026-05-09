package com.xgj.outsourcing.service.hourlog;

import com.xgj.outsourcing.common.cache.RedisService;
import com.xgj.outsourcing.enums.common.ProcessStatus;
import com.xgj.outsourcing.mapper.TaskMapper;
import com.xgj.outsourcing.mapper.WorkLogMapper;
import com.xgj.outsourcing.pojo.dto.hourlog.ActualHourLogMsg;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ExecuteHourLogSubmitServiceImpl implements ExecuteHourLogSubmitService {
    private final WorkLogMapper workLogMapper;
    private final TaskMapper taskMapper;
    // 异步服务
    private final HourWarningServiceImpl asyncHourLogService;
    // Redis服务
    private final RedisService redisService;

    // 执行工时填报
    @Transactional        // 涉及多表增改操作，开启事务
    public boolean recordHourLog(ActualHourLogMsg msg) {
        try{
            // 数据库添加工时记录
            workLogMapper.insertWorkLog(msg.getUserId(), msg.getDto(), LocalDateTime.now());
            // 任务实体中，总实际工时增加
            taskMapper.updateActualHoursById(msg.getDto().getTaskId(), msg.getDto().getHours());

            // Redis删除相应任务详情键（异步）
            redisService.deleteTaskDetailsKey(msg.getDto().getTaskId());
            // 调用工时预警的判断与执行模块（异步）
            asyncHourLogService.checkAndExecuteHourWarning(msg.getDto().getTaskId(), 1);

            // 设置状态位为成功
            redisService.set(msg.getRedisKey(), ProcessStatus.Success.getValue(), 10 * 60);
            return true;

        }catch (Exception e){
            // 设置状态位为失败
            redisService.set(msg.getRedisKey(), ProcessStatus.Fail.getValue(), 10 * 60);
            throw new RuntimeException("工时日志填报失败");
        }

    }
}
