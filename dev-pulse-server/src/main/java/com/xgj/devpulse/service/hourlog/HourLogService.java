package com.xgj.devpulse.service.hourlog;

import com.xgj.devpulse.pojo.dto.hourlog.*;
import com.xgj.devpulse.pojo.vo.common.PageResultVO;
import com.xgj.devpulse.pojo.vo.hourlog.ActualHourLogListVO;
import com.xgj.devpulse.pojo.vo.hourlog.EstimatedHourChangeLogListVO;

public interface HourLogService {
    // 开发人员 提交实际工时记录（接口只返回成功收到请求，具体计入数据库由后台队列完成，返回UUID字符串）
    String recordActualHourLog(ActualHourLogSubmitDTO dto);
    // 提交工时填报后，轮询查询提交与处理结果
    boolean checkRecordActualHourLog(String redisKey);

    // 开发人员 修改已有的实际工时记录
    boolean updateActualHourLog(int logId, ActualHourLogUpdateDTO dto);
    // 开发人员 删除已有的实际工时记录
    boolean deleteActualHourLog(int logId);
    // 参数化查询已有的实际工时记录
    PageResultVO<ActualHourLogListVO> getActualHourLogs(ActualHourLogQueryDTO dto);

    // 项目经理 修改任务预估工时
    boolean updateEstimatedHours(int taskId, EstimatedHourUpdateDTO dto);
    // 参数化查询任务预估工时修改记录
    PageResultVO<EstimatedHourChangeLogListVO> getEstimatedHourChangeLogs(EstimatedHourChangeLogQueryDTO dto);

}
