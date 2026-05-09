package com.xgj.outsourcing.pojo.vo.user;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
public class UserListVO {
    protected int userId;
    protected String userName;
    protected String realName;
    protected Byte role;
    protected String email;
    protected String phone;
    protected String skills;
}
