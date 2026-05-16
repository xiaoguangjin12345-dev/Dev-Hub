package com.xgj.devpulse.service.hourlog;

import com.xgj.devpulse.common.cache.RedisService;
import com.xgj.devpulse.common.context.UserContext;
import com.xgj.devpulse.common.exception.AuthorizationException;
import com.xgj.devpulse.common.exception.BusinessException;
import com.xgj.devpulse.common.mq.RabbitConfig;
import com.xgj.devpulse.enums.hourlog.ActualHourLogStatus;
import com.xgj.devpulse.enums.common.ProcessStatus;
import com.xgj.devpulse.enums.task.TaskStatus;
import com.xgj.devpulse.enums.user.Role;
import com.xgj.devpulse.mapper.TaskChangeLogMapper;
import com.xgj.devpulse.mapper.TaskMapper;
import com.xgj.devpulse.mapper.WorkLogMapper;
import com.xgj.devpulse.pojo.dto.hourlog.*;
import com.xgj.devpulse.pojo.dto.hourlog.ActualHourLogMsg;
import com.xgj.devpulse.pojo.entity.TaskEntity;
import com.xgj.devpulse.pojo.entity.WorkLogEntity;
import com.xgj.devpulse.pojo.vo.common.PageResultVO;
import com.xgj.devpulse.pojo.vo.hourlog.ActualHourLogListVO;
import com.xgj.devpulse.pojo.vo.hourlog.EstimatedHourChangeLogListVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class HourLogServiceImpl implements HourLogService {
    private final WorkLogMapper workLogMapper;
    private final TaskMapper taskMapper;
    private final TaskChangeLogMapper taskChangeLogMapper;
    private final HourLogTransactionalService hourLogTransactionalService;
    // Redis服务
    private final RedisService redis;
    // RabbitMQ服务
    private final RabbitTemplate rabbit;
    // 异步服务
    private final HourWarningService hourWarningService;
    private final RedisService redisService;

    // 开发人员 提交实际工时记录，返回Redis键供结果查询
    // 消费者类开启事务，这里不必开
    public String recordActualHourLog(ActualHourLogSubmitDTO dto){
        int userId = UserContext.getCurrentUserId();
        byte role = UserContext.getCurrentRole().getValue();
        // 只有开发人员可以操作
        if(role != Role.DEV.getValue()){
            throw new AuthorizationException(403, "没有操作权限");
        }

        // 查询任务实体
        TaskEntity task = taskMapper.getTaskEntity(dto.getTaskId());
        // 非本人的任务，不予操作
        if(task.getDevID() != userId){
            throw new AuthorizationException(403, "没有操作权限");
        }
        // 若该任务不在进行中状态，不予操作
        if(task.getStatus() != TaskStatus.Ongoing.getValue()){
            throw new BusinessException(500, "该任务不在进行中状态，不允许操作", false);
        }

        // 构造Redis键
        String redisKey = "hour-log:actual:submit:" + UUID.randomUUID().toString();
        // 构造工时填报消息体
        ActualHourLogMsg msg = new ActualHourLogMsg(redisKey, userId, role, dto);

        // Redis标记该请求为待处理
        redisService.set(msg.getRedisKey(), ProcessStatus.Pending.getValue());
        // 将消息体传入队列
        rabbit.convertAndSend(RabbitConfig.actual_hour_log, msg);

        // 返回Redis键
        return redisKey;
    }

    // 轮询查询实际工时提交结果
    public boolean checkRecordActualHourLog(String redisKey){
        // 获取状态值
        Byte result = redis.get(redisKey, Byte.class);
        if(result == null){
            throw new BusinessException(500, "状态已过期，请直接到工时日志页面查看是否有记录", false);
        }
        // 失败
        if(result == ProcessStatus.Fail.getValue()){
            throw new BusinessException(500, "工时日志提交失败", true);
        }
        // 如果为Success，返回true，说明填报成功；反之应该为Pending，说明尚未处理完成，需要前端继续轮询访问
        return result == ProcessStatus.Success.getValue();
    }


    // 开发人员 修改已有的实际工时记录
    @Transactional                 // 涉及多表操作，开启事务
    public boolean updateActualHourLog(int logId, ActualHourLogUpdateDTO dto){
        int userId = UserContext.getCurrentUserId();

        // 数据库查询该工时记录实体
        WorkLogEntity query = workLogMapper.getWorkLogByLogId(logId);
        // 查询不到该实际工时
        if(query == null){
            throw new BusinessException(404, "查询不到该记录", true);
        }
        // 记录非本人的工时记录，不予操作
        if(userId != query.getUserID()){
            throw new AuthorizationException(403, "没有操作权限");
        }
        // 只读的工时记录，不予操作
        if(query.getStatus() == ActualHourLogStatus.Readonly.getValue()){
            throw new BusinessException(500, "该工时已被锁定，不允许操作", false);
        }

        int oldHours = query.getHours();
        // 数据库更新工时记录
        workLogMapper.updateWorkLog(logId, userId, dto.getHours(), dto.getDescription(), LocalDateTime.now());
        // 任务实体中，总实际工时修改
        taskMapper.updateActualHoursById(query.getTaskID(), dto.getHours()-oldHours);

        // Redis删除相应任务详情键（异步）
        redisService.deleteTaskDetailsKey(query.getTaskID());
        // 调用工时预警的判断与执行模块（异步）
        hourWarningService.checkAndExecuteHourWarning(query.getTaskID(), 1);

        return true;
    }

    // 开发人员 删除已有的实际工时记录（修改为逻辑删除，非物理删除，本质是更新工时为0）
    @Transactional                 // 涉及多表操作，开启事务
    public boolean deleteActualHourLog(int logId){
        int userId = UserContext.getCurrentUserId();

        // 数据库查询该工时记录实体
        WorkLogEntity query = workLogMapper.getWorkLogByLogId(logId);
        // 查询不到该实际工时
        if(query == null){
            throw new BusinessException(404, "查询不到该记录", true);
        }
        // 记录非本人的工时记录，不予操作
        if(userId != query.getUserID()){
            throw new AuthorizationException(403, "没有操作权限");
        }
        // 只读的工时记录，不予操作
        if(query.getStatus() == ActualHourLogStatus.Readonly.getValue()){
            throw new BusinessException(500, "该工时已被锁定，不允许操作", false);
        }

        int oldHours = query.getHours();
        // 数据库设置工时数据为0，工作描述为空
        workLogMapper.updateWorkLog(logId, userId, 0, "", LocalDateTime.now());
        // 任务实体中，总实际工时减少
        taskMapper.updateActualHoursById(query.getTaskID(), -oldHours);

        // Redis删除相应任务详情键（异步）
        redisService.deleteTaskDetailsKey(query.getTaskID());

        return true;
    }

    // 参数化查询已有的实际工时记录（分页）
    public PageResultVO<ActualHourLogListVO> getActualHourLogs(ActualHourLogQueryDTO dto){
        int userId = UserContext.getCurrentUserId();
        byte role = UserContext.getCurrentRole().getValue();

        // 检查分页参数
        if(dto.getPageNum()<=0){
            throw new BusinessException(500, "分页参数错误", false);
        }
        // 计算页面查询偏移量
        int offset = (dto.getPageNum() - 1) * dto.getPageSize();

        // 数据库查询分页数据、总数（数据隔离下沉到Mapper）
        List<ActualHourLogListVO> result = workLogMapper.getWorkLogsByQuery(userId, role, dto, offset, dto.getPageSize());
        long total = workLogMapper.countWorkLogsByQuery(userId, role, dto);

        return new PageResultVO<ActualHourLogListVO>(total, result);
    }

    // 项目经理 修改任务预估工时
    // 事务下沉到专用类，这里不必开启
    public boolean updateEstimatedHours(int taskId, EstimatedHourUpdateDTO dto){
        int userId = UserContext.getCurrentUserId();

        // 调用 任务预估工时修改执行模块（核心）
        hourLogTransactionalService.executeEstimatedHourChange(userId, taskId, dto);

        // Redis删除相应任务详情键（异步）
        redisService.deleteTaskDetailsKey(taskId);
        // 调用工时预警的判断与执行模块
        hourWarningService.checkAndExecuteHourWarning(taskId, 1);

        return true;
    }

    // 参数化查询 任务预估工时修改记录
    public PageResultVO<EstimatedHourChangeLogListVO> getEstimatedHourChangeLogs(EstimatedHourChangeLogQueryDTO dto){
        int userId = UserContext.getCurrentUserId();
        byte role = UserContext.getCurrentRole().getValue();

        // 检查分页参数
        if(dto.getPageNum()<=0){
            throw new BusinessException(500, "分页参数错误", false);
        }
        // 计算页面查询偏移量
        int offset = (dto.getPageNum() - 1) * dto.getPageSize();

        // 数据库查询分页结果与总数（数据隔离下沉到Mapper）
        List<EstimatedHourChangeLogListVO> result = taskChangeLogMapper.getTaskChangeLogsByQuery(userId, role, dto, offset, dto.getPageSize());
        long total = taskChangeLogMapper.countTaskChangeLogsByQuery(userId, role, dto);

        return new PageResultVO<EstimatedHourChangeLogListVO>(total, result);
    }

}
