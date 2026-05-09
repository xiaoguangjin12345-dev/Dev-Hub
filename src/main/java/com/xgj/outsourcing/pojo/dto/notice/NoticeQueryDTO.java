package com.xgj.outsourcing.pojo.dto.notice;

import com.xgj.outsourcing.pojo.dto.common.BasePageDTO;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

@Data
@SuperBuilder
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
