package com.xgj.devpulse.mapper;

import com.xgj.devpulse.pojo.dto.performance.PerformanceSubjectiveScoreDTO;
import com.xgj.devpulse.pojo.dto.performance.SumHoursInfoDTO;
import com.xgj.devpulse.pojo.dto.performance.ProjectPerformanceQueryDTO;
import com.xgj.devpulse.pojo.entity.ProjectPerformanceEntity;
import com.xgj.devpulse.pojo.vo.performance.ProjectPerformancePendingVO;
import com.xgj.devpulse.pojo.vo.performance.ProjectPerformanceReleasedVO;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ProjectPerformanceMapper {

    // 添加项目级绩效记录
    @Insert("""
            insert into `Project_Performance`
                (`ProjectID`, `PMOID`, `PMID`,
                 `Resource`, `Modify`, `PMOScore`,
                 `TotalScore`, `Status`)
            values
                (#{ProjectID}, #{PMOID}, #{PMID},
                #{Resource}, #{Modify}, #{PMOScore},
                #{TotalScore}, #{Status})
            """)
    int insertProjectPerformance(ProjectPerformanceEntity projectPerformanceEntity);

    // PMO提交主观评分后，更新以下信息
    @Update("""
            update `Project_Performance`
            set `PMOID` = #{pmoId},
                `PMOScore` = #{dto.score},
                `TotalScore` = #{totalScore},
                `Comment` = #{dto.comment},
                `Status` = #{status},
                `EvaluateTime` = #{evaluateTime}
            where `ProjectPerfID` = #{perfId}
            and `Status` = 1
            """)
    int updateSubjectiveScoreInfo(@Param("perfId") Integer perfId,
                                  @Param("pmoId") Integer pmoId,
                                  @Param("dto") PerformanceSubjectiveScoreDTO dto,
                                  @Param("totalScore") BigDecimal totalScore,
                                  @Param("status") Byte status,
                                  @Param("evaluateTime") LocalDateTime evaluateTime);



    // 获取待评分项目级绩效列表（分页，项目经理与开发人员不予查看）
    List<ProjectPerformancePendingVO> getPendingProjectPerformances(@Param("role") Byte role,
                                                                    @Param("status") Byte status,
                                                                    @Param("offset") Integer offset,
                                                                    @Param("size") Integer size);
    // 获取待评分项目级绩效总数（项目经理与开发人员不予查看）
    long countPendingProjectPerformances(@Param("role") Byte role,
                                         @Param("status") Byte status);



    // 获取已发布项目级绩效列表（分页，开发人员不予查看）
    List<ProjectPerformanceReleasedVO> getReleasedProjectPerformances(@Param("userId") Integer userId,
                                                                      @Param("role") Byte role,
                                                                      @Param("status") Byte status,
                                                                      @Param("dto") ProjectPerformanceQueryDTO dto,
                                                                      @Param("offset") Integer offset,
                                                                      @Param("size") Integer size);
    // 获取已发布项目级绩效总数（开发人员不予查看）
    long countReleasedProjectPerformances(@Param("userId") Integer userId,
                                          @Param("role") Byte role,
                                          @Param("status") Byte status,
                                          @Param("dto") ProjectPerformanceQueryDTO dto);



    // 根据编号，查询项目级绩效实体（内部判断使用）
    @Select("select * from `Project_Performance` where `ProjectPerfID` = #{projectPerfId}")
    ProjectPerformanceEntity getProjectPerformanceEntity(@Param("projectPerfId") Integer projectPerfId);

    // 根据项目编号，计算项目级绩效的总预估、实际工时数据
    SumHoursInfoDTO getSumHoursInfo(@Param("projectId") Integer projectId);

    // 根据项目编号，计算任务（项目）预估工时修改总次数
    @Select("select count(*) from `Task_Change_Log` where `ProjectID` = #{projectId}")
    Integer getCountModify(@Param("projectId") Integer projectId);

}
