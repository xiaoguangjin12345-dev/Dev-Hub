package com.xgj.outsourcing.service.task;

import com.xgj.outsourcing.common.cache.RedisService;
import com.xgj.outsourcing.common.context.UserContext;
import com.xgj.outsourcing.common.exception.AuthorizationException;
import com.xgj.outsourcing.common.exception.BusinessException;
import com.xgj.outsourcing.enums.task.TaskStatus;
import com.xgj.outsourcing.enums.taskreview.TaskReviewResult;
import com.xgj.outsourcing.enums.user.Role;
import com.xgj.outsourcing.mapper.TaskMapper;
import com.xgj.outsourcing.pojo.dto.notice.NoticeApproveDTO;
import com.xgj.outsourcing.pojo.dto.taskreview.TaskDeliverableDTO;
import com.xgj.outsourcing.pojo.entity.TaskEntity;
import com.xgj.outsourcing.pojo.entity.TaskReviewEntity;
import com.xgj.outsourcing.mapper.TaskReviewMapper;
import com.xgj.outsourcing.pojo.vo.common.PageResultVO;
import com.xgj.outsourcing.pojo.vo.taskreview.TaskReviewListVO;
import com.xgj.outsourcing.service.common.NoticeService;
import com.xgj.outsourcing.service.performance.PerformanceService;
import com.xgj.outsourcing.service.common.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskReviewServiceImpl implements TaskReviewService {
    private final TaskReviewMapper taskReviewMapper;
    private final TaskMapper taskMapper;
    @Qualifier("taskPerformanceService")
    private final PerformanceService performanceService;
    private final NoticeService noticeService;
    private final FileService fileService;
    // Redis服务
    private final RedisService redisService;

    // 开发人员提交成果，生成初始任务评审记录
    @Transactional(rollbackFor = Exception.class)          // 涉及多表增改操作，开启事务，且涉及文件上传，应对Exception触发
    public boolean createTaskReview(TaskDeliverableDTO dto){
        int userId = UserContext.getCurrentUserId();
        byte role = UserContext.getCurrentRole().getValue();

        if(role != Role.DEV.getValue()){    // 非开发人员，不予操作
            throw new AuthorizationException(403, "没有操作权限");
        }
        // 数据库获取当前任务编号的任务实体
        TaskEntity task = taskMapper.getTaskEntity(dto.getTaskId());
        if(task == null){                   // 任务记录为空
            throw new BusinessException(404, "查询不到该任务", true);
        }
        if(task.getDevID() != userId){      // 非本开发人员的项目
            throw new AuthorizationException(403, "没有操作权限");
        }
        // 数据库获取管理当前任务的项目经理编号
        Integer pmId = taskMapper.getPmIdByTaskId(dto.getTaskId());
        if(pmId == null){
            throw new BusinessException(500, "任务状态异常", true);
        }

        // 程序源文件（压缩包）
        String archiveUrl = null;
        if(dto.getArchiveFile() != null){
            // 上传文件并获取相对路径
            archiveUrl = fileService.uploadFile
                    (dto.getArchiveFile(), "task", task.getTaskID(),
                            String.format("v%d/archive-file", task.getRevision() + 1), "");
        }
        // 技术文档
        String docUrl = null;
        if(dto.getDocFile() != null){
            // 上传文件并获取相对路径
            docUrl = fileService.uploadFile
                    (dto.getDocFile(), "task", task.getTaskID(),
                            String.format("v%d/doc-file", task.getRevision() + 1), "");
        }

        TaskReviewEntity taskReview = new TaskReviewEntity();
        // 填写关键信息
        taskReview.setTaskID(dto.getTaskId());
        taskReview.setDevID(userId);
        taskReview.setPMID(pmId);
        // 填写任务提交信息
        taskReview.setGitUrl(dto.getGitUrl());
        taskReview.setArchiveUrl(archiveUrl);
        taskReview.setDocUrl(docUrl);
        // 填写初始状态信息和其他信息
        taskReview.setRevision(task.getRevision() + 1);
        taskReview.setResult(TaskReviewResult.Pending.getValue());
        taskReview.setSubmitTime(LocalDateTime.now());

        // 数据库添加新评审记录
        taskReviewMapper.insertTaskReview(taskReview);
        // 任务实体，同步更新任务提交信息（状态机变更）
        taskMapper.updateTaskReviewInfo(dto.getTaskId(), TaskStatus.Review.getValue(), task.getRevision());

        // Redis删除相应任务详情键（异步）
        redisService.deleteTaskDetailsKey(dto.getTaskId());

        return true;
    }

    // 项目经理执行评审
    @Transactional             // 涉及多表增改操作，需要添加事务
    public boolean executeTaskReview(int reviewId, NoticeApproveDTO dto){
        int userId = UserContext.getCurrentUserId();

        // 数据库查询该任务评审实体
        TaskReviewEntity taskReview = taskReviewMapper.getTaskReviewEntity(reviewId);
        // 查询不到该任务评审记录 或 该记录非当前项目经理的，不予操作
        if(taskReview == null){
            throw new BusinessException(404, "查询不到该记录", true);
        }
        // 该记录非当前项目经理的，不予操作
        if(taskReview.getPMID() != userId){
            throw new AuthorizationException(403, "没有操作权限");
        }
        // 根据幂等性，若评审状态不为待评审，应返回操作成功
        if(taskReview.getResult() != TaskReviewResult.Pending.getValue()){
            return true;
        }

        // 将前端传输结果，转换成任务评审结果的相应枚举值
        Byte result = dto.isResult()? TaskReviewResult.Approved.getValue(): TaskReviewResult.Rework.getValue();
        // 数据库更新任务评审结果信息
        taskReviewMapper.updateExecuteTaskReviewInfo(reviewId, result, dto.getReason(), LocalDateTime.now());

        // 任务状态变更
        if(result == TaskReviewResult.Approved.getValue()){
            // 针对任务实体，更新任务完成的相关信息
            taskMapper.updateTaskFinishInfo(taskReview.getTaskID(), TaskStatus.Done.getValue(), LocalDateTime.now());
            // 生成任务级绩效记录
            performanceService.createPerformance(taskReview.getTaskID());
        }else{
            // 针对任务实体，更新任务评审不通过的相关信息
            taskMapper.updateRejectReviewInfo(taskReview.getTaskID(), TaskStatus.Ongoing.getValue());
        }

        String taskName = taskMapper.getTaskNameByTaskId(taskReview.getTaskID());

        // Redis删除相应任务详情键（异步）
        redisService.deleteTaskDetailsKey(taskReview.getTaskID());
        // 添加评审结果消息通知，提醒开发人员（异步）
        noticeService.createTaskReviewNotice(taskReview.getDevID(), userId, taskName, taskReview.getRevision(), dto);

        // 消息通知的失败，不应影响主业务的结果
        return true;
    }

    // 查看任务评审记录，需要按照角色做好数据隔离
    public PageResultVO<TaskReviewListVO> getTaskReviews(Integer pageNum, Integer pageSize){
        int userId = UserContext.getCurrentUserId();
        byte role = UserContext.getCurrentRole().getValue();

        // 检查分页参数
        if(pageNum<=0){
            throw new BusinessException(500, "分页参数错误", false);
        }
        // 计算页面查询偏移量
        int offset = (pageNum - 1) * pageSize;

        // 数据库查询任务评审记录分页结果与总数
        List<TaskReviewListVO> result = taskReviewMapper.getTaskReviews(userId, role, offset, pageSize);
        long total = taskReviewMapper.countTaskReviews(userId, role);

        return new PageResultVO<TaskReviewListVO>(total, result);
    }

}
