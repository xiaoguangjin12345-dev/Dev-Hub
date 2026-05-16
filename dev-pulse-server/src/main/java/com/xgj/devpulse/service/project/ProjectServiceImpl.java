package com.xgj.devpulse.service.project;

import com.xgj.devpulse.common.cache.RedisService;
import com.xgj.devpulse.common.context.UserContext;
import com.xgj.devpulse.common.exception.AuthenticationException;
import com.xgj.devpulse.common.exception.AuthorizationException;
import com.xgj.devpulse.common.exception.BusinessException;
import com.xgj.devpulse.enums.notice.ApproveResult;
import com.xgj.devpulse.enums.performance.PerformanceStatus;
import com.xgj.devpulse.enums.project.ProjectApproveType;
import com.xgj.devpulse.enums.project.ProjectStatus;
import com.xgj.devpulse.enums.task.TaskStatus;
import com.xgj.devpulse.enums.user.Role;
import com.xgj.devpulse.mapper.ProjectApprovalMapper;
import com.xgj.devpulse.mapper.TaskMapper;
import com.xgj.devpulse.pojo.dto.notice.NoticeApproveDTO;
import com.xgj.devpulse.pojo.dto.project.ProjectClosureRequestDTO;
import com.xgj.devpulse.pojo.dto.project.ProjectQueryDTO;
import com.xgj.devpulse.pojo.dto.project.ProjectUpdateDTO;
import com.xgj.devpulse.mapper.ProjectMapper;
import com.xgj.devpulse.pojo.entity.ProjectEntity;
import com.xgj.devpulse.pojo.vo.common.PageResultVO;
import com.xgj.devpulse.pojo.vo.project.ProjectDetailsVO;
import com.xgj.devpulse.pojo.vo.project.ProjectListVO;
import com.xgj.devpulse.service.common.NoticeService;
import com.xgj.devpulse.service.performance.PerformanceService;
import com.xgj.devpulse.service.common.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final ProjectMapper projectMapper;
    private final TaskMapper taskMapper;
    private final ProjectApprovalMapper projectApprovalMapper;
    private final NoticeService noticeService;
    @Qualifier("projectPerformanceService")
    private final PerformanceService performanceService;
    private final FileService fileService;
    private final ExecuteProjectApproveService executeProjectApproveService;
    // Redis服务
    private final RedisService redisService;
    private final RedissonClient redissonClient;

    // PM创建项目
    @Transactional(rollbackFor = Exception.class)     // 涉及多模块处理（文件上传），需要开启事务，而且是Exception级别的
    public boolean createProject(ProjectUpdateDTO dto){
        int userId = UserContext.getCurrentUserId();
        byte role = UserContext.getCurrentRole().getValue();
        // 只能PM创建
        if(role != Role.PM.getValue()){
            throw new AuthenticationException(403, "只能项目经理创建项目");
        }

        // 构造项目实体
        ProjectEntity project = new ProjectEntity();
        // 填写关键信息
        project.setProjectName(dto.getProjectName());
        project.setPMID(userId);
        project.setStatus(ProjectStatus.Pending.getValue());
        // 填写甲方信息
        project.setClientName(dto.getClientName());
        project.setClientEmail(dto.getClientEmail());
        project.setClientPhone(dto.getClientPhone());
        // 填写详情信息
        project.setProjectDescription(dto.getProjectDescription());
        project.setBudget(dto.getBudget());
        project.setPersonnel(dto.getPersonnel());
        // 填写时间信息
        project.setStartDate(dto.getStartDate());
        project.setEndDate(dto.getEndDate());
        project.setCreateTime(LocalDateTime.now());
        // 数据库插入该记录，并获得项目编号保存在project实体
        projectMapper.insertProject(project);

        // 有需求文档时，执行文件上传与物理相对路径生成工作
        if(dto.getRequirementFile() != null){
            // 结合文件处理模块，获取物理相对路径
            String url = fileService.uploadFile
                    (dto.getRequirementFile(), "project", project.getProjectID(), "requirement-file", "");
            // 数据库更新（填写）需求文档字段
            projectMapper.updateRequirementFileInfo(project.getProjectID(), url);
        }

        // 添加了新项目，需要让原项目下拉框Redis键失效（异步）
        redisService.deleteProjectSelectOptionKey();

        return true;
    };

    // PM修改项目
    @Transactional(rollbackFor = Exception.class)     // 涉及多模块处理（文件上传），需要开启事务，而且是Exception级别的
    public boolean updateProject(int projectId, ProjectUpdateDTO dto){
        int userId = UserContext.getCurrentUserId();

        // 查询该项目实体
        ProjectEntity project = projectMapper.getProjectEntity(projectId);
        // 查询不到该项目记录
        if(project == null){
            throw new BusinessException(404, "查找不到该项目", true);
        }
        // 该项目非当前PM的，不予操作
        if(project.getPMID() != userId){
            throw new AuthenticationException(403, "该项目不是你创建的，你无权修改");
        }
        // 当前项目状态不是待审核或待修改，不予操作
        if (project.getStatus() != ProjectStatus.Pending.getValue()
            && project.getStatus() != ProjectStatus.Revision.getValue()
        ){
            throw new BusinessException(500, "项目状态异常", false);
        }

        String url = null;
        // 有需求文档时，执行文件上传与物理相对路径生成工作
        if(dto.getRequirementFile() != null){
            // 结合文件处理模块，获取物理相对路径（这里需要传入原始相对路径，将原文件删除）
            url = fileService.uploadFile
                    (dto.getRequirementFile(), "project", project.getProjectID(), "requirement-file", project.getRequirementDocUrl());
        }

        // 修改项目记录
        projectMapper.updateProject(projectId, dto, ProjectStatus.Pending.getValue(), url);

        // 若修改前后项目名称不一样，需要让原始项目下拉框的Redis键失效
        if(!dto.getProjectName().equals(project.getProjectName())){
            redisService.deleteProjectSelectOptionKey();
        }
        // 让原项目详情的Redis记录失效（异步）
        redisService.deleteProjectDetailsKey(projectId);

        return true;
    }

    // PM申请结项
    @Transactional(rollbackFor = Exception.class)     // 涉及多模块处理（文件上传），需要开启事务，而且是Exception级别的
    public boolean applyProjectClosure(int projectId, ProjectClosureRequestDTO dto){
        int userId = UserContext.getCurrentUserId();

        // 数据库查询该项目实体
        ProjectEntity project = projectMapper.getProjectEntity(projectId);
        // 查询不到该项目记录
        if(project == null){
            throw new BusinessException(404, "查询不到该项目", true);
        }
        // 该项目非当前PM的
        if(project.getPMID() != userId){
            throw new AuthenticationException(403, "该项目不是你创建的，你无权申请结项");
        }
        // 该项目不满足结项条件
        if (!this.isProjectCouldClosure(projectId)){
            throw new BusinessException(500, "该项目不具备结项条件，无法申请结项", false);
        }
        // 根据幂等性，项目已为待结项状态时，应返回操作成功
        if(project.getStatus() == ProjectStatus.Closing.getValue()){
            return true;
        }

        // 必须上传结项报告文件后，才能提交结项（即使不是第一次结项）
        if(dto.getFinalReportFile() != null){
            // 结合文件处理模块，获取物理相对路径
            String url = fileService.uploadFile
                    (dto.getFinalReportFile(), "project", project.getProjectID(), "final-report-file", project.getFinalReportUrl());
            // 项目实体，更新申请结项的信息
            projectMapper.updateProjectClosureInfo(projectId, ProjectStatus.Closing.getValue(), url);

            // 让原项目详情的Redis记录失效（异步）
            redisService.deleteProjectDetailsKey(projectId);

            return true;
        }else{
            throw new BusinessException(500, "申请结项需上传结项文件", false);
        }

    };

    // PMO执行立项审批
    // 事务需要在临界区添加
    public boolean executeProjectCreateApprove(int projectId, NoticeApproveDTO dto){
        int userId = UserContext.getCurrentUserId();
        byte role = UserContext.getCurrentRole().getValue();

        // 非PMO不予操作
        if(role != Role.PMO.getValue()){
            throw new AuthorizationException(403, "没有操作权限");
        }
        // 数据库查询项目实体
        ProjectEntity project = projectMapper.getProjectEntity(projectId);
        // 查询不到该项目 或 该项目不是待审核状态，不予操作
        if(project == null){
            throw new BusinessException(404, "查询不到该项目", true);
        }
        if(project.getStatus() != ProjectStatus.Pending.getValue()){
            throw new BusinessException(500, "该项目不处于待立项状态", false);
        }
        // 将审批结果转换为枚举值
        Byte result = dto.isResult() ? ApproveResult.Approved.getValue() : ApproveResult.Rejected.getValue();

        // 设置分布式锁的键
        String lockKey = "lock:project-approve:create:project-id:" + projectId;
        RLock lock = redissonClient.getLock(lockKey);
        try {
            // 尝试获取锁
            boolean isLocked = lock.tryLock(0, -1, TimeUnit.SECONDS);
            if(!isLocked){
                throw new BusinessException(500, "该立项审批已被其他PMO尝试执行", false);
            }
            // 执行临界区的操作
            executeProjectApproveService.executeCreateApprove(userId, projectId, result, dto.getReason());
        }
        catch(InterruptedException e){
            // 恢复中断状态
            Thread.currentThread().interrupt();
            throw new RuntimeException("操作被系统中断");
        }
        catch (Exception e) {
            throw new BusinessException(500, "立项审批执行失败", true);
        }
        finally{
            // 若锁被当前线程持有，则释放锁
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }

        // 让原项目详情的Redis记录失效（异步）
        redisService.deleteProjectDetailsKey(projectId);
        // 添加项目立项审核结果通知，提醒项目经理（异步）
        noticeService.createProjectApprovalNotice
                (project.getPMID(), userId, project.getProjectName(), ProjectApproveType.Create.getValue(), dto);

        // 消息通知的失败，不应影响主业务的结果
        return true;
    }

    // PMO执行结项审批
    // 事务需要在临界区添加
    public boolean executeProjectClosureApprove(int projectId, NoticeApproveDTO dto){
        int userId = UserContext.getCurrentUserId();
        byte role = UserContext.getCurrentRole().getValue();

        // 只有PMO可以审批项目
        if (role != Role.PMO.getValue()){
            throw new AuthorizationException(403, "没有操作权限");
        }
        // 数据库查询项目实体
        ProjectEntity project = projectMapper.getProjectEntity(projectId);
        // 查询不到该项目
        if(project == null){
            throw new BusinessException(404, "查询不到该项目", true);
        }
        // 该项目不是待结项状态，不予操作
        if(project.getStatus() != ProjectStatus.Closing.getValue()){
            throw new BusinessException(500, "该项目不处于待结项状态", false);
        }
        // 将审批结果转换为枚举值
        Byte result = dto.isResult() ? ApproveResult.Approved.getValue() : ApproveResult.Rejected.getValue();

        // 设置分布式锁的键
        String lockKey = "lock:project-approve:closure:project-id:" + projectId;
        RLock lock = redissonClient.getLock(lockKey);
        try {
            // 尝试获取锁
            boolean isLocked = lock.tryLock(0, -1, TimeUnit.SECONDS);
            if(!isLocked){
                throw new BusinessException(500, "该结项审批已被其他PMO尝试执行", false);
            }
            // 执行临界区的操作
            executeProjectApproveService.executeClosureApprove(userId, projectId, result, dto.getReason());
        }
        catch(InterruptedException e){
            // 恢复中断状态
            Thread.currentThread().interrupt();
            throw new RuntimeException("操作被系统中断");
        }
        catch (Exception e) {
            throw new BusinessException(500, "结项审批执行失败", true);
        }
        finally{
            // 若锁被当前线程持有，则释放锁
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }

        // 让原项目实体、项目详情的Redis记录失效（异步）
        redisService.deleteProjectDetailsKey(projectId);
        // 添加项目结项审核结果通知，提醒项目经理（异步）
        noticeService.createProjectApprovalNotice
                (project.getPMID(), userId, project.getProjectName(), ProjectApproveType.Closure.getValue(), dto);

        // 消息通知的失败，不应影响主业务的结果
        return true;
    };

    // 判断项目是否具有结项条件
    public boolean isProjectCouldClosure(int projectId){
        // 根据项目编号，查询任务数量
        Integer taskCount = taskMapper.getTaskCountByProjectId(projectId);
        // 没有任何任务的项目，不予结项
        if(taskCount == 0){
            return false;
        }
        // 该项目下，所有任务均已完成 且 所有任务级绩效全部完成主观部分评定
        return projectMapper.isAllTaskFinishedByProjectId(projectId, TaskStatus.Done.getValue())
                && projectMapper.isAllTaskPerformanceReleasedByProjectId(projectId, PerformanceStatus.Released.getValue());
    };

    // 多条件查询项目列表
    public PageResultVO<ProjectListVO> getProjects(ProjectQueryDTO dto){
        int userId = UserContext.getCurrentUserId();
        byte role = UserContext.getCurrentRole().getValue();

        // 检查分页参数
        if(dto.getPageNum()<=0){
            throw new BusinessException(500, "分页参数错误", false);
        }
        // 计算页面查询偏移量
        int offset = (dto.getPageNum() - 1) * dto.getPageSize();

        // 数据库查询分页结果与总数（数据隔离下沉至Mapper层）
        List<ProjectListVO> query = projectMapper.getProjectsByQuery(userId, role, dto, offset, dto.getPageSize());
        long total = projectMapper.countProjectsByQuery(userId, role, dto);

        return new PageResultVO<ProjectListVO>(total, query);
    };

    // 查询项目详情
    public ProjectDetailsVO getProjectDetails(int projectId){
        int userId = UserContext.getCurrentUserId();
        byte role = UserContext.getCurrentRole().getValue();

        // 构造Redis键，设置TTL
        String redisKey = "project:details:" + projectId;
        long expireTime = 20 * 60;
        if(role == Role.PM.getValue()){
            redisKey += ":pm:userid:" + userId;
            expireTime = 5 * 60;      // 对项目经理访问的项目详情，设置较短的TTL即可
        }else{
            redisKey += ":no-pm";
        }

        // Redis取值
        ProjectDetailsVO result = redisService.get(redisKey, ProjectDetailsVO.class);
        // Redis命中
        if(result != null && result.getProjectId() == projectId){
            redisService.expire(redisKey, expireTime);   // 重设有效时间
            return result;                                     // 直接返回结果
        }

        // 根据项目编号，查询项目经理编号
        Integer pmId = projectMapper.getProjectPmId(projectId);
        // PM角色该项目非本人创建的，不予操作
        if(role == Role.PM.getValue() && pmId != userId){
            throw new AuthenticationException(403, "没有访问权限");
        }
        // 查找不到项目经理
        if(pmId == null){
            throw new BusinessException(500, "项目状态异常", true);
        }
        // 查询项目详情
        result = projectMapper.getProjectById(projectId);
        // 理论上，前面查到了项目经理编号，后面查不到项目详情，出现的概率非常低
        if(result == null){
            throw new BusinessException(404, "查找不到该项目", false);
        }

        // 设置Redis键值，方便访问
        redisService.set(redisKey, result, expireTime);

        return result;
    };

}
