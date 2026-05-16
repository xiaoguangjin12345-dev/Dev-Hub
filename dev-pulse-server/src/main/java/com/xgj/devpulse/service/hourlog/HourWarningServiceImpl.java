package com.xgj.devpulse.service.hourlog;

import com.xgj.devpulse.mapper.TaskMapper;
import com.xgj.devpulse.pojo.entity.TaskEntity;
import com.xgj.devpulse.service.common.NoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class HourWarningServiceImpl implements HourWarningService {
    private final TaskMapper taskMapper;
    private final NoticeService noticeService;

    // 执行工时预警的判断，预警通知的发送
    @Async
    public void checkAndExecuteHourWarning(int taskId, double threshold){
        // 获取任务实体（获得任务名称、预估工时、实际工时等信息）
        TaskEntity task = taskMapper.getTaskEntity(taskId);
        if(task == null){
            // throw new BusinessException(404, "任务状态异常", true);
            log.error("任务状态异常");
            return;
        }
        // 预估工时为0 或 实际工时不超出阈值范围
        if (task.getEstimatedHours() == 0 || (double)task.getActualHours() < (double)task.getEstimatedHours() * threshold){
            // 不需要预警，直接返回操作成功
            return;
        }
        // 获取该任务的项目经理编号
        Integer pmId  = taskMapper.getPmIdByTaskId(taskId);
        if(pmId == null){
            // throw new BusinessException(404, "项目状态异常", true);
            log.error("项目状态异常");
            return;
        }
        // 计算实际工时超过比例
        double percent = task.getEstimatedHours() == 0 ? 0:(double)task.getActualHours()/(double)task.getEstimatedHours() * 100;

        // 通过notice服务，添加工时预警信息（异步）
        noticeService.createHourWarningNotice(pmId, task.getDevID(), task.getTaskName(), task.getEstimatedHours(), task.getActualHours(), percent);

    }

}
