package com.xgj.outsourcing.service.user;

import com.xgj.outsourcing.pojo.dto.auth.LoginRequestDTO;
import com.xgj.outsourcing.pojo.dto.auth.RegisterRequestDTO;
import com.xgj.outsourcing.pojo.vo.auth.LoginResponseVO;

public interface AuthService {
    // 登录
    LoginResponseVO login(LoginRequestDTO dto);
    // 注册
    boolean register(RegisterRequestDTO dto);
}
