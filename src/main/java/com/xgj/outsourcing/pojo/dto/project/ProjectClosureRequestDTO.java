package com.xgj.outsourcing.pojo.dto.project;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.attribute.FileAttribute;

@Data
@Builder
public class ProjectClosureRequestDTO {
    // 处理结项申请文件
    private MultipartFile finalReportFile;
}
