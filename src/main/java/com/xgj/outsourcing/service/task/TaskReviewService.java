package com.xgj.outsourcing.service.task;

import com.xgj.outsourcing.pojo.dto.notice.NoticeApproveDTO;
import com.xgj.outsourcing.pojo.dto.taskreview.TaskDeliverableDTO;
import com.xgj.outsourcing.pojo.vo.common.PageResultVO;
import com.xgj.outsourcing.pojo.vo.taskreview.TaskReviewListVO;

import java.util.List;

public interface TaskReviewService {
    // 开发人员提交成果，生成初始任务评审记录
    boolean createTaskReview(TaskDeliverableDTO dto);
    // 项目经理执行评审
    boolean executeTaskReview(int reviewId, NoticeApproveDTO dto);

    // 查看任务评审记录，需要按照角色做好数据隔离
    PageResultVO<TaskReviewListVO> getTaskReviews(Integer pageNum, Integer pageSize);

}
