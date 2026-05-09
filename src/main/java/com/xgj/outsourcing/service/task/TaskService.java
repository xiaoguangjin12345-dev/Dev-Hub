package com.xgj.outsourcing.service.task;

import com.xgj.outsourcing.pojo.dto.task.TaskQueryDTO;
import com.xgj.outsourcing.pojo.dto.task.TaskUpdateDTO;
import com.xgj.outsourcing.pojo.vo.common.PageResultVO;
import com.xgj.outsourcing.pojo.vo.task.TaskDetailsVO;
import com.xgj.outsourcing.pojo.vo.task.TaskListVO;

import java.util.List;

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
