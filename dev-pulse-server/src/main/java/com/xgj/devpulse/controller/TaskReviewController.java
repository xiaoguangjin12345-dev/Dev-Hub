package com.xgj.devpulse.controller;

import com.xgj.devpulse.common.operationlog.annotation.Log;
import com.xgj.devpulse.common.response.APIResponse;
import com.xgj.devpulse.pojo.dto.notice.NoticeApproveDTO;
import com.xgj.devpulse.pojo.dto.taskreview.TaskDeliverableDTO;
import com.xgj.devpulse.pojo.vo.common.PageResultVO;
import com.xgj.devpulse.pojo.vo.taskreview.TaskReviewListVO;
import com.xgj.devpulse.service.task.TaskReviewService;
import io.swagger.v3.oas.annotations.media.Content;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class TaskReviewController {
    private final TaskReviewService taskReviewService;

    // 开发人员提交任务成果，生成初始任务评审记录
    @Log("提交任务")
    @PostMapping(value = "/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public APIResponse<Boolean> submitDeliverable(@io.swagger.v3.oas.annotations.parameters.RequestBody(
                                                        content = @Content(mediaType = "multipart/form-data"
                                                  ))
                                                  @ModelAttribute TaskDeliverableDTO dto) {
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
