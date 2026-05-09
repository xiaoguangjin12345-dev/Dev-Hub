package com.xgj.outsourcing.service.hourlog;

import com.xgj.outsourcing.pojo.dto.hourlog.ActualHourLogMsg;
import org.springframework.transaction.annotation.Transactional;

public interface ExecuteHourLogSubmitService {
    // 执行工时填报
    boolean recordHourLog(ActualHourLogMsg msg);

}
