package com.xgj.outsourcing.service.common;

import com.xgj.outsourcing.pojo.dto.notice.NoticeApproveDTO;
import com.xgj.outsourcing.pojo.dto.notice.NoticeQueryDTO;
import com.xgj.outsourcing.pojo.vo.common.PageResultVO;
import com.xgj.outsourcing.pojo.vo.notice.NoticeVO;

import java.util.List;

public interface NoticeService {
    // 参数化查询消息通知列表（分页）
    PageResultVO<NoticeVO> getMyNotices(NoticeQueryDTO dto);

    // 获取消息详情并标记已读
    NoticeVO getNoticeDetails(int noticeId);

    // 逻辑删除消息通知
    boolean logicalDeleteNotice(int noticeId);

    // 获取未读消息数量
    int getUnreadCount();


    // 添加工时预警的消息通知
    void createHourWarningNotice(int pmId, int devId, String taskName,
                                    int EstimatedHours, int ActualHours, double percent);

    // 添加 项目经理任务邀请 的消息通知
    void createPMInvitationNotice(int devId, int pmId, String taskName);

    // 添加 开发人员任务申请 的消息通知
    void createDevApplicationNotice(int pmId, int devId, String taskName);

    // 添加 用户审核 的消息通知
    void createUserAuditNotice(int targetUserId, int adminId, NoticeApproveDTO dto);

    // 添加 PMO项目审批（含立项、结项审批）结果 的消息通知
    void createProjectApprovalNotice(int pmId, int pmoId, String projectName, Byte type, NoticeApproveDTO dto);

    // 添加 项目经理任务评审结果 的消息通知
    void createTaskReviewNotice(int devId, int pmId, String taskName, Integer revision, NoticeApproveDTO dto);

}
