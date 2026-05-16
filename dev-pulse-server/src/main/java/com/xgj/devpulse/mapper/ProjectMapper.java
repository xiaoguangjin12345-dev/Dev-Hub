package com.xgj.devpulse.mapper;

import com.xgj.devpulse.pojo.dto.project.ProjectQueryDTO;
import com.xgj.devpulse.pojo.dto.project.ProjectUpdateDTO;
import com.xgj.devpulse.pojo.entity.ProjectEntity;
import com.xgj.devpulse.pojo.vo.common.SelectOptionVO;
import com.xgj.devpulse.pojo.vo.project.ProjectDetailsVO;
import com.xgj.devpulse.pojo.vo.project.ProjectListVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ProjectMapper {
    // 添加项目记录
    int insertProject(@Param("dto") ProjectEntity dto);

    // 修改需求文档字段（仅在添加项目记录时使用）
    @Update("update `Project` set `RequirementDocUrl` = #{requirementUrl} where `ProjectID` = #{projectId}")
    int updateRequirementFileInfo(@Param("projectId") Integer projectId,
                                  @Param("requirementUrl") String requirementUrl);

    // 修改项目记录（立项阶段）
    int updateProject(@Param("projectId") Integer projectId,
                      @Param("dto") ProjectUpdateDTO dto,
                      @Param("status") Byte status,
                      @Param("requirementDocUrl") String requirementDocUrl);

    // PMO立项审批通过时，更新以下信息
    @Update("""
            update `Project`
            set `Status` = #{status},
                `ApprovalTime` = #{approvalTime}
            where `ProjectID` = #{projectId}
            """)
    int updateProjectApprovalInfo(@Param("projectId") Integer projectId,
                                  @Param("status") Byte status,
                                  @Param("approvalTime") LocalDateTime approvalTime);

    // PM提交结项时，更新以下信息（更新状态字段、结项文档字段）
    @Update("""
            update `Project`
            set `Status` = #{status},
                `FinalReportUrl` = #{finalReportUrl}
            where `ProjectID` = #{projectId}
            """)
    int updateProjectClosureInfo(@Param("projectId") Integer projectId,
                                 @Param("status") Byte status,
                                 @Param("finalReportUrl") String finalReportUrl);

    // PMO结项审批通过时，更新以下信息
    @Update("""
            update `Project`
            set `Status` = #{status},
                `FinishTime` = #{finishTime}
            where `ProjectID` = #{projectId}
            """)
    int updateProjectFinishInfo(@Param("projectId") Integer projectId,
                                @Param("status") Byte status,
                                @Param("finishTime") LocalDateTime finishTime);

    // 单纯修改项目状态（目前仅在PMO立项、结项审批不通过时使用）
    @Update("""
            update `Project`
            set `Status` = #{status}
            where `ProjectID` = #{projectId}
            """)
    int updateRejectApprovalInfo(@Param("projectId") Integer projectId,
                                 @Param("status") Byte status);


    // 多条件查询项目（分页）
    List<ProjectListVO> getProjectsByQuery(@Param("userId") Integer userId,
                                           @Param("role") Byte role,
                                           @Param("dto") ProjectQueryDTO dto,
                                           @Param("offset") Integer offset,
                                           @Param("size") Integer size);

    // 多条件查询项目总数
    long countProjectsByQuery(@Param("userId") Integer userId,
                              @Param("role") Byte role,
                              @Param("dto") ProjectQueryDTO dto);


    // 根据编号查询项目详情
    ProjectDetailsVO getProjectById(@Param("projectId") Integer projectId);


    // 判断该项目下所有任务是否全部完成
    @Select("""
            select
                case when
                    count(*) > 0
                    and
                    count(case when `Status` = #{status} then 1 end) = count(*)
                then 1 else 0
                end
            from `Task`
            where `ProjectID` = #{projectId}
            """)
    boolean isAllTaskFinishedByProjectId(@Param("projectId") Integer projectId,
                                         @Param("status") Byte status);

    // 判断该项目下所有任务级绩效是否全部完成评定
    @Select("""
            select
                case when
                    count(*) > 0
                    and
                    count(case when tp.`Status` = #{status} then 1 end) = count(*)
                then 1 else 0
                end
            from `Task_Performance` tp
            left join `Task` t
            on tp.`TaskID` = t.`TaskID`
            where t.`ProjectID` = #{projectId}
            """)
    boolean isAllTaskPerformanceReleasedByProjectId(@Param("projectId") Integer projectId,
                                                    @Param("status") Byte status);

    // 根据编号，获取项目实体（内部使用）
    @Select("select * from `Project` where `ProjectID` = #{projectId}")
    ProjectEntity getProjectEntity(@Param("projectId") Integer projectId);

    // 查询指定项目状态
    @Select("select `Status` from `Project` where `ProjectID` = #{projectId}")
    Integer getProjectStatus(@Param("projectId") Integer projectId);

    // 查询指定项目的项目经理编号
    @Select("select `PMID` from `Project` where `ProjectID` = #{projectId}")
    Integer getProjectPmId(@Param("projectId") Integer projectId);

    // 获取项目下拉框列表
    List<SelectOptionVO<Integer>> getProjectSelectOptions(@Param("userId") Integer userId,
                                                          @Param("role") Byte role);
}
