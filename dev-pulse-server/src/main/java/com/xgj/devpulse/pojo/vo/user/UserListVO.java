package com.xgj.devpulse.pojo.vo.user;

import lombok.Data;

@Data
public class UserListVO {
    protected int userId;
    protected String userName;
    protected String realName;
    protected Byte role;
    protected String email;
    protected String phone;
    protected String skills;
}
