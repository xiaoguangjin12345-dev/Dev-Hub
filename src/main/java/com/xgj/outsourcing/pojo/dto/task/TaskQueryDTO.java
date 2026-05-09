package com.xgj.outsourcing.pojo.dto.task;

import com.xgj.outsourcing.pojo.dto.common.BasePageDTO;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
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
