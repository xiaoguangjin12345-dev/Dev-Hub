package com.xgj.devpulse.controller;

import com.xgj.devpulse.common.operationlog.annotation.Log;
import com.xgj.devpulse.common.response.APIResponse;
import com.xgj.devpulse.pojo.dto.notice.NoticeQueryDTO;
import com.xgj.devpulse.pojo.vo.common.PageResultVO;
import com.xgj.devpulse.pojo.vo.notice.NoticeVO;
import com.xgj.devpulse.service.common.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notices")
@RequiredArgsConstructor
public class NoticeController {
    private final NoticeService noticeService;

    // 查询消息通知列表
    @Log("查询通知列表")
    @GetMapping("/")
    public APIResponse<PageResultVO<NoticeVO>> getNotices(@ModelAttribute NoticeQueryDTO dto){
        PageResultVO<NoticeVO> notices = noticeService.getMyNotices(dto);
        return APIResponse.success(notices, "消息通知列表查询成功");
    }

    // 查询指定消息通知并标记已读
    @Log("查询指定通知")
    @GetMapping("/{id}")
    public APIResponse<NoticeVO> getDetails(@PathVariable Integer id){
        NoticeVO notice = noticeService.getNoticeDetails(id);
        return APIResponse.success(notice, "指定消息通知查询成功");
    }

    // 删除指定消息通知（逻辑）
    @Log("逻辑删除通知")
    @PutMapping("/{id}/delete")
    public APIResponse<Boolean> logicalDelete(@PathVariable Integer id){
        noticeService.logicalDeleteNotice(id);
        return APIResponse.success(true, "消息通知删除成功");
    }

    // 获取消息通知未读总数
    // 不设置操作日志AOP，因为该请求明显最多
    @GetMapping("/unread-count")
    public APIResponse<Integer> getUnreadCount(){
        Integer count = noticeService.getUnreadCount();
        return APIResponse.success(count, "消息通知未读总数获取成功");
    }

}
