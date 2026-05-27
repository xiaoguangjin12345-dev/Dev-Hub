package com.xgj.devpulse.mapper;

import com.xgj.devpulse.pojo.entity.OperationLogEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OperationLogMapper {

    // 插入操作日志记录
    @Insert("""
            insert into `Operation_Log`
                (UserID, ApiRoute, ExecutionTime, StatusCode, IpAddress, CreateTime)
            values
                (#{UserID}, #{ApiRoute}, #{ExecutionTime}, #{StatusCode}, #{IpAddress}, #{CreateTime})
            """)
    int insertOperationLog(OperationLogEntity dto);

}
