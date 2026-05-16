package com.xgj.devpulse.mapper;

import com.xgj.devpulse.pojo.dto.task.TaskQueryDTO;
import com.xgj.devpulse.pojo.entity.TaskEntity;
import com.xgj.devpulse.pojo.vo.common.SelectOptionVO;
import com.xgj.devpulse.pojo.vo.task.TaskDetailsVO;
import com.xgj.devpulse.pojo.vo.task.TaskListVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface TaskMapper {

    // 添加任务记录
    int insertTask(@Param("dto") TaskEntity dto);

    // 根据任务编号，更新技能标签字段
    @Update("update `Task` set `RequiredSkills` = #{requiredSkills} where `TaskID` = #{taskId}")
    int updateSkillsById(@Param("taskId") Integer taskId,
                         @Param("requiredSkills") String requiredSkills);

    // 根据任务编号，更新任务的实际工时
    @Update("""
            update `Task`
            set `ActualHours` = `ActualHours` + #{changeValue}
            where `TaskID` = #{taskId}
            """)
    int updateActualHoursById(@Param("taskId") Integer taskId,
                              @Param("changeValue") Integer changeValue);

    // 根据任务编号，更新任务的预估工时
    @Update("""
            update `Task`
            set `EstimatedHours` = #{newHours}
            where `TaskID` = #{taskId}
            """)
    int updateEstimatedHoursById(@Param("taskId") Integer taskId,
                                 @Param("newHours") Integer newHours);

    // 任务成功匹配指定开发人员时，更新以下信息（状态条件检验）
    @Update("""
            update `Task`
            set `DevID` = #{devId},
                `AssignTime` = #{assignTime},
                `Status` = #{status}
            where `TaskID` = #{taskId}
            and `Status` = 1
            """)
    int updateTaskAssignInfo(@Param("taskId") Integer taskId,
                             @Param("devId") Integer devId,
                             @Param("status") Byte status,
                             @Param("assignTime") LocalDateTime assignTime);

    // 开发人员提交任务成果时，更新以下信息（乐观锁 版本号判断）
    @Update("""
            update `Task`
            set `Status` = #{status},
                `Revision` = `Revision` + 1
            where `TaskID` = #{taskId}
            and `Revision` = #{oldRevision}
            """)
    int updateTaskReviewInfo(@Param("taskId") Integer taskId,
                             @Param("status") Byte status,
                             @Param("oldRevision") Integer oldRevision);

    // 根据任务编号，单纯修改任务状态（目前仅在项目经理评审不通过时用）
    @Update("update `Task` set `Status` = #{status} where `TaskID` = #{taskId}")
    int updateRejectReviewInfo(@Param("taskId") Integer taskId,
                               @Param("status") Byte status);

    // 项目经理评审任务通过时，更新以下信息
    @Update("""
            update `Task`
            set `Status` = #{status},
                `FinishTime` = #{finishTime}
            where `TaskID` = #{taskId}
            """)
    int updateTaskFinishInfo(@Param("taskId") Integer taskId,
                             @Param("status") Byte status,
                             @Param("finishTime") LocalDateTime finishTime);



    // 参数化查询任务列表（分页，含数据隔离）
    List<TaskListVO> getTasksByQuery(@Param("userId") Integer userId,
                                     @Param("role") Byte role,
                                     @Param("dto") TaskQueryDTO dto,
                                     @Param("offset") Integer offset,
                                     @Param("size") Integer size);
    // 参数化查询任务总数（含数据隔离）
    long countTasksByQuery(@Param("userId") Integer userId,
                           @Param("role") Byte role,
                           @Param("dto") TaskQueryDTO dto);


    // 参数化查询 开发人员待分配任务列表（分页，含数据隔离）
    List<TaskListVO> getTaskSquareByQuery(@Param("dto") TaskQueryDTO dto,
                                          @Param("status") Byte status,
                                          @Param("offset") Integer offset,
                                          @Param("size") Integer size);
    // 参数化查询 开发人员待分配任务总数（含数据隔离）
    long countTaskSquareByQuery(@Param("dto") TaskQueryDTO dto,
                                @Param("status") Byte status);



    // 根据编号查询任务详情
    TaskDetailsVO getTaskDetails(@Param("taskId") Integer taskId,
                                 @Param("role") Byte role);

    // 根据任务编号，查询任务实体（内部使用）
    @Select("select * from `Task` where `TaskID` = #{taskId}")
    TaskEntity getTaskEntity(@Param("taskId") Integer taskId);

    // 根据项目编号，查询任务实体
    @Select("select * from `Task` where `ProjectID` = #{projectId}")
    TaskEntity getTaskEntityByProjectId(@Param("projectId") Integer projectId);

    // 根据项目编号，查询该项目下的任务数量
    @Select("select count(*) from `Task` where `ProjectID` = #{projectId}")
    Integer getTaskCountByProjectId(@Param("projectId") Integer projectId);

    // 根据任务编号，查找该任务所属项目的项目经理
    @Select("""
            select p.`PMID`
            from `Project` p
            left join `Task` t
            on p.`ProjectID` = t.`ProjectID`
            where t.`TaskID` = #{taskId}
            """)
    Integer getPmIdByTaskId(@Param("taskId") Integer taskId);

    // 根据任务编号，查询任务名称
    @Select("select `TaskName` from `Task` where `TaskID` = #{taskId}")
    String getTaskNameByTaskId(@Param("taskId") Integer taskId);

    // 获取任务下拉框
    // 开发人员用此填报工时，因此要严格限定权限及任务状态（只能为进行中）
    // 项目经理只能查看自己创建项目下辖的任务
    // PMO和系统管理员可获取所有数据
    List<SelectOptionVO<Integer>> getTaskSelectOptions(@Param("userId") Integer userId,
                                                       @Param("role") Byte role);

}
