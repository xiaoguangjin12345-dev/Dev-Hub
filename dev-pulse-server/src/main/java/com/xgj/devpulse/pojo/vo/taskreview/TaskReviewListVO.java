package com.xgj.devpulse.pojo.vo.taskreview;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TaskReviewListVO {
    private Integer reviewId;
    private Integer taskId;
    private String taskName;
    private String devName;
    private String pmName;
    private String gitUrl;
    private String archiveFileUrl;
    private String docFileUrl;
    private Integer revision;
    private Byte result;
    private String comment;
    private LocalDateTime submitTime;
    private LocalDateTime reviewTime;
}
