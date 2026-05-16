package com.xgj.devpulse.pojo.dto.task;

import com.xgj.devpulse.pojo.dto.common.BasePageDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TaskQueryDTO extends BasePageDTO {
    private String taskName;
    private String projectName;
    private String pmName;
    private String devName;
    private List<Integer> projectIds;
    private List<Byte> statuses;
    private List<Integer> skills;
//    private Integer projectId;
}
