package com.xgj.devpulse.controller;

import com.xgj.devpulse.common.operationlog.annotation.Log;
import com.xgj.devpulse.common.response.APIResponse;
import com.xgj.devpulse.pojo.dto.notice.NoticeApproveDTO;
import com.xgj.devpulse.pojo.dto.project.ProjectClosureRequestDTO;
import com.xgj.devpulse.pojo.dto.project.ProjectQueryDTO;
import com.xgj.devpulse.pojo.dto.project.ProjectUpdateDTO;
import com.xgj.devpulse.pojo.vo.common.PageResultVO;
import com.xgj.devpulse.pojo.vo.project.ProjectDetailsVO;
import com.xgj.devpulse.pojo.vo.project.ProjectListVO;
import com.xgj.devpulse.service.project.ProjectService;
import io.swagger.v3.oas.annotations.media.Content;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    // 项目经理创建项目
    @Log("创建项目")
    @PostMapping(value = "/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public APIResponse<Boolean> createProject(@io.swagger.v3.oas.annotations.parameters.RequestBody(
                                                    content = @Content(mediaType = "multipart/form-data"
                                              ))
                                              @ModelAttribute ProjectUpdateDTO dto){
        projectService.createProject(dto);
        return APIResponse.success(true, "项目创建成功");
    }

    // 项目经理修改项目
    @Log("修改项目")
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public APIResponse<Boolean> updateProject(@PathVariable Integer id,
                                              @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                                    content = @Content(mediaType = "multipart/form-data"
                                              ))
                                              @ModelAttribute ProjectUpdateDTO dto){
        projectService.updateProject(id, dto);
        return APIResponse.success(true, "项目修改成功");
    }

    // 项目经理申请结项
    @Log("结项申请")
    @PutMapping(value = "/{id}/closure", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public APIResponse<Boolean> applyClosure(@PathVariable Integer id,
                                             @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                                     content = @Content(mediaType = "multipart/form-data"
                                             ))
                                             @ModelAttribute ProjectClosureRequestDTO dto){
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
