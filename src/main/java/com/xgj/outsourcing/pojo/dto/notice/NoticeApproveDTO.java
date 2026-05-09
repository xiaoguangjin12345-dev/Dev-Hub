package com.xgj.outsourcing.pojo.dto.notice;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NoticeApproveDTO {
    // 审核、审批类业务通用模板
    private boolean result;
    private String reason;
}
