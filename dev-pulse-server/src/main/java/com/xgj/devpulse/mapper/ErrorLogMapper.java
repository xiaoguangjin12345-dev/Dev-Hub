package com.xgj.devpulse.mapper;

import com.xgj.devpulse.pojo.entity.ErrorLogEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ErrorLogMapper {

    // 添加异常日志记录
    @Insert("""
            insert into `Error_Log`
                (`UserID`, `EndPoint`, `ExceptionMessage`, `StackTrace`, `ErrorTime`)
            value
                (#{UserID}, #{EndPoint}, #{ExceptionMessage}, #{StackTrace}, #{ErrorTime})
            """)
    int insertErrorLog(ErrorLogEntity dto);

}
