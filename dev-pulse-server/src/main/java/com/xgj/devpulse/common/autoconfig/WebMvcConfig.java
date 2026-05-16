package com.xgj.devpulse.common.autoconfig;

import com.xgj.devpulse.common.interceptor.JwtInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    // 注入拦截器
    private final JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")             // 拦截所有路径
                .excludePathPatterns(               // 排除不需要检查的白名单
                        "/auth/login",           // 登录
                        "/auth/register",        // 注册
                        "/static/**",            // 静态资源
                        "/error",                // 错误信息页
                        // 测试接口相关
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/v3/api-docs",
                        "/v3/api-docs/**",
                        "/swagger-resources/**",
                        "/webjars/**",
                        // Python操作专用（暂时不拦截，后期需要）
                        "/sdk/ai/**"
                );
    }
}
