package com.xgj.devpulse.service.project;

public interface ExecuteProjectApproveService {

    // 执行立项审批的临界区暨事务控制区
    boolean executeCreateApprove(Integer userId, Integer projectId, Byte result, String reason);

    // 执行结项审批的临界区暨事务控制区
    boolean executeClosureApprove(Integer userId, Integer projectId, Byte result, String reason);
}
