package com.xgj.outsourcing.controller;

import com.xgj.outsourcing.common.operationlog.annotation.Log;
import com.xgj.outsourcing.common.response.APIResponse;
import com.xgj.outsourcing.pojo.dto.notice.NoticeApproveDTO;
import com.xgj.outsourcing.pojo.dto.project.ProjectClosureRequestDTO;
import com.xgj.outsourcing.pojo.dto.project.ProjectQueryDTO;
import com.xgj.outsourcing.pojo.dto.project.ProjectUpdateDTO;
import com.xgj.outsourcing.pojo.vo.common.PageResultVO;
import com.xgj.outsourcing.pojo.vo.project.ProjectDetailsVO;
import com.xgj.outsourcing.pojo.vo.project.ProjectListVO;
import com.xgj.outsourcing.service.project.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    // 项目经理创建项目
    @Log("创建项目")
    @PostMapping("/")
    public APIResponse<Boolean> createProject(@ModelAttribute ProjectUpdateDTO dto){
        projectService.createProject(dto);
        return APIResponse.success(true, "项目创建成功");
    }

    // 项目经理修改项目
    @Log("修改项目")
    @PutMapping("/{id}")
    public APIResponse<Boolean> updateProject(@PathVariable Integer id,
                                              @ModelAttribute ProjectUpdateDTO dto){
        projectService.updateProject(id, dto);
        return APIResponse.success(true, "项目修改成功");
    }

    // 项目经理申请结项
    @Log("结项申请")
    @PutMapping("/{id}/closure")
    public APIResponse<Boolean> applyClosure(@PathVariable Integer id,
                                             @RequestBody ProjectClosureRequestDTO dto){
        projectService.applyProjectClosure(id, dto);
        return APIResponse.success(true, "项目结项申请提交成功");
    }

    // PMO审批立项
    @Log("立项审批")
    @PostMapping("/{id}/approve")
    public APIResponse<Boolean> approveCreate(@PathVariable Integer id,
                                              @RequestBody NoticeApproveDTO dto){
        projectService.executeProjectCreateApprove(id, dto);
        return APIResponse.success(true, "项目立项审批结果提交成功");
    }

    // PMO审批结项
    @Log("结项审批")
    @PostMapping("/{id}/archive")
    public APIResponse<Boolean> approveClosure(@PathVariable Integer id,
                                               @RequestBody NoticeApproveDTO dto){
        projectService.executeProjectClosureApprove(id, dto);
        return APIResponse.success(true, "项目结项审批结果提交成功");
    }

    // 参数化查询项目列表
    @Log("查询项目列表")
    @GetMapping("/")
    public APIResponse<PageResultVO<ProjectListVO>> getProjects(@ModelAttribute ProjectQueryDTO dto){
        PageResultVO<ProjectListVO> projects = projectService.getProjects(dto);
        return APIResponse.success(projects, "项目列表查询成功");
    }

    // 查询指定项目
    @Log("查询指定项目")
    @GetMapping("/{id}")
    public APIResponse<ProjectDetailsVO> getProjectById(@PathVariable Integer id){
        ProjectDetailsVO project = projectService.getProjectDetails(id);
        return APIResponse.success(project, "指定项目查询成功");
    }

}
