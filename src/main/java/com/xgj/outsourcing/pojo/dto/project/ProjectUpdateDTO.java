package com.xgj.outsourcing.pojo.dto.project;

import lombok.Builder;
import lombok.Data;
import org.springframework.cglib.core.Local;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;

@Data
@Builder
public class ProjectUpdateDTO {
    private String projectName;
    private String clientName;
    private String clientEmail;
    private String clientPhone;
    private String projectDescription;
    private BigDecimal budget;
    private Integer personnel;
    // 处理需求文档的文件字段
    private MultipartFile requirementFile;
    private LocalDate startDate;
    private LocalDate endDate;
}
