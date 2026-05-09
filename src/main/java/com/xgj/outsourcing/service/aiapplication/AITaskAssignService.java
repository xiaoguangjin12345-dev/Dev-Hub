package com.xgj.outsourcing.service.aiapplication;

import com.xgj.outsourcing.pojo.vo.ai.taskassign.AITaskAssignResultVO;

public interface AITaskAssignService {
    // 处理AI任务拆解请求，并构造Msg发送至队列
    String aiTaskAssignRequest(Integer projectId, String userPrompt);

    // AI任务拆解的结果查询（轮询）
    AITaskAssignResultVO aiTaskAssignResponse(String redisKey) throws InterruptedException;

}
