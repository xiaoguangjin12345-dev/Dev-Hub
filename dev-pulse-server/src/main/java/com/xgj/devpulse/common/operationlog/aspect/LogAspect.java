package com.xgj.devpulse.common.operationlog.aspect;

import com.xgj.devpulse.common.operationlog.annotation.Log;
import com.xgj.devpulse.common.context.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LogAspect {
    private final AsyncLogService asyncLogService;

    @Around("@annotation(logAnnotation)")
    public Object getLog(ProceedingJoinPoint point, Log logAnnotation) throws Throwable {
        // 获取数据
        ServletRequestAttributes attr = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attr.getRequest();

        // 记录开始时间
        long startTime = System.currentTimeMillis();
        LocalDateTime createTime = LocalDateTime.now();

        // 执行业务Controller（异常已由业务代码抛出）
        Object result = point.proceed();

        // 记录结束时间并计算接口耗时
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        // 获取操作用户编号
        Integer userId = UserContext.getCurrentUserId();
        if(userId == 0){
            userId = null;
        }
        // 获取操作接口
        String requestURI = String.format("%s: %s", logAnnotation.value(), request.getRequestURI());
        // 获取状态码
        int statusCode = attr.getResponse().getStatus();
        // 获取操作IP
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null) {
            ipAddress = attr.getRequest().getRemoteAddr();
        }

        // 创建操作日志记录并保存（调用void类型的异步方法）
        asyncLogService.saveOperationLogAsync(userId, requestURI, statusCode, ipAddress, executionTime, createTime);

        return result;
    }



}
