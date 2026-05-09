package com.xgj.outsourcing.service.project;

import com.xgj.outsourcing.pojo.dto.notice.NoticeApproveDTO;
import com.xgj.outsourcing.pojo.dto.project.ProjectClosureRequestDTO;
import com.xgj.outsourcing.pojo.dto.project.ProjectQueryDTO;
import com.xgj.outsourcing.pojo.dto.project.ProjectUpdateDTO;
import com.xgj.outsourcing.pojo.vo.common.PageResultVO;
import com.xgj.outsourcing.pojo.vo.project.ProjectDetailsVO;
import com.xgj.outsourcing.pojo.vo.project.ProjectListVO;

import java.util.List;

public interface ProjectService {
    // PM创建项目
    boolean createProject(ProjectUpdateDTO dto);
    // PM修改项目
    boolean updateProject(int projectId, ProjectUpdateDTO dto);
    // PM申请结项
    boolean applyProjectClosure(int projectId, ProjectClosureRequestDTO dto);

    // PMO执行立项审批
    boolean executeProjectCreateApprove(int projectId, NoticeApproveDTO dto);
    // PMO执行结项审批
    boolean executeProjectClosureApprove(int projectId, NoticeApproveDTO dto);

    // 判断项目是否具有结项条件
    boolean isProjectCouldClosure(int projectId);

    // 多条件查询项目列表
    PageResultVO<ProjectListVO> getProjects(ProjectQueryDTO dto);
    // 查询项目详情
    ProjectDetailsVO getProjectDetails(int projectId);

}
