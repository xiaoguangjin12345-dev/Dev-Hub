package com.xgj.outsourcing.service.task;

import com.xgj.outsourcing.common.cache.RedisService;
import com.xgj.outsourcing.common.context.UserContext;
import com.xgj.outsourcing.common.exception.AuthorizationException;
import com.xgj.outsourcing.common.exception.BusinessException;
import com.xgj.outsourcing.enums.tag.TagType;
import com.xgj.outsourcing.enums.task.TaskStatus;
import com.xgj.outsourcing.enums.user.Role;
import com.xgj.outsourcing.mapper.ProjectMapper;
import com.xgj.outsourcing.pojo.dto.task.TaskQueryDTO;
import com.xgj.outsourcing.pojo.dto.task.TaskUpdateDTO;
import com.xgj.outsourcing.pojo.entity.TaskEntity;
import com.xgj.outsourcing.mapper.TaskMapper;
import com.xgj.outsourcing.pojo.vo.common.PageResultVO;
import com.xgj.outsourcing.pojo.vo.task.TaskDetailsVO;
import com.xgj.outsourcing.pojo.vo.task.TaskListVO;
import com.xgj.outsourcing.service.common.DictTagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskMapper taskMapper;
    private final DictTagService dictTagService;
    private final ProjectMapper projectMapper;
    // Redis服务
    private final RedisService redisService;

    // PM创建(分配)任务
    @Transactional         // 涉及多表操作（任务、技能标签），需要添加事务
    public boolean createTask(TaskUpdateDTO dto){
        int userId = UserContext.getCurrentUserId();
        Integer pmId = projectMapper.getProjectPmId(dto.getProjectId());
        // 该任务非此项目经理创建的，不予创建任务
        if (userId != pmId){
            throw new AuthorizationException(403, "没有操作权限");
        }

        // 构造实体类
        TaskEntity task = new TaskEntity();
        task.setProjectID(dto.getProjectId());
        task.setTaskName(dto.getTaskName());
        task.setTaskDescription(dto.getTaskDescription());
        task.setStatus(TaskStatus.Pending.getValue());
        // 未进行任何提交时，设版次号为0，更符合语义
        task.setRevision(0);
        task.setEstimatedHours(dto.getEstimatedHours());
        task.setActualHours(0);
        task.setCreateTime(LocalDateTime.now());

        // 添加任务记录，其编号将存储到实体对象task的TaskID字段
        taskMapper.insertTask(task);
        // 设置技能标签
        dictTagService.setDictTag(TagType.TASK.getValue(),
                                    task.getTaskID(),
                                    dto.getRequiredSkills());
        // 获取技能标签字符串
        String skillsStr = dictTagService.getDictTagName(TagType.TASK.getValue(), task.getTaskID());
        // 更新数据库的技能字段
        taskMapper.updateSkillsById(task.getTaskID(), skillsStr);

        // 添加了新任务，需要让原任务下拉框Redis键失效（异步）
        redisService.deleteTaskSelectOptionKey();

        return true;
    }

    // 参数化查询任务列表
    public PageResultVO<TaskListVO> getTasksByQuery(TaskQueryDTO dto){
        int userId = UserContext.getCurrentUserId();
        byte role = UserContext.getCurrentRole().getValue();

        // 检查分页参数
        if(dto.getPageNum()<=0){
            throw new BusinessException(500, "分页参数错误", false);
        }
        // 计算页面查询偏移量
        int offset = (dto.getPageNum() - 1) * dto.getPageSize();

        // 数据库查询分页结果与总数（数据隔离下沉至Mapper层，对开发人员隐藏预估工时）
        List<TaskListVO> result = taskMapper.getTasksByQuery(userId, role, dto, offset, dto.getPageSize());
        long total = taskMapper.countTasksByQuery(userId, role, dto);

        return new PageResultVO<TaskListVO>(total, result);
    }

    // 开发人员 查询待分配任务，用于任务申请
    public PageResultVO<TaskListVO> getTaskSquareList(TaskQueryDTO dto){
        byte role = UserContext.getCurrentRole().getValue();

        // 仅限开发人员访问
        if(role != Role.DEV.getValue()){
            throw new AuthorizationException(403, "没有访问权限");
        }

        // 检查分页参数
        if(dto.getPageNum()<=0){
            throw new BusinessException(500, "分页参数错误", false);
        }
        // 计算页面查询偏移量
        int offset = (dto.getPageNum() - 1) * dto.getPageSize();

        // 数据库查询分页结果与总数（数据隔离下沉至Mapper层，对开发人员隐藏预估工时）
        List<TaskListVO> result = taskMapper.getTaskSquareByQuery(dto, TaskStatus.Pending.getValue(), offset, dto.getPageSize());
        long total = taskMapper.countTaskSquareByQuery(dto, TaskStatus.Pending.getValue());

        return new PageResultVO<TaskListVO>(total, result);
    }

    // 根据编号查询任务详情
    public TaskDetailsVO getTaskById(int taskId){
        byte role = UserContext.getCurrentRole().getValue();

        // 构造Redis键
        String redisKey = "task:details:" + taskId;
        // 由于对开发人员隐藏预估工时，因此要设置两个版本
        if(role == Role.DEV.getValue()){
            redisKey += ":dev";
        }else{
            redisKey += ":no-dev";
        }

        // Redis取值
        TaskDetailsVO result = redisService.get(redisKey, TaskDetailsVO.class);
        // Redis命中
        if(result != null && result.getTaskId() == taskId){
            redisService.expire(redisKey, 10 * 60);  // 重设有效时间
            return result;                                     // 直接返回结果
        }

        // 数据库查询结果（对开发人员隐藏预估工时）
        result = taskMapper.getTaskDetails(taskId, role);
        // 设置Redis键值，方便接下来的请求快速访问
        redisService.set(redisKey, result, 10 * 60);

        return result;
    }

}
