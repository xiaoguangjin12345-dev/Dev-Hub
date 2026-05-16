package com.xgj.devpulse.service.aiapplication;

import com.xgj.devpulse.pojo.vo.ai.taskassign.TaskAssignListResultVO;

public interface AITaskAssignService {
    // 处理AI任务拆解请求，并构造Msg发送至队列
    String aiTaskAssignRequest(Integer projectId, String userPrompt);

    // AI任务拆解的结果查询（轮询）
    TaskAssignListResultVO aiTaskAssignResponse(String redisKey) throws InterruptedException;

}
