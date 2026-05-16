package com.xgj.devpulse.service.project;

import com.xgj.devpulse.common.exception.BusinessException;
import com.xgj.devpulse.enums.notice.ApproveResult;
import com.xgj.devpulse.enums.project.ProjectApproveType;
import com.xgj.devpulse.enums.project.ProjectStatus;
import com.xgj.devpulse.mapper.ProjectApprovalMapper;
import com.xgj.devpulse.mapper.ProjectMapper;
import com.xgj.devpulse.pojo.entity.ProjectEntity;
import com.xgj.devpulse.service.performance.PerformanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ExecuteProjectApproveServiceImpl implements ExecuteProjectApproveService {
    private final ProjectApprovalMapper projectApprovalMapper;
    private final ProjectMapper projectMapper;
    @Qualifier("projectPerformanceService")
    private final PerformanceService performanceService;

    // 执行立项审批的临界区暨事务控制区
    @Transactional                   // 多表操作，需要事务控制
    public boolean executeCreateApprove(Integer userId, Integer projectId, Byte result, String reason) {
        // 为防止极度并发，再查一次项目状态
        ProjectEntity project = projectMapper.getProjectEntity(projectId);
        // 若不是待立项状态，不予操作
        if(project.getStatus() != ProjectStatus.Pending.getValue()){
            throw new BusinessException(500, "该项目不处于待立项状态", false);
        }
        // 数据库插入项目审批记录
        projectApprovalMapper.insertProjectApproval
                (ProjectApproveType.Create.getValue(), userId, projectId, result, reason, LocalDateTime.now());
        // 立项审批通过时，修改项目状态
        if(result == ApproveResult.Approved.getValue()){
            // 针对项目实体，更新立项审批通过的相关信息
            projectMapper.updateProjectApprovalInfo(projectId, ProjectStatus.Ongoing.getValue(), LocalDateTime.now());
        }else{
            // 针对项目实体，更新（立项）审批不通过的相关信息（状态置为待修改）
            projectMapper.updateRejectApprovalInfo(projectId, ProjectStatus.Revision.getValue());
        }
        return true;
    }

    // 执行结项审批的临界区暨事务控制区
    @Transactional                   // 多表操作，需要事务控制
    public boolean executeClosureApprove(Integer userId, Integer projectId, Byte result, String reason){
        // 为防止极度并发，再查一次项目状态
        ProjectEntity project = projectMapper.getProjectEntity(projectId);
        // 若不是待结项状态，不予操作
        if(project.getStatus() != ProjectStatus.Closing.getValue()){
            throw new BusinessException(500, "该项目不处于待结项状态", false);
        }
        // 数据库插入项目审批记录
        projectApprovalMapper.insertProjectApproval
                (ProjectApproveType.Closure.getValue(), userId, projectId, result, reason, LocalDateTime.now());
        // 结项审批通过时，修改项目状态
        if(result == ApproveResult.Approved.getValue()){
            // 针对项目实体，更新项目归档的相关信息
            projectMapper.updateProjectFinishInfo(projectId, ProjectStatus.Archived.getValue(), LocalDateTime.now());
            // 生成项目级绩效记录
            performanceService.createPerformance(projectId);
        }else{
            // 针对项目实体，更新（结项）审批不通过的相关信息（状态置为进行中）
            projectMapper.updateRejectApprovalInfo(projectId, ProjectStatus.Ongoing.getValue());
        }
        return true;
    }

}
