package com.xgj.outsourcing.common.operationlog.aspect;

import com.xgj.outsourcing.mapper.OperationLogMapper;
import com.xgj.outsourcing.pojo.entity.OperationLogEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncLogService {
    private final OperationLogMapper operationLogMapper;

    // 数据库异步添加操作日志记录
    @Async              // 开启异步模式
    public void saveOperationLogAsync(Integer userId, String requestURI, int statusCode,
                                 String ipAddress, long executionTime, LocalDateTime createTime) {
        try{
            // 构造操作日志实体
            OperationLogEntity log = new OperationLogEntity();
            // 填写字段
            log.setUserID(userId);
            log.setApiRoute(requestURI);
            log.setExecutionTime((int)executionTime);
            log.setStatusCode(statusCode);
            log.setIpAddress(ipAddress);
            log.setCreateTime(createTime);

            // 数据库插入操作日志记录
            operationLogMapper.insertOperationLog(log);

        }catch (Exception e){
            log.error("操作日志创建失败", e);
        }

    }
}
