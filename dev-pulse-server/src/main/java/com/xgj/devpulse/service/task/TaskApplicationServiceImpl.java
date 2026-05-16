package com.xgj.devpulse.service.task;

import com.xgj.devpulse.common.cache.RedisService;
import com.xgj.devpulse.common.context.UserContext;
import com.xgj.devpulse.common.exception.AuthorizationException;
import com.xgj.devpulse.common.exception.BusinessException;
import com.xgj.devpulse.enums.taskapplication.TaskApplicationStatus;
import com.xgj.devpulse.enums.taskapplication.TaskApplicationType;
import com.xgj.devpulse.enums.user.Role;
import com.xgj.devpulse.mapper.TaskMapper;
import com.xgj.devpulse.pojo.dto.taskapplication.TaskInviteDTO;
import com.xgj.devpulse.pojo.entity.TaskApplicationEntity;
import com.xgj.devpulse.mapper.TaskApplicationMapper;
import com.xgj.devpulse.pojo.entity.TaskEntity;
import com.xgj.devpulse.pojo.vo.taskapplication.TaskApplicationListVO;
import com.xgj.devpulse.service.common.NoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskApplicationServiceImpl implements TaskApplicationService {
    private final ExecuteAcceptApplicationService executeAcceptApplicationService;
    private final TaskApplicationMapper taskApplicationMapper;
    private final TaskMapper taskMapper;
    private final NoticeService noticeService;
    // Redis服务
    private final RedisService redisService;
    // Redisson服务
    private final RedissonClient redissonClient;


    // 项目经理邀请/开发人员申请（优化双选流程版）
    @Transactional         // 涉及多表、多模块操作，添加事务
    public boolean createTaskApplication(int taskId, TaskInviteDTO dto){
        int userId = UserContext.getCurrentUserId();
        byte role = UserContext.getCurrentRole().getValue();

        // 必须是项目经理或开发人员才能操作
        if (role != Role.PM.getValue() && role != Role.DEV.getValue()){
            throw new AuthorizationException(403, "没有操作权限");
        }
        TaskEntity task = taskMapper.getTaskEntity(taskId);
        // 若查找不到任务记录，返回操作失败
        if(task == null){
            throw new BusinessException(404, "查找不到该任务", true);
        }

        // 填写发起方类型字段
        Byte type = (role == Role.PM.getValue()) ?
                TaskApplicationType.PM.getValue() : TaskApplicationType.DEV.getValue();
        // 设置方向相反的发起方变量，用于优化双选流程
        Byte oppositeType = (role == Role.PM.getValue()) ?
                TaskApplicationType.DEV.getValue() : TaskApplicationType.PM.getValue();

        // 项目经理邀请
        if (type == TaskApplicationType.PM.getValue()){
            // 尝试获取其他信息相同，但是发起方相反的任务申请记录编号
            Integer appId = taskApplicationMapper.getOppositeApplicationId
                    (taskId, userId, dto.getDevId(), oppositeType, TaskApplicationStatus.Pending.getValue());

            // 如果存在该记录，直接同意该申请即可
            if(appId != null){
                // 该模块内部包含锁，若对应任务被抢先一步分配，则该模块内部抛出异常，该模块的创建任务申请即为失败
                this.acceptTaskApplication(appId);

                // 直接添加成功的记录，便于存档
                // 若失败，则连同 成功分配涉及的所有表的增改操作，一起回滚
                taskApplicationMapper.insertSuccessApplication
                        (taskId, userId, dto.getDevId(), type, LocalDateTime.now());
            }
            // 不存在该记录
            else{
                // 添加任务邀请记录
                taskApplicationMapper.insertTaskApplication
                        (taskId, userId, dto.getDevId(), type, LocalDateTime.now());
                // 创建消息通知给开发人员（异步）
                noticeService.createPMInvitationNotice(dto.getDevId(), userId, task.getTaskName());
            }
        }
        // 开发人员申请
        else{
            // 根据任务编号查询项目经理
            Integer pmId = taskMapper.getPmIdByTaskId(taskId);
            // 查询有结果
            if (pmId != null){
                // 尝试获取其他信息相同，但是发起方相反的任务邀请记录编号
                Integer appId = taskApplicationMapper.getOppositeApplicationId
                        (taskId, pmId, userId, oppositeType, TaskApplicationStatus.Pending.getValue());

                // 如果存在该记录，直接同意该邀请即可
                if(appId != null){
                    // 该模块内部包含锁，若对应任务被抢先一步分配，则该模块内部抛出异常，该模块的创建任务申请即为失败
                    this.acceptTaskApplication(appId);

                    // 直接添加成功的记录，便于存档
                    // 若失败，则连同 成功分配涉及的所有表的增改操作，一起回滚
                    taskApplicationMapper.insertSuccessApplication
                            (taskId, pmId, userId, type, LocalDateTime.now());
                }
                // 不存在该记录
                else{
                    // 添加任务申请记录
                    taskApplicationMapper.insertTaskApplication
                            (taskId, pmId, userId, type, LocalDateTime.now());

                    // 创建消息通知给项目经理（异步）
                    noticeService.createDevApplicationNotice(pmId, userId, task.getTaskName());
                }
            }
            // 查询无结果，返回失败
            else{
                throw new BusinessException(500, "任务状态异常", true);
            }
        }
        // 消息通知的失败，不应影响主业务的结果
        return true;
    }


    // 开发人员接受邀请/项目经理接受申请
    // 真正的增改核心操作，在临界区类中，这里不必开启事务
    public boolean acceptTaskApplication(int appId) {
        int userId = UserContext.getCurrentUserId();
        byte role = UserContext.getCurrentRole().getValue();

        // 只有开发人员和项目经理可以操作
        if (role != Role.PM.getValue() && role != Role.DEV.getValue()){
            throw new AuthorizationException(403, "没有操作权限");
        }
        // 根据任务申请记录编号，在数据库中查询该实体
        TaskApplicationEntity query = taskApplicationMapper.getTaskApplicationByAppId(appId);

        // 如果这条任务申请记录不属于该用户，应该为失败
        if ((role == Role.PM.getValue() && userId != query.getPMID())
                || (role == Role.DEV.getValue() && userId != query.getDevID())){
            throw new AuthorizationException(403, "没有操作权限");
        }

        // 根据幂等性，此情况应该为操作成功
        if (query.getStatus() == TaskApplicationStatus.Approved.getValue()) {
            return true;
        }

        // 获取任务编号
        Integer taskId = query.getTaskID();
        // 一个任务，同一时刻，只允许被一条请求执行分配
        String lockKey = "lock:task-application:task-id:" + taskId;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            // 尝试获取锁
            boolean isLocked = lock.tryLock(0, -1, TimeUnit.SECONDS);
            if(!isLocked){
                throw new BusinessException(500, "该任务已被其他用户尝试分配", false);
            }
            // 执行临界区的操作
            executeAcceptApplicationService.executeAcceptApplication(taskId, appId, query.getDevID());
        }
        catch(InterruptedException e){
            // 恢复中断状态
            Thread.currentThread().interrupt();
            throw new RuntimeException("操作被系统中断");
        }
        catch (Exception e) {
            throw new BusinessException(500, "任务分配失败", true);
        }
        finally{
            // 若锁被当前线程持有，则释放锁
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }

        // Redis删除相应任务详情键（异步）
        // 潜在的风险：当这个模块被 优化双选版 的创建申请模块调用时，若下一步 直接添加成功记录 失败，那么该模块将被会回滚；此时删去的Redis记录其实是误删的
        // 但是，无伤大雅，因为该业务分支出现的概率本就不高，更别说出故障了；而且即使真误删了，也不过是一条任务的事
        redisService.deleteTaskDetailsKey(taskId);

        return true;
    }

    // 查看邀请/申请列表
    public List<TaskApplicationListVO> getTaskApplicationList(Byte type){
        int userId = UserContext.getCurrentUserId();
        byte role = UserContext.getCurrentRole().getValue();

        // 查询数据库，获得结果
        List<TaskApplicationListVO> result = taskApplicationMapper.getTaskApplications(userId, role, type);
        return result;
    }

}
