package com.xgj.outsourcing.service.aiapplication;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.xgj.outsourcing.common.ai.AIConfig;
import com.xgj.outsourcing.common.cache.RedisService;
import com.xgj.outsourcing.common.context.UserContext;
import com.xgj.outsourcing.common.exception.AuthorizationException;
import com.xgj.outsourcing.common.exception.BusinessException;
import com.xgj.outsourcing.common.mq.RabbitConfig;
import com.xgj.outsourcing.enums.common.ProcessStatus;
import com.xgj.outsourcing.enums.user.Role;
import com.xgj.outsourcing.mapper.ProjectMapper;
import com.xgj.outsourcing.pojo.dto.ai.AIToolCallMsg;
import com.xgj.outsourcing.pojo.entity.ProjectEntity;
import com.xgj.outsourcing.pojo.vo.ai.AIHttpResultVO;
import com.xgj.outsourcing.pojo.vo.ai.taskassign.AITaskAssignResultVO;
import com.xgj.outsourcing.pojo.vo.ai.taskassign.AITaskAssignVO;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AITaskAssignServiceImpl implements AITaskAssignService {
    private final RedisService redisService;
    private final RabbitTemplate rabbit;
    private final ProjectMapper projectMapper;

    // 处理AI任务拆解请求，并构造Msg发送至队列
    // 返回Redis键
    public String aiTaskAssignRequest(Integer projectId, String userPrompt){
        Integer userId = UserContext.getCurrentUserId();
        Byte role = UserContext.getCurrentRole().getValue();

        // 角色不是项目经理，不予操作
        if(role != Role.PM.getValue()){
            throw new AuthorizationException(403, "没有操作权限");
        }

        // 查询项目信息
        ProjectEntity project = projectMapper.getProjectEntity(projectId);
        if(project == null){                         // 不存在该项目
            throw new BusinessException(404, "不存在该项目", true);
        }
        if(!project.getPMID().equals(userId)){       // 非本项目经理的项目，不予操作
            throw new AuthorizationException(403, "没有操作权限");
        }

        // 获取项目名称、项目描述
        String projectName = project.getProjectName();
        String projectDescription = project.getProjectDescription();

        // 调用Prompt Engineering模块，拼接messages与tools完整的请求体（预留model参数供http层填充）
        String jsonStr = this.promptEngineering(projectName, projectDescription, userPrompt);

        // 设置Redis信息
        // 这里将生成的JSON字符串转化为MD5码，以便拼接成Key
        // 若短期内再次发起项目名称、项目描述、用户prompt完全相同的请求，可以实现秒开；这样可以实现成本与体验双赢
        String jsonHash = DigestUtils.md5DigestAsHex(jsonStr.getBytes(StandardCharsets.UTF_8));
        String redisKey = "ai:task-assign:" + jsonHash;
        long redisTTL = 10 * 60;

        // 尝试获取Redis缓存数据
        AIHttpResultVO result = redisService.hashGetAll(redisKey, AIHttpResultVO.class);
        // 结果检验
        if(result != null && result.getStatus() == ProcessStatus.Success.getValue()){
            // 先尝试延长TTL，降低最终过期概率
            redisService.expire(redisKey, redisTTL);

            // 将结果json字符串转为对象
            Map<String, List<AITaskAssignVO>> resultObj = JSON.parseObject(result.getResultStr(), new TypeReference<Map<String, List<AITaskAssignVO>>>() {});
            // 取出任务拆解结果数组
            List<AITaskAssignVO> resultList = resultObj.get("tasks");
            // 判断结果合法性
            if(this.checkResultValidity(resultList) == true){
                // 若命中，则直接返回Redis键；这样后，前端开始轮询的一瞬间，即可获取缓存结果
                // 若正好处于过期的临界点，可能最终结果是"404 系统繁忙，请稍后再试"，但是用户重新发起请求即可，不会有明显不良体验
                // 其实在非极端高并发的情况下，最终结果过期的概率极低
                return redisKey;
            }
        }

        // 构造消息队列格式（Redis键、模型类型、请求体字符串）
        AIToolCallMsg aiMsg = AIToolCallMsg.builder()
                .redisKey(redisKey).redisTTL(redisTTL)
                .modelType("qwen").modelLevel("plus")
                .jsonRequest(jsonStr).build();

        // 删除可能存在的原始记录
        redisService.delete(redisKey);
        // 设置Redis状态位为Pending
        redisService.hashSetAll(redisKey, new AIHttpResultVO(ProcessStatus.Pending.getValue(), LocalDateTime.now()));

        // 将请求发送至MQ处理
        rabbit.convertAndSend(RabbitConfig.AI_service, aiMsg);

        return redisKey;
    }

    // AI任务拆解的结果查询（轮询）
    // 若结果为失败，直接throw，轮不到返回给前端200
    public AITaskAssignResultVO aiTaskAssignResponse(String redisKey) throws InterruptedException{
        // Redis获取结果
        AIHttpResultVO result = redisService.hashGetAll(redisKey, AIHttpResultVO.class);
        // 结果获取异常
        if(result == null){
            throw new BusinessException(404, "系统繁忙，请稍后再试", true);
        }
        // 失败
        if(result.getStatus() == ProcessStatus.Fail.getValue()){
            throw new BusinessException(500, "系统繁忙，请稍后再试", true);
        }
        // 正常，但是要校验数据
        if(result.getStatus() == ProcessStatus.Success.getValue()){
            // 将结果json字符串转为对象
            Map<String, List<AITaskAssignVO>> resultObj = JSON.parseObject(result.getResultStr(),
                                                                     new TypeReference<Map<String, List<AITaskAssignVO>>>() {});
            // 取出任务拆解结果数组
            List<AITaskAssignVO> resultList = resultObj.get("tasks");
            // 判断结果合法性
            if(this.checkResultValidity(resultList) == false){
                throw new BusinessException(500, "系统繁忙，请稍后再试", true);
            }

            // 返回状态位为Success的格式化结果
            return AITaskAssignResultVO.builder()
                    .Status(ProcessStatus.Success.getValue())
                    .results(resultList)
                    .resultTime(LocalDateTime.now())
                    .build();
        }
        else{
            // 返回状态位为Pending的格式化结果
            return new AITaskAssignResultVO(ProcessStatus.Pending.getValue());
        }
    }


    // 判断任务拆解结果合法性
    private boolean checkResultValidity(List<AITaskAssignVO> resultList){
        // 结果为空
        if(resultList == null || resultList.isEmpty()){
            return false;
        }
        for(int i = 0; i < resultList.size(); i++){
            // 参数不合法
            if(resultList.get(i).getTaskName() == null || resultList.get(i).getTaskDescription() == null
               || resultList.get(i).getEstimatedHours() == null || resultList.get(i).getEstimatedHours() < 0){
                return false;
            }
        }
        return true;
    }

    // 拼接项目名称、项目描述、用户个性化需求；补全完整prompt，完成messages构造
    // 最终返回序列化的请求体JSON字符串
    private String promptEngineering(String projectName, String projectDescription, String userPrompt) {
        // 处理项目描述可能的空缺值
        if(projectDescription == null || "".equals(projectDescription)) {
            projectDescription = "无";
        }
        // 处理个性化需求可能的空缺值
        if(userPrompt == null || "".equals(userPrompt)) {
            userPrompt = "";
        }
        else{
            userPrompt = String.format("补充信息：\n这里是项目经理输入的个性化需求：%s", userPrompt);
        }

        // 根据项目信息、个性化需求信息，填充任务拆解的专用prompt模板
        String prompt = String.format(promptFormat, projectName, projectDescription, userPrompt);

        // 构建message数组
        List<Map<String, Object>> messages = new ArrayList<>();
        // 添加角色定义
        messages.add(new HashMap<String, Object>() {{
            put("role", "system");
            put("content", roleSetting);
        }});
        // 添加提示词
        messages.add(new HashMap<String, Object>() {{
            put("role", "user");
            put("content", prompt);
        }});

        // message数组序列化
        String messagesJson = JSON.toJSONString(messages);

        // 拼接messages，tools，tool_choice，完成完整请求体的构造（模型名参数预留至http响应层填充）
        String jsonStr = String.format(AIConfig.toolCallRequestTemplate,
                messagesJson,          // messages消息数组
                toolsForTaskAssign,    // tools工具定义
                toolChoice);           // tool-choice工具选择

        return jsonStr;
    }


    // Tools的json编写
    private static String toolsForTaskAssign = """
        [
            {
                "type": "function",
                "function": {
                    "name": "task_assign",
                    "description": "根据项目名称、项目描述等信息，将其拆解为若干条任务",
                    "parameters": {
                        "type": "object",
                        "properties": {
                            "tasks": {
                                "type": "array",
                                "items": {
                                    "type": "object",
                                    "properties": {
                                        "taskName": {"type": "string", "description": "任务名称"},
                                        "taskDescription": {"type": "string", "description": "任务描述"},
                                        "skillAnalysis": {"type": "string", "description": "任务需要的技术栈，这里你直接列举可行、常见、推荐的即可"},
                                        "estimatedHours": {"type": "integer", "description": "任务预估工时，应填写大于0的整数"}
                                    },
                                    "required": ["taskName", "taskDescription", "estimatedHours"]
                                },
                                "description": "注意，一条任务只能由一个人承担，若相似任务需要多人完成，可以拆解成相应数量且描述信息不完全的任务"
                            }
                        },
                        "required": ["tasks"]
                    }
                }
            }
        ]
        """;

    // ToolChoice的json编写
    private static String toolChoice = """
        {
            "type": "function",
            "function": {
                "name": "task_assign"
            }
        }
        """;

    // 角色设定
    private static String roleSetting = """
         请严格按照要求输出格式正确且内容合法的JSON数据，
         不能有任何前导语和后缀，只能是标准的JSON格式，
         具体要求见用户prompt与Function Calling定义
         """;

    // 任务分配的prompt模板
    private static String promptFormat = """
        你是一名信息系统分析师、软件架构师、资深的项目经理
        请你根据以下信息，将该软件开发项目拆解成若干个单人执行的任务，辅助我们公司的项目经理在项目管理系统中完成对指定项目的任务拆解工作
        需要的信息是：任务名称、任务描述、该任务所需技术栈（这里你直接列举可行、常见、推荐的即可）、预估总工时（应填写大于0的整数）
        结果生成格式详见Function Calling的定义
        再次提醒，一条任务只能由一个人完成，若需要多人完成相似任务，请将其拆解为相应数量的描述信息不完全相同的任务
        以下是你需要进行任务拆解的项目信息：
        项目名称：%s
        项目描述：%s
        
        %s
        
        请认真消化以上信息，并给出合理、可靠的决策
        """;

}



