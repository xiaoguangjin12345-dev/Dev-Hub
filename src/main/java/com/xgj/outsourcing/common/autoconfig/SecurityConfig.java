package com.xgj.outsourcing.common.autoconfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // 关掉跨站请求伪造防护
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // 允许所有请求通过，不走 Security 的登录
                );
        return http.build();
    }
}