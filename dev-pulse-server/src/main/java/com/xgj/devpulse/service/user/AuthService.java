package com.xgj.devpulse.service.user;

import com.xgj.devpulse.pojo.dto.auth.LoginRequestDTO;
import com.xgj.devpulse.pojo.dto.auth.RegisterRequestDTO;
import com.xgj.devpulse.pojo.vo.auth.LoginResponseVO;

public interface AuthService {
    // 登录
    LoginResponseVO login(LoginRequestDTO dto);
    // 注册
    boolean register(RegisterRequestDTO dto);
}
