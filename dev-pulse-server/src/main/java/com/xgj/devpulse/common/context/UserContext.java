package com.xgj.devpulse.common.context;

import com.xgj.devpulse.enums.user.Role;
import com.xgj.devpulse.pojo.entity.UserEntity;
import io.jsonwebtoken.Claims;

public class UserContext {

    // 使用 ThreadLocal 存储当前线程的 Claims
    private static final ThreadLocal<Claims> USER_CLAIMS_HOLDER = new ThreadLocal<>();

    // 存入（由拦截器调用）
    public static void setClaims(Claims claims) {
        USER_CLAIMS_HOLDER.set(claims);
    }

    // 获取原始 Claims
    public static Claims getClaims() {
        return USER_CLAIMS_HOLDER.get();
    }

    // 获取 ID
    public static int getCurrentUserId() {
        Claims claims = getClaims();
        if (claims != null && claims.get("id") != null) {
            return Integer.parseInt(claims.get("id").toString());
        }
        return 0;
    }

    // 获取角色
    public static Role getCurrentRole() {
        Claims claims = getClaims();
        if (claims != null && claims.get("role") != null) {
            return Role.fromValue(Byte.parseByte(claims.get("role").toString()));
        }
        return Role.None;
    }

    // 获取用户名
    public static String getCurrentUsername() {
        Claims claims = getClaims();
        return (claims != null && claims.get("username") != null) ? claims.get("username").toString() : "";
    }

    // 获取真实姓名
    public static String getCurrentRealName() {
        Claims claims = getClaims();
        return (claims != null && claims.get("realName") != null) ? claims.get("realName").toString() : "";
    }

    // 清理ThreadLocal，防止内存泄漏
    public static void remove() {
        USER_CLAIMS_HOLDER.remove();
    }
}
