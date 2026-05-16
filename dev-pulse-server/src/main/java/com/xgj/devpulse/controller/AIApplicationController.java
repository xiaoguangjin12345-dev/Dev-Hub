package com.xgj.devpulse.controller;

import com.xgj.devpulse.common.response.APIResponse;
import com.xgj.devpulse.enums.common.ProcessStatus;
import com.xgj.devpulse.pojo.vo.ai.taskassign.TaskAssignListResultVO;
import com.xgj.devpulse.service.aiapplication.AITaskAssignService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AIApplicationController {
    private final AITaskAssignService aiTaskAssignService;

    // PM发起针对指定项目的任务拆解请求
    @GetMapping("/task-assign/{projectId}")
    public APIResponse<String> aiTaskAssignRequest(@PathVariable Integer projectId,
                                                   @RequestParam String prompt) {
        String redisKey = aiTaskAssignService.aiTaskAssignRequest(projectId, prompt);
        return APIResponse.success(redisKey, "AI任务拆解请求发起成功");
    }

    // 前端查询AI任务拆解结果（轮询）
    @GetMapping("/task-assign/result")
    public APIResponse<TaskAssignListResultVO> getAITaskAssignResult(@RequestParam String redisKey) throws InterruptedException {
        TaskAssignListResultVO result = aiTaskAssignService.aiTaskAssignResponse(redisKey);
        String msg = (result.getStatus() == ProcessStatus.Success.getValue())?"AI任务拆解结果获取成功":"AI结果正在生成中";
        return APIResponse.success(result, msg);
    }

}
