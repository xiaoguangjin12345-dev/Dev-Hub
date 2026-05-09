package com.xgj.outsourcing.pojo.dto.project;

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
public class ProjectQueryDTO extends BasePageDTO {
    private String projectName;
    private List<Byte> statuses;
    private List<Integer> pmIds;
}
