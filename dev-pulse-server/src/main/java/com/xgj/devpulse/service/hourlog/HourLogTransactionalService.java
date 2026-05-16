package com.xgj.devpulse.service.hourlog;

import com.xgj.devpulse.pojo.dto.hourlog.ActualHourLogMsg;
import com.xgj.devpulse.pojo.dto.hourlog.EstimatedHourUpdateDTO;

public interface HourLogTransactionalService {
    // 执行实际工时填报的事务控制区
    boolean executeActualHourLogSubmit(ActualHourLogMsg msg);

    // 执行任务预估工时修改的事务控制区
    boolean executeEstimatedHourChange(Integer userId, Integer taskId, EstimatedHourUpdateDTO dto);

}
