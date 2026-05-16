package com.xgj.devpulse.service.task;

import com.xgj.devpulse.pojo.dto.task.TaskQueryDTO;
import com.xgj.devpulse.pojo.dto.task.TaskUpdateDTO;
import com.xgj.devpulse.pojo.vo.common.PageResultVO;
import com.xgj.devpulse.pojo.vo.task.TaskDetailsVO;
import com.xgj.devpulse.pojo.vo.task.TaskListVO;

public interface TaskService {
    // PM创建(分配)任务
    boolean createTask(TaskUpdateDTO dto);

    // 参数化查询任务列表
    PageResultVO<TaskListVO> getTasksByQuery(TaskQueryDTO dto);
    // 开发人员 查询待分配任务，用于任务申请
    PageResultVO<TaskListVO> getTaskSquareList(TaskQueryDTO dto);
    // 根据编号查询任务详情
    TaskDetailsVO getTaskById(int taskId);

}
