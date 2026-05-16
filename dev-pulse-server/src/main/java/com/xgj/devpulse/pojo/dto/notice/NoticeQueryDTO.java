package com.xgj.devpulse.pojo.dto.notice;

import com.xgj.devpulse.pojo.dto.common.BasePageDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class NoticeQueryDTO extends BasePageDTO {
    private List<Byte> statuses;
    private List<Byte> noticeTypes;
    private String senderName;
    private String title;

    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
