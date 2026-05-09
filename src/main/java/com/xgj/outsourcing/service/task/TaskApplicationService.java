package com.xgj.outsourcing.service.task;

import com.xgj.outsourcing.pojo.dto.taskapplication.TaskInviteDTO;
import com.xgj.outsourcing.pojo.vo.taskapplication.TaskApplicationListVO;

import java.util.List;

public interface TaskApplicationService {
    // 项目经理邀请/开发人员申请（优化双选流程版）
    boolean createTaskApplication(int taskId, TaskInviteDTO dto);

    // 开发人员接受邀请/项目经理接受申请
    boolean acceptTaskApplication(int appId);

    // 查看邀请/申请列表
    List<TaskApplicationListVO> getTaskApplicationList(Byte type);

}
