package com.xgj.devpulse.service.common;

import com.xgj.devpulse.common.context.UserContext;
import com.xgj.devpulse.common.exception.AuthorizationException;
import com.xgj.devpulse.common.exception.BusinessException;
import com.xgj.devpulse.enums.notice.NoticeStatus;
import com.xgj.devpulse.enums.notice.NoticeType;
import com.xgj.devpulse.enums.project.ProjectApproveType;
import com.xgj.devpulse.enums.user.Role;
import com.xgj.devpulse.mapper.NoticeMapper;
import com.xgj.devpulse.pojo.dto.notice.NoticeApproveDTO;
import com.xgj.devpulse.pojo.dto.notice.NoticeQueryDTO;
import com.xgj.devpulse.pojo.entity.NoticeEntity;
import com.xgj.devpulse.pojo.vo.common.PageResultVO;
import com.xgj.devpulse.pojo.vo.notice.NoticeVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {
    private final NoticeMapper noticeMapper;
    private final BasicCommonService basicCommonService;

    // 参数化查询消息通知列表（分页）
    public PageResultVO<NoticeVO> getMyNotices(NoticeQueryDTO dto){
        int userId = UserContext.getCurrentUserId();
        byte role = UserContext.getCurrentRole().getValue();

        // 检查分页参数
        if(dto.getPageNum()<=0){
            throw new BusinessException(500, "分页参数错误", false);
        }
        // 计算页面查询偏移量
        int offset = (dto.getPageNum() - 1) * dto.getPageSize();

        // 数据库查询消息通知列表、总数
        List<NoticeVO> result = noticeMapper.getNoticesByReceiverID(role, userId, dto, offset, dto.getPageSize());
        long total = noticeMapper.countNoticesByReceiverID(role, userId, dto);

        // 返回标准分页格式
        return new PageResultVO<NoticeVO>(total, result);
    }

    // 获取消息详情并标记已读
    public NoticeVO getNoticeDetails(int noticeId){
        int userId = UserContext.getCurrentUserId();
        byte role = UserContext.getCurrentRole().getValue();

        // 数据库查询消息通知信息
        NoticeVO result = noticeMapper.getNoticeById(role, userId, noticeId);
        return result;
    }

    // 逻辑删除消息通知
    public boolean logicalDeleteNotice(int noticeId){
        int userId = UserContext.getCurrentUserId();
        byte role = UserContext.getCurrentRole().getValue();

        // 数据库查询消息通知实体
        NoticeEntity notice = noticeMapper.getNoticeEntity(noticeId);
        // 查询不到该消息通知记录
        if(notice == null){
            throw new BusinessException(404, "查询不到该记录", true);
        }
        // 该消息通知非本人的，不予操作
        if(role != Role.ADMIN.getValue() && notice.getRecieverID() != userId){
            throw new AuthorizationException(403, "没有操作权限");
        }
        // 根据幂等性，若状态为已删除，应直接为操作成功
        if(notice.getStatus() == NoticeStatus.Deleted.getValue()){
            return true;
        }

        // 数据库更新消息通知状态为 已删除
        noticeMapper.updateDeleteInfo(noticeId, NoticeStatus.Deleted.getValue());
        return true;
    }


    // 获取未读消息数量
    public int getUnreadCount(){
        int userId = UserContext.getCurrentUserId();

        // 数据库查询当前用户未读消息总数
        int count = noticeMapper.getUnreadNoticeCount(userId, NoticeStatus.Unread.getValue());
        return count;
    }

    // 添加工时预警的消息通知
    @Async
    public void createHourWarningNotice(int pmId, int devId, String taskName, int estimatedHours, int actualHours, double percent){
        try{
            // 构造消息内容
            String devName = basicCommonService.getNameById(devId);
            String title = String.format("任务 %s 的工时预警", taskName);
            String content = String.format("""
                                       您管理的任务 %s 的实际工时已超出阈值范围，请留意!
                                       该任务预估工时：    %d 小时
                                       该任务实际工时：    %d 小时
                                       实际工时超出比例:   %.2f %%
                                       该任务开发人员：    %s
                                       预警时间：         %6$tY-%6$tm-%6$td %6$tH:%6$tM:%6$tS
                                       """,
                    taskName, estimatedHours, actualHours,
                    percent, devName,
                    LocalDateTime.now());

            // 构造消息通知实体
            NoticeEntity notice= new NoticeEntity();
            // 填写参数
            notice.setRecieverID(pmId);
            notice.setSenderID(devId);
            notice.setTitle(title);
            notice.setContent(content);
            notice.setNoticeType(NoticeType.HourWarning.getValue());
            notice.setStatus(NoticeStatus.Unread.getValue());
            notice.setCreateTime(LocalDateTime.now());
            // 数据库执行添加消息通知
            noticeMapper.insertNotice(notice);

        }catch (Exception e){
            log.error("消息通知创建失败", e);
        }

    }

    // 添加 项目经理任务邀请 的消息通知
    @Async
    public void createPMInvitationNotice(int devId, int pmId, String taskName){
        try{
            // 构造消息内容
            String pmName = basicCommonService.getNameById(pmId);
            String title = String.format("项目经理 %s 的任务邀请", pmName);
            String content = String.format("""
                                       项目经理 %s 向您发起任务邀请，请查看任务邀请栏目了解详情!
                                       任务名称：  %s
                                       邀请时间：  %3$tY-%3$tm-%3$td %3$tH:%3$tM:%3$tS
                                       """, pmName, taskName, LocalDateTime.now());

            // 构造消息通知实体
            NoticeEntity notice = new NoticeEntity();
            // 项目经理邀请，接收者为开发人员
            notice.setRecieverID(devId);
            notice.setSenderID(pmId);
            notice.setTitle(title);
            notice.setContent(content);
            notice.setNoticeType(NoticeType.TaskApplication.getValue());
            notice.setStatus(NoticeStatus.Unread.getValue());
            notice.setCreateTime(LocalDateTime.now());
            // 数据库执行添加消息通知
            noticeMapper.insertNotice(notice);

        }catch (Exception e){
            log.error("消息通知创建失败", e);
        }

    }

    // 添加 开发人员任务申请 的消息通知
    @Async
    public void createDevApplicationNotice(int pmId, int devId, String taskName){
        try{
            // 构造消息内容
            String devName = basicCommonService.getNameById(devId);
            String title = String.format("开发人员 %s 的任务申请", devName);
            String content = String.format("""
                                    开发人员 %s 向您发起任务申请，请查看任务邀请栏目了解详情!
                                    任务名称：  %s
                                    申请时间：  %3$tY-%3$tm-%3$td %3$tH:%3$tM:%3$tS
                                    """, devName, taskName, LocalDateTime.now());

            // 构造消息通知实体
            NoticeEntity notice = new NoticeEntity();
            // 开发人员申请，接收者为项目经理
            notice.setRecieverID(pmId);
            notice.setSenderID(devId);
            notice.setTitle(title);
            notice.setContent(content);
            notice.setNoticeType(NoticeType.TaskApplication.getValue());
            notice.setStatus(NoticeStatus.Unread.getValue());
            notice.setCreateTime(LocalDateTime.now());
            // 数据库执行添加消息通知
            noticeMapper.insertNotice(notice);

        }catch (Exception e){
            log.error("消息通知创建失败", e);
        }

    }

    // 添加 用户审核 的消息通知
    @Async
    public void createUserAuditNotice(int targetUserId, int adminId, NoticeApproveDTO dto){
        try{
            // 构造消息内容
            String resultText = dto.isResult() ? "通过" : "不通过";
            String reason = dto.isResult() ? "" : "\n原   因：  " + dto.getReason();
            String extraText = dto.isResult() ? "" : "\n特别提醒：  审核结果不通过的账号，不予开放业务操作，您需要重新注册，谢谢配合。";
            String targetUserName = basicCommonService.getNameById(targetUserId);

            String title = String.format("用户审核%s提醒", resultText);
            String content = String.format("""
                                       %s，您好！
                                       您的账号审核结果如下：
                                       审核结果：  %s%s%s
                                       处理时间：  %6$tY-%6$tm-%6$td %6$tH:%6$tM:%6$tS
                                       """, targetUserName, resultText, reason, extraText, LocalDateTime.now());

            // 构造消息通知实体
            NoticeEntity notice = new NoticeEntity();
            // 接收者为被审核用户
            notice.setRecieverID(targetUserId);
            notice.setSenderID(adminId);
            notice.setTitle(title);
            notice.setContent(content);
            notice.setNoticeType(NoticeType.System.getValue());
            notice.setStatus(NoticeStatus.Unread.getValue());
            notice.setCreateTime(LocalDateTime.now());
            // 数据库执行添加消息通知
            noticeMapper.insertNotice(notice);

        }catch (Exception e){
            log.error("消息通知创建失败", e);
        }

    }

    // 添加 PMO项目审批（含立项、结项审批）结果 的消息通知
    @Async
    public void createProjectApprovalNotice(int pmId, int pmoId, String projectName, Byte type, NoticeApproveDTO dto){
        try{
            // 构造消息内容
            String typeText = (type == ProjectApproveType.Create.getValue()) ? "立项" : "结项";
            String resultText = dto.isResult() ? "通过" : "驳回";
            String extraText = (type == ProjectApproveType.Create.getValue()) ?
                    "\n特别提醒：  您可以修改项目信息后重新提交立项审核。" :
                    "\n特别提醒：  您可以重新上传结项文档并重新提交结项申请。";
            extraText = dto.isResult() ? "" : extraText;
            String pmoName = basicCommonService.getNameById(pmoId);

            String title = String.format("项目 %s 的%s审核结果", projectName, typeText);
            String content = String.format("""
                                       您的项目 %s 的%s审核结果如下：
                                       审核结果：  %s
                                       审核意见：  %s
                                       审核PMO：  %s
                                       审核时间：  %6$tY-%6$tm-%6$td %6$tH:%6$tM:%6$tS%7$s
                                       """, projectName, typeText, resultText, dto.getReason(), pmoName, LocalDateTime.now(), extraText);

            // 构造消息通知实体
            NoticeEntity notice = new NoticeEntity();
            // 接收者为项目经理，发送者为PMO
            notice.setRecieverID(pmId);
            notice.setSenderID(pmoId);
            notice.setTitle(title);
            notice.setContent(content);
            notice.setNoticeType(NoticeType.ApproveResult.getValue());
            notice.setStatus(NoticeStatus.Unread.getValue());
            notice.setCreateTime(LocalDateTime.now());
            // 数据库执行添加消息通知
            noticeMapper.insertNotice(notice);

        }catch (Exception e){
            log.error("消息通知创建失败", e);
        }

    }

    // 添加 项目经理任务评审结果 的消息通知
    @Async
    public void createTaskReviewNotice(int devId, int pmId, String taskName, Integer revision, NoticeApproveDTO dto){
        try{
            // 构造消息内容
            String resultText = dto.isResult() ? "通过" : "返工";
            String extraText = dto.isResult() ? "" : "\n特别提醒：  返工次数将影响最终绩效结果，提交任务前请认真检查。";

            String title = String.format("任务 %s 的第%d次评审结果", taskName, revision);
            String content = String.format("""
                                       您提交的任务 %s 的评审结果如下：
                                       评审版次：  %d
                                       评审结果：  %s
                                       评审意见：  %s
                                       评审时间：  %5$tY-%5$tm-%5$td %5$tH:%5$tM:%5$tS%6$s
                                       """, taskName, revision, resultText, dto.getReason(), LocalDateTime.now(), extraText);

            // 构造消息通知实体
            NoticeEntity notice = new NoticeEntity();
            // 接收者为项目经理，发送者为PMO
            notice.setRecieverID(devId);
            notice.setSenderID(pmId);
            notice.setTitle(title);
            notice.setContent(content);
            notice.setNoticeType(NoticeType.ReviewResult.getValue());
            notice.setStatus(NoticeStatus.Unread.getValue());
            notice.setCreateTime(LocalDateTime.now());
            // 数据库执行添加消息通知
            noticeMapper.insertNotice(notice);

        }catch (Exception e){
            log.error("消息通知创建失败", e);
        }

    }

}
