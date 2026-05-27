package com.xgj.devpulse.mapper;

import com.xgj.devpulse.pojo.dto.hourlog.ActualHourLogQueryDTO;
import com.xgj.devpulse.pojo.dto.hourlog.ActualHourLogSubmitDTO;
import com.xgj.devpulse.pojo.entity.WorkLogEntity;
import com.xgj.devpulse.pojo.vo.hourlog.ActualHourLogListVO;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface WorkLogMapper {
    // 添加工时日志记录
    @Insert("""
            insert into `Work_Log`
                (`TaskID`, `UserID`, `Status`, `WorkDate`,
                 `Hours`, `Description`, `LastTime`)
            values
                (#{dto.taskId}, #{userId}, #{status}, #{dto.workDate},
                 #{dto.hours}, #{dto.description}, #{lastTime})
            """)
    int insertWorkLog(@Param("userId") Integer userId,
                      @Param("status") Byte status,
                      @Param("dto") ActualHourLogSubmitDTO dto,
                      @Param("lastTime") LocalDateTime lastTime);

    // 更新工时日志的状态（任务状态更新时同步执行）
    @Update("""
            update `Work_Log`
            set `Status` = #{status}
            where `TaskID` = #{taskId} and `LogID` > 0
            """)
    int updateWorkLogStatus(@Param("taskId") Integer taskId, @Param("status") Byte status);

    // 修改工时日志记录（与service共同作双重身份校验）
    // 逻辑删除工时日志记录，也用这个接口
    @Update("""
            update `Work_Log`
            set `Hours` = #{hours},
                `Description` = #{description}
            where `LogID` = #{logId}
            and `UserID` = #{userId}
            and `Status` = 1
            """)
    int updateWorkLog(@Param("logId") Integer logId,
                      @Param("userId") Integer userId,
                      @Param("hours") Integer hours,
                      @Param("description") String description,
                      @Param("lastTime") LocalDateTime lastTime);

    // 根据任务编号，更新工时记录的操作权限状态（结合任务的状态机控制）
    @Update("update `Work_Log` set `Status` = #{status} where `TaskID` = #{taskId}")
    int updateWorkLogStatusByTaskId(@Param("taskId") Integer taskId,
                                    @Param("status") Byte status);

    // 根据工时记录编号，获取该工时记录实体（用于旧工时的获取、身份核验）
    @Select("select * from `Work_Log` where `LogID` = #{logId}")
    WorkLogEntity getWorkLogByLogId(@Param("logId") Integer logId);


    // 参数化查询 实际工时记录列表（分页）
    List<ActualHourLogListVO> getWorkLogsByQuery(@Param("userId") Integer userId,
                                                 @Param("role") Byte role,
                                                 @Param("dto") ActualHourLogQueryDTO dto,
                                                 @Param("offset") Integer offset,
                                                 @Param("size") Integer size);

    // 参数化查询 实际工时记录总数
    long countWorkLogsByQuery(@Param("userId") Integer userId,
                              @Param("role") Byte role,
                              @Param("dto") ActualHourLogQueryDTO dto);


}
