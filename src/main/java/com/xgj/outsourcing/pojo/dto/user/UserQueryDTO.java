package com.xgj.outsourcing.pojo.dto.user;

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
public class UserQueryDTO extends BasePageDTO {
    // 角色
    private List<Byte> roles;
    // 状态（未验证、已验证）
    private List<Byte> statuses;
    // 真实姓名
    private String realName;
    // 技能标签
    private List<Integer> skills;
}
