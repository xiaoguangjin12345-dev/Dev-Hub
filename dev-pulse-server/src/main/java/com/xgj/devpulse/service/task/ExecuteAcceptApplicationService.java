package com.xgj.devpulse.service.task;

public interface ExecuteAcceptApplicationService {
    // 执行真正任务分配操作的临界区暨事务控制区
    boolean executeAcceptApplication(Integer taskId, Integer appId, Integer devId);
}
