package com.xgj.outsourcing.service.hourlog;

public interface HourWarningService {

    // 执行工时预警的判断，预警通知的发送（异步）
    void checkAndExecuteHourWarning(int taskId, double threshold);
}
