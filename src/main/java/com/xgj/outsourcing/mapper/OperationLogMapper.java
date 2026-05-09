package com.xgj.outsourcing.mapper;

import com.xgj.outsourcing.pojo.entity.OperationLogEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OperationLogMapper {

    // 插入操作日志记录
    @Insert("""
            insert into `Operation_Log`
                (UserID, ApiRoute, ExecutionTime, StatusCode, IpAddress, CreateTime)
            value
                (#{UserID}, #{ApiRoute}, #{ExecutionTime}, #{StatusCode}, #{IpAddress}, #{CreateTime})
            """)
    int insertOperationLog(OperationLogEntity dto);

}
