package com.xgj.outsourcing.controller;

import com.xgj.outsourcing.common.operationlog.annotation.Log;
import com.xgj.outsourcing.common.response.APIResponse;
import com.xgj.outsourcing.pojo.dto.notice.NoticeApproveDTO;
import com.xgj.outsourcing.pojo.dto.taskreview.TaskDeliverableDTO;
import com.xgj.outsourcing.pojo.vo.common.PageResultVO;
import com.xgj.outsourcing.pojo.vo.taskreview.TaskReviewListVO;
import com.xgj.outsourcing.service.task.TaskReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class TaskReviewController {
    private final TaskReviewService taskReviewService;

    // 开发人员提交任务成果，生成初始任务评审记录
    @Log("提交任务")
    @PostMapping("/")
    public APIResponse<Boolean> submitDeliverable(@ModelAttribute TaskDeliverableDTO dto) {
        taskReviewService.createTaskReview(dto);
        return APIResponse.success(true, "任务成果提交成功");
    }

    // 项目经理执行任务评审
    @Log("执行任务评审")
    @PutMapping("/{id}")
    public APIResponse<Boolean> executeReview(@PathVariable Integer id,
                                              @RequestBody NoticeApproveDTO dto){
        taskReviewService.executeTaskReview(id, dto);
        return APIResponse.success(true, "任务评审执行成功");
    }

    // 查询任务评审记录
    @Log("查询任务评审列表")
    @GetMapping("/")
    public APIResponse<PageResultVO<TaskReviewListVO>> getReviews(@RequestParam Integer pageNum,
                                                                  @RequestParam Integer pageSize){
        PageResultVO<TaskReviewListVO> reviews = taskReviewService.getTaskReviews(pageNum, pageSize);
        return APIResponse.success(reviews, "任务评审列表查询成功");
    }

}
