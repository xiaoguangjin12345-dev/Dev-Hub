package com.xgj.devpulse.common.exception;

import com.xgj.devpulse.mapper.ErrorLogMapper;
import com.xgj.devpulse.pojo.entity.ErrorLogEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncErrorLogService {
    private final ErrorLogMapper errorLogMapper;

    // 数据库异步添加异常日志记录
    @Async                 // 开启异步模式
    public void saveErrorLogAsync(Integer userId, String endPoint, String msg, String stackTrace, LocalDateTime errorTime) {
        try{
            // 构造异常日志实体
            ErrorLogEntity log = new ErrorLogEntity();
            // 填写信息
            log.setUserID(userId);
            log.setEndPoint(endPoint);
            log.setExceptionMessage(msg);
            log.setStackTrace(stackTrace);
            log.setErrorTime(errorTime);

            // 执行添加记录操作
            errorLogMapper.insertErrorLog(log);

        } catch (Exception e){
            log.error("操作日志创建失败", e);
        }

    }
}
