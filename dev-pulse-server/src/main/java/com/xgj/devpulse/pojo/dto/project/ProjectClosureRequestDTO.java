package com.xgj.devpulse.pojo.dto.project;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
public class ProjectClosureRequestDTO {
    // 处理结项申请文件
    private MultipartFile finalReportFile;
}
