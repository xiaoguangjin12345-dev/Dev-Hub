package com.xgj.devpulse.controller;

import com.xgj.devpulse.common.operationlog.annotation.Log;
import com.xgj.devpulse.common.response.APIResponse;
import com.xgj.devpulse.pojo.dto.taskapplication.TaskInviteDTO;
import com.xgj.devpulse.pojo.vo.taskapplication.TaskApplicationListVO;
import com.xgj.devpulse.service.task.TaskApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskApplicationController {
    private final TaskApplicationService taskApplicationService;

    // 项目经理邀请/开发人员申请
    @Log("创建任务申请")
    @PostMapping("/{taskId}/applications")
    public APIResponse<Boolean> createApplication(@PathVariable Integer taskId,
                                                  @RequestBody TaskInviteDTO dto) {
        taskApplicationService.createTaskApplication(taskId, dto);
        return APIResponse.success(true, "任务申请提交成功");
    }

    // 项目经理同意申请/开发人员同意邀请
    @Log("同意任务申请")
    @PutMapping("/applications/{appId}")
    public APIResponse<Boolean> acceptApplication(@PathVariable Integer appId){
        taskApplicationService.acceptTaskApplication(appId);
        return APIResponse.success(true, "任务申请同意成功");
    }

    // 查询任务申请/邀请列表
    @Log("查看任务申请列表")
    @GetMapping("/applications")
    public APIResponse<List<TaskApplicationListVO>> getApplications(@RequestParam Byte type){
        List<TaskApplicationListVO> applications = taskApplicationService.getTaskApplicationList(type);
        return APIResponse.success(applications, "任务申请列表查询成功");
    }

}
