package com.xgj.devpulse.mapper;

import com.xgj.devpulse.pojo.entity.TaskApplicationEntity;
import com.xgj.devpulse.pojo.vo.taskapplication.TaskApplicationListVO;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface TaskApplicationMapper {
    // 添加普通任务申请记录
    @Insert("""
            insert into `Task_Application`
                (`TaskID`, `PMID`, `DevID`, `Type`, `Status`, `ApplyTime`)
            values
                (#{taskId}, #{pmId}, #{devId}, #{type}, 1, #{applyTime})
            """)
    int insertTaskApplication(@Param("taskId") Integer taskId,
                              @Param("pmId") Integer pmId,
                              @Param("devId") Integer devId,
                              @Param("type") Byte type,
                              @Param("applyTime") LocalDateTime applyTime);

    // 直接添加成功的任务申请记录
    @Insert("""
            insert into `Task_Application`
                (`TaskID`, `PMID`, `DevID`, `Type`, `Status`, `ApplyTime`, `DealTime`)
            values
                (#{taskId}, #{pmId}, #{devId}, #{type}, 2, #{localTime}, #{localTime})
            """)
    int insertSuccessApplication(@Param("taskId") Integer taskId,
                                 @Param("pmId") Integer pmId,
                                 @Param("devId") Integer devId,
                                 @Param("type") Byte type,
                                 @Param("localTime") LocalDateTime localTime);

    // 判断是否存在任务相同、目标用户为自己，但是申请方向相反的任务申请记录，用于优化双选流程
    @Select("""
            select `ApplicationID`
            from `Task_Application`
            where `TaskID` = #{taskId}
            and `PMID` = #{pmId}
            and `DevID` = #{devId}
            and `Type` = #{type}
            and `Status` = #{status}
            """)
    Integer getOppositeApplicationId(@Param("taskId") Integer taskId,
                                     @Param("pmId") Integer pmId,
                                     @Param("devId") Integer devId,
                                     @Param("type") Byte type,
                                     @Param("status") Byte status);

    // 根据任务申请记录编号，更新任务申请状态
    @Update("""
            update `Task_Application`
            set `Status` = #{status},
                `DealTime` = #{dealTime}
            where `ApplicationID` = #{appId}
            """)
    int updateTaskApplicationStatusByAppId(@Param("appId") Integer appId,
                                           @Param("status") Byte status,
                                           @Param("dealTime") LocalDateTime dealTime);

    // 根据任务编号，更新任务申请状态（排除当前任务申请编号）
    @Update("""
            update `Task_Application`
            set `Status` = #{status},
                `DealTime` = #{dealTime}
            where `TaskID` = #{taskId}
            and `ApplicationID` != #{appId}
            """)
    int updateTaskApplicationStatusByTaskId(@Param("appId") Integer appId,
                                            @Param("taskId") Integer taskId,
                                            @Param("status") Byte status,
                                            @Param("dealTime") LocalDateTime dealTime);

    // 根据任务申请记录编号，获取该任务申请记录实体（用于幂等性判断、身份核验、任务编号字段获取）
    @Select("select * from `Task_Application` where `ApplicationID` = #{appId}")
    TaskApplicationEntity getTaskApplicationByAppId(@Param("appId") Integer appId);

    // 查看邀请/申请列表
    List<TaskApplicationListVO> getTaskApplications(@Param("userId") Integer userId,
                                                    @Param("role") Byte role,
                                                    @Param("type") Byte type);

}
