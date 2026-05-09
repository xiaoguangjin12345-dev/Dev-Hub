package com.xgj.outsourcing.controller;

import com.xgj.outsourcing.common.operationlog.annotation.Log;
import com.xgj.outsourcing.common.response.APIResponse;
import com.xgj.outsourcing.pojo.dto.task.TaskQueryDTO;
import com.xgj.outsourcing.pojo.dto.task.TaskUpdateDTO;
import com.xgj.outsourcing.pojo.vo.common.PageResultVO;
import com.xgj.outsourcing.pojo.vo.task.TaskDetailsVO;
import com.xgj.outsourcing.pojo.vo.task.TaskListVO;
import com.xgj.outsourcing.service.task.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    // 创建任务
    @Log("创建任务")
    @PostMapping("/")
    public APIResponse<Boolean> createTask(@RequestBody TaskUpdateDTO dto) {
        taskService.createTask(dto);
        return APIResponse.success(true, "任务创建成功");
    }

    // 参数化查询任务
    @Log("查询任务列表")
    @GetMapping("/")
    public APIResponse<PageResultVO<TaskListVO>> getTasks(@ModelAttribute TaskQueryDTO dto) {
        PageResultVO<TaskListVO> tasks = taskService.getTasksByQuery(dto);
        return APIResponse.success(tasks, "任务列表查询成功");
    }

    // 开发人员 查询待分配任务列表，用于任务申请
    @Log("查询待分配任务")
    @GetMapping("/square")
    public APIResponse<PageResultVO<TaskListVO>> getTasksForApplication(@ModelAttribute TaskQueryDTO dto) {
        PageResultVO<TaskListVO> tasks = taskService.getTaskSquareList(dto);
        return APIResponse.success(tasks, "开发人员申请专用任务列表查询成功");
    }

    // 查询指定任务详情
    @Log("查询指定任务")
    @GetMapping("/{id}")
    public APIResponse<TaskDetailsVO> getTaskById(@PathVariable Integer id){
        TaskDetailsVO task = taskService.getTaskById(id);
        return APIResponse.success(task, "指定任务查询成功");
    }

}
