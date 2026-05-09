package com.xgj.outsourcing.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordHelper {

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    // 加密：将明文密码转换为不可逆的哈希字符串
    public static String hashPassword(String password) {
        // BCrypt 会自动生成盐（Salt）并混入哈希值中
        return encoder.encode(password);
    }

    // 校验：检查明文密码与数据库里的哈希值是否匹配
    public static boolean verifyPassword(String password, String hashedPassword) {
        // BCrypt 内部会提取 hashedPassword 里的盐来处理明文，再进行比对
        return encoder.matches(password, hashedPassword);
    }
}
