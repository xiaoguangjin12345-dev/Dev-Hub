package com.xgj.devpulse.common.interceptor;

import com.xgj.devpulse.common.exception.AuthenticationException;
import com.xgj.devpulse.utils.JwtUtils;
import com.xgj.devpulse.common.context.UserContext;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    // 请求前的拦截器校验
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 浏览器预检请求，不拦截
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        // 只拦截映射到Controller方法的请求
        if (!(handler instanceof org.springframework.web.method.HandlerMethod)) {
            return true;
        }

        // 获取token
        String token = this.getToken(request);

        // 若没有token信息，返回认证异常
        if (token == null || token.isEmpty()) {
            throw new AuthenticationException(401, "认证失效，请重新登录");
        }
        // 解析token
        Claims claims = JwtUtils.parseToken(token);
        if (claims == null) {
            throw new AuthenticationException(401, "登录已过期，请重新登录");
        }
        // 设置用户上下文信息
        UserContext.setClaims(claims);
        return true;
    }

    // 请求结束时的清理
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 请求结束，必须清理，否则在线程池模式下会导致用户信息错乱
        UserContext.remove();
    }

    // 根据请求信息获取token
    private String getToken(HttpServletRequest request) {
        // 从Header中获取token
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

}
