package com.xgj.devpulse.pojo.dto.project;

import com.xgj.devpulse.pojo.dto.common.BasePageDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ProjectQueryDTO extends BasePageDTO {
    private String projectName;
    private List<Byte> statuses;
    private List<Integer> pmIds;
}
