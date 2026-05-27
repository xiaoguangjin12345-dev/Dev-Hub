package com.xgj.devpulse.mapper;

import com.xgj.devpulse.pojo.dto.performance.PerformanceSubjectiveScoreDTO;
import com.xgj.devpulse.pojo.dto.performance.TaskPerformanceQueryDTO;
import com.xgj.devpulse.pojo.entity.TaskPerformanceEntity;
import com.xgj.devpulse.pojo.vo.performance.TaskPerformancePendingVO;
import com.xgj.devpulse.pojo.vo.performance.TaskPerformanceReleasedVO;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface TaskPerformanceMapper {

    // 添加任务级绩效记录
    @Insert("""
            insert into `Task_Performance`
                (`TaskID`, `PMID`, `DevID`,
                 `Quality`, `Efficiency`, `PMScore`,
                 `TotalScore`, `Status`)
            values
                (#{TaskID}, #{PMID}, #{DevID},
                #{Quality}, #{Efficiency}, #{PMScore},
                #{TotalScore}, #{Status})
            """)
    int insertTaskPerformance(TaskPerformanceEntity taskPerformanceEntity);

    // 项目经理提交主观评分后，更新以下信息
    @Update("""
            update `Task_Performance`
            set `PMScore` = #{dto.score},
                `TotalScore` = #{totalScore},
                `Comment` = #{dto.comment},
                `Status` = #{status},
                `EvaluateTime` = #{evaluateTime}
            where `TaskPerfID` = #{perfId}
            and `Status` = 1
            """)
    int updateSubjectiveScoreInfo(@Param("perfId") Integer perfId,
                                  @Param("dto") PerformanceSubjectiveScoreDTO dto,
                                  @Param("totalScore") BigDecimal totalScore,
                                  @Param("status") Byte status,
                                  @Param("evaluateTime") LocalDateTime evaluateTime);



    // 获取待评分任务级绩效列表（分页，开发人员不予查看）
    List<TaskPerformancePendingVO> getPendingTaskPerformances(@Param("userId") Integer userId,
                                                              @Param("role") Byte role,
                                                              @Param("status") Byte status,
                                                              @Param("offset") Integer offset,
                                                              @Param("size") Integer size);
    // 获取待评分任务级绩效总数（开发人员不予查看）
    long countPendingTaskPerformances(@Param("userId") Integer userId,
                                      @Param("role") Byte role,
                                      @Param("status") Byte status);



    // 获取已发布任务级绩效列表（分页）
    List<TaskPerformanceReleasedVO> getReleasedTaskPerformances(@Param("userId") Integer userId,
                                                                @Param("role") Byte role,
                                                                @Param("status") Byte status,
                                                                @Param("dto") TaskPerformanceQueryDTO dto,
                                                                @Param("offset") Integer offset,
                                                                @Param("size") Integer size);
    // 获取已发布任务级绩效总数
    long countReleasedTaskPerformances(@Param("userId") Integer userId,
                                       @Param("role") Byte role,
                                       @Param("status") Byte status,
                                       @Param("dto") TaskPerformanceQueryDTO dto);



    // 根据编号，查询任务级绩效实体（内部判断使用）
    @Select("select * from `Task_Performance` where `TaskPerfID` = #{taskPerfId}")
    TaskPerformanceEntity getTaskPerformanceEntity(@Param("taskPerfId") Integer taskPerfId);

}
