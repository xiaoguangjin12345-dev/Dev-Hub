package com.xgj.outsourcing.mapper;

import com.xgj.outsourcing.pojo.entity.ErrorLogEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
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
