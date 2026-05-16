package com.xgj.devpulse.controller;

import com.xgj.devpulse.common.operationlog.annotation.Log;
import com.xgj.devpulse.pojo.dto.auth.LoginRequestDTO;
import com.xgj.devpulse.pojo.dto.auth.RegisterRequestDTO;
import com.xgj.devpulse.pojo.vo.auth.LoginResponseVO;
import com.xgj.devpulse.service.user.AuthService;
import com.xgj.devpulse.common.response.APIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    // 注入服务
    private final AuthService authService;

    @Log("登录")
    @PostMapping("/login")
    public APIResponse<LoginResponseVO> login(@RequestBody LoginRequestDTO dto) {
        LoginResponseVO result = authService.login(dto);
        return APIResponse.success(result, "登录成功");
    }

    @Log("注册")
    @PostMapping("/register")
    public APIResponse<Boolean> register(@RequestBody RegisterRequestDTO dto) {
        authService.register(dto);
        return APIResponse.success(true, "注册成功");
    }

}
