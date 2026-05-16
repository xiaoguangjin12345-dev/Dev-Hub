package com.xgj.devpulse.service.hourlog;

import com.xgj.devpulse.common.cache.RedisService;
import com.xgj.devpulse.common.exception.AuthorizationException;
import com.xgj.devpulse.common.exception.BusinessException;
import com.xgj.devpulse.enums.common.ProcessStatus;
import com.xgj.devpulse.mapper.TaskChangeLogMapper;
import com.xgj.devpulse.mapper.TaskMapper;
import com.xgj.devpulse.mapper.WorkLogMapper;
import com.xgj.devpulse.pojo.dto.hourlog.ActualHourLogMsg;
import com.xgj.devpulse.pojo.dto.hourlog.EstimatedHourUpdateDTO;
import com.xgj.devpulse.pojo.entity.TaskEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class HourLogTransactionalServiceImpl implements HourLogTransactionalService {
    private final WorkLogMapper workLogMapper;
    private final TaskChangeLogMapper taskChangeLogMapper;
    private final TaskMapper taskMapper;
    // Redis服务
    private final RedisService redisService;

    // 执行实际工时填报的事务控制区
    @Transactional
    public boolean executeActualHourLogSubmit(ActualHourLogMsg msg) {
        try{
            // 数据库添加工时记录
            workLogMapper.insertWorkLog(msg.getUserId(), msg.getDto(), LocalDateTime.now());
            // 任务实体中，总实际工时增加
            taskMapper.updateActualHoursById(msg.getDto().getTaskId(), msg.getDto().getHours());

            // 设置状态位为成功
            redisService.set(msg.getRedisKey(), ProcessStatus.Success.getValue(), 10 * 60);
            return true;

        }catch (Exception e){
            // 设置状态位为失败
            redisService.set(msg.getRedisKey(), ProcessStatus.Fail.getValue(), 10 * 60);
            throw new RuntimeException("工时日志填报失败");
        }
    }

    // 执行任务预估工时修改的事务控制区
    @Transactional
    public boolean executeEstimatedHourChange(Integer userId, Integer taskId, EstimatedHourUpdateDTO dto) {
        // 数据库查询任务实体
        TaskEntity task = taskMapper.getTaskEntity(taskId);
        // 数据库查询该任务对应项目所属的项目经理编号
        Integer pmId = taskMapper.getPmIdByTaskId(taskId);
        // 若查找不到该任务或其项目经理编号
        if(task == null || pmId == null){
            throw new BusinessException(404, "任务状态异常", true);
        }
        // 该任务不是该项目经理的，不予操作
        if(!pmId.equals(userId)){
            throw new AuthorizationException(403, "没有操作权限");
        }

        Integer oldHours = task.getEstimatedHours();
        // 数据库插入任务预估工时修改记录
        taskChangeLogMapper.insertTaskChangeLog
                (userId, taskId, task.getProjectID(), oldHours, dto.getNewHours(), dto.getUpdateReason(), LocalDateTime.now());
        // 数据库更新任务预估工时字段
        taskMapper.updateEstimatedHoursById(taskId, dto.getNewHours());

        return true;
    }

}
