package com.xgj.devpulse.pojo.dto.taskreview;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
public class TaskDeliverableDTO {
    private Integer taskId;
    private String gitUrl;
    private MultipartFile archiveFile;
    private MultipartFile docFile;
}
