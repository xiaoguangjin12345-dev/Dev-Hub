package com.xgj.devpulse.controller;

import com.xgj.devpulse.common.operationlog.annotation.Log;
import com.xgj.devpulse.common.response.APIResponse;
import com.xgj.devpulse.pojo.dto.hourlog.*;
import com.xgj.devpulse.pojo.vo.common.PageResultVO;
import com.xgj.devpulse.pojo.vo.hourlog.ActualHourLogListVO;
import com.xgj.devpulse.pojo.vo.hourlog.EstimatedHourChangeLogListVO;
import com.xgj.devpulse.service.hourlog.HourLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/hour-logs")
@RequiredArgsConstructor
public class HourLogController {
    private final HourLogService hourLogService;

    // 开发人员 填写实际工时
    @Log("填写工时")
    @PostMapping("/actual")
    public APIResponse<String> createActualLog(@RequestBody ActualHourLogSubmitDTO dto){
        String UUIDStr =  hourLogService.recordActualHourLog(dto);
        return APIResponse.success(UUIDStr, "已收到实际工时填写请求");
    }

    // 实际工时填写提交后，轮询查询存库结果
    // 轮询请求量大，不记录操作日志
    @GetMapping("/actual/check")
    public APIResponse<Boolean> checkCreateActualLog(@RequestParam String redisKey){
        Boolean result = hourLogService.checkRecordActualHourLog(redisKey);
        return APIResponse.success(result, result ? "实际工时填报成功" : "工时数据提交中");
    }

    // 开发人员 修改实际工时
    @Log("修改工时")
    @PutMapping("/actual/{id}")
    public APIResponse<Boolean> updateActualLog(@PathVariable Integer id,
                                                @RequestBody ActualHourLogUpdateDTO dto){
        hourLogService.updateActualHourLog(id, dto);
        return APIResponse.success(true, "实际工时修改成功");
    }

    // 开发人员 删除实际工时记录（逻辑）
    @Log("逻辑删除工时")
    @PutMapping("/actual/{id}/delete")
    public APIResponse<Boolean> deleteActualLog(@PathVariable Integer id){
        hourLogService.deleteActualHourLog(id);
        return APIResponse.success(true, "实际工时删除成功");
    }

    // 参数化查询实际工时记录
    @Log("查询工时记录列表")
    @GetMapping("/actual")
    public APIResponse<PageResultVO<ActualHourLogListVO>> getActualLogs(@ModelAttribute ActualHourLogQueryDTO dto){
        PageResultVO<ActualHourLogListVO> logs = hourLogService.getActualHourLogs(dto);
        return APIResponse.success(logs, "实际工时记录列表查询成功");
    }

    // 项目经理 修改任务预估工时
    @Log("修改预估工时")
    @PutMapping("/estimated/{taskId}")
    public APIResponse<Boolean> updateEstimatedHours(@PathVariable Integer taskId,
                                                     @RequestBody EstimatedHourUpdateDTO dto){
        hourLogService.updateEstimatedHours(taskId, dto);
        return APIResponse.success(true, "预估工时修改成功");
    }

    // 参数化查询任务预估工时修改记录
    @Log("查询预估工时修改")
    @GetMapping("/estimated")
    public APIResponse<PageResultVO<EstimatedHourChangeLogListVO>> getEstimatedHourChangeLogs(@ModelAttribute EstimatedHourChangeLogQueryDTO dto){
        PageResultVO<EstimatedHourChangeLogListVO> logs = hourLogService.getEstimatedHourChangeLogs(dto);
        return APIResponse.success(logs, "预估工时修改记录列表查询成功");
    }

}
