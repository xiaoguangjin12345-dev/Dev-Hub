package com.xgj.devpulse.service.aiapplication;

import com.alibaba.fastjson2.TypeReference;
import com.xgj.devpulse.common.cache.RedisService;
import com.xgj.devpulse.common.context.UserContext;
import com.xgj.devpulse.common.exception.AuthorizationException;
import com.xgj.devpulse.common.exception.BusinessException;
import com.xgj.devpulse.common.mq.RabbitConfig;
import com.xgj.devpulse.enums.common.ProcessStatus;
import com.xgj.devpulse.enums.user.Role;
import com.xgj.devpulse.mapper.ProjectMapper;
import com.xgj.devpulse.pojo.entity.ProjectEntity;
import com.xgj.devpulse.pojo.dto.ai.TaskAssignMsg;
import com.xgj.devpulse.pojo.vo.ai.taskassign.TaskAssignListResultVO;
import com.xgj.devpulse.pojo.vo.ai.taskassign.TaskAssignVO;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AITaskAssignServiceImpl implements AITaskAssignService {
    private final RedisService redisService;
    private final RabbitTemplate rabbit;
    private final ProjectMapper projectMapper;

    // 处理AI任务拆解请求，并构造Msg发送至队列
    // 返回Redis键
    public String aiTaskAssignRequest(Integer projectId, String userPrompt) {
        Integer userId = UserContext.getCurrentUserId();
        Byte role = UserContext.getCurrentRole().getValue();

        // 角色不是项目经理，不予操作
        if (role != Role.PM.getValue()) {
            throw new AuthorizationException(403, "没有操作权限");
        }

        // 查询项目信息
        ProjectEntity project = projectMapper.getProjectEntity(projectId);
        if (project == null) {                         // 不存在该项目
            throw new BusinessException(404, "不存在该项目", true);
        }
        if (!project.getPMID().equals(userId)) {       // 非本项目经理的项目，不予操作
            throw new AuthorizationException(403, "没有操作权限");
        }

        // 获取项目名称、项目描述
        String projectName = project.getProjectName();
        String projectDescription = project.getProjectDescription();

        // 设置Redis信息
        // 这里将生成的JSON字符串转化为MD5码，以便拼接成Key
        // 若短期内再次发起项目名称、项目描述、用户prompt完全相同的请求，可以实现秒开；这样可以实现成本与体验双赢
        String jsonHash = DigestUtils.md5DigestAsHex(userPrompt.getBytes(StandardCharsets.UTF_8));
        String redisKey = "ai:task-assign:project-id:" + projectId + ":" + jsonHash;

        // 如果命中Redis，直接返回
        if (redisService.hasKey(redisKey)) {
            redisService.expire(redisKey, 5 * 60);     // 延长TTL
            return redisKey;
        }

        // 构造请求（消息）体
        TaskAssignMsg request = TaskAssignMsg.builder()
                .redisKey(redisKey)
                .redisTtl(5 * 60)
                .projectName(projectName)
                .projectDescription(projectDescription)
                .userPrompt(userPrompt)
                .build();

        // 设置Redis键
        redisService.hashSet(redisKey, "status", ProcessStatus.Pending.getValue());
        redisService.hashSet(redisKey, "requestTime", LocalDateTime.now());

        // 发送队列，Python端接收
        rabbit.convertAndSend(RabbitConfig.AI_request_queue, request);

        return redisKey;
    }


    // AI任务拆解的结果查询（轮询）
    // 若结果为失败，直接throw，轮不到返回给前端200
    public TaskAssignListResultVO aiTaskAssignResponse(String redisKey) throws InterruptedException {
        // Redis获取结果
        Byte status = redisService.hashGet(redisKey, "status", Byte.class);
        // 结果为空或失败
        if (status == null || status == ProcessStatus.Fail.getValue()) {
            throw new BusinessException(404, "系统繁忙，请稍后再试", false);
        }

        List<TaskAssignVO> data = null;
        LocalDateTime resultTime = null;
        // 成功态下，获取数据与时间
        if (status == ProcessStatus.Success.getValue()) {
            data = redisService.hashGet(redisKey, "data", new TypeReference<List<TaskAssignVO>>() {
            });
            resultTime = redisService.hashGet(redisKey, "resultTime", LocalDateTime.class);
        }

        TaskAssignListResultVO vo = TaskAssignListResultVO.builder()
                .status(status)
                .results(data)
                .resultTime(resultTime)
                .build();

        return vo;
    }

}