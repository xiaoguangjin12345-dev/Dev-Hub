package com.xgj.devpulse.pojo.vo.notice;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NoticeVO {
    private Integer noticeId;
    private String senderName;
    private String title;
    private String content;
    private Byte noticeType;
    private Byte status;
    private LocalDateTime createTime;
}
