package com.xgj.devpulse.service.task;

import com.xgj.devpulse.common.exception.BusinessException;
import com.xgj.devpulse.enums.task.TaskStatus;
import com.xgj.devpulse.enums.taskapplication.TaskApplicationStatus;
import com.xgj.devpulse.mapper.TaskApplicationMapper;
import com.xgj.devpulse.mapper.TaskMapper;
import com.xgj.devpulse.pojo.entity.TaskApplicationEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ExecuteAcceptApplicationServiceImpl implements ExecuteAcceptApplicationService {
    private final TaskApplicationMapper taskApplicationMapper;
    private final TaskMapper taskMapper;

    // 执行真正任务分配操作的临界区暨事务控制区
    @Transactional
    public boolean executeAcceptApplication(Integer taskId, Integer appId, Integer devId) {
        // 再确认一次，防止极端高并发的产生
        TaskApplicationEntity app = taskApplicationMapper.getTaskApplicationByAppId(appId);
        // 抛出异常，供外层接收
        if(app.getStatus() == TaskApplicationStatus.Expired.getValue()){
            throw new BusinessException(404, "该任务已被分配", false);
        }

        // 将该申请记录的状态，标记为 已同意
        taskApplicationMapper.updateTaskApplicationStatusByAppId
                (appId, TaskApplicationStatus.Approved.getValue(), LocalDateTime.now());
        // 将该记录对应任务的其他申请记录状态，标记为 已失效
        taskApplicationMapper.updateTaskApplicationStatusByTaskId
                (appId, taskId, TaskApplicationStatus.Expired.getValue(), LocalDateTime.now());

        // 更新任务实体的开发人员匹配信息
        taskMapper.updateTaskAssignInfo
                (taskId, devId, TaskStatus.Ongoing.getValue(), LocalDateTime.now());

        return true;
    }

}
