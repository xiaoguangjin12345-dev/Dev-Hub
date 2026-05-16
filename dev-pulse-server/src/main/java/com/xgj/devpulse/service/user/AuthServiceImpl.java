package com.xgj.devpulse.service.user;

import com.xgj.devpulse.common.exception.AuthenticationException;
import com.xgj.devpulse.common.exception.BusinessException;
import com.xgj.devpulse.enums.user.Role;
import com.xgj.devpulse.mapper.DevProfileMapper;
import com.xgj.devpulse.mapper.UserMapper;
import com.xgj.devpulse.pojo.dto.auth.LoginRequestDTO;
import com.xgj.devpulse.pojo.dto.auth.RegisterRequestDTO;
import com.xgj.devpulse.pojo.entity.UserEntity;
import com.xgj.devpulse.pojo.vo.auth.LoginResponseVO;
import com.xgj.devpulse.utils.JwtUtils;
import com.xgj.devpulse.utils.PasswordHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    // 注入数据库服务
    private final UserMapper userMapper;
    private final DevProfileMapper devProfileMapper;

    // 登录 实现
    public LoginResponseVO login(LoginRequestDTO dto) {
        // 根据用户名查询数据库
        UserEntity user = userMapper.getUserByUserName(dto.getUsername());
        // 用户不存在 或 密码错误
        if (user == null || !PasswordHelper.verifyPassword(dto.getPassword(), user.getPassword())){
            throw new AuthenticationException(401, "用户名或密码错误");
        }
        // 通过JWT工具类获取token
        String token = JwtUtils.generateJwtToken(user);
        // 返回前端使用类型
        return LoginResponseVO.builder()
                .token(token)
                .userId(user.getUserID())
                .userName(user.getUsername())
                .role(user.getRole())
                .realName(user.getRealName())
                .build();
    }

    // 注册 实现
    @Transactional            // 涉及多表添加操作，需要开启事务
    public boolean register(RegisterRequestDTO dto) {
        // 两次输入密码不一致
        if (!dto.getPassword().equals(dto.getPasswordConfirm())) {
            throw new BusinessException(500, "两次密码输入不一致", false);
        }
        // 根据用户名查询数据库
        UserEntity user = userMapper.getUserByUserName(dto.getUsername());
        // 该用户名已注册
        if (user != null){
            throw new BusinessException(500, "该用户名已注册", false);
        }else {
            // 运用BCrypt算法加密密码
            dto.setPassword(PasswordHelper.hashPassword(dto.getPassword()));
            try{  // 注册
                userMapper.insertUser(dto, LocalDateTime.now());
            }
            catch (DuplicateKeyException e){    // 违反唯一性约束
                throw new BusinessException(500, "该用户名已注册", false);
            }
            // 为开发人员创建简历记录
            if (dto.getRole() == Role.DEV.getValue()){
                user = userMapper.getUserByUserName(dto.getUsername());  // 获取开发人员编号
                devProfileMapper.insertDevProfile(user.getUserID());
            }
            return true;
        }
    }

}
