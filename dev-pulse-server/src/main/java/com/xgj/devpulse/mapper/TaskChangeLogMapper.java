package com.xgj.devpulse.mapper;

import com.xgj.devpulse.pojo.dto.hourlog.EstimatedHourChangeLogQueryDTO;
import com.xgj.devpulse.pojo.vo.hourlog.EstimatedHourChangeLogListVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface TaskChangeLogMapper {

    // 添加任务预估工时修改记录
    @Insert("""
            insert into `Task_Change_Log`
                (`TaskID`, `ProjectID`, `PMID`, `OldHours`, `NewHours`, `ChangeReason`, `ChangeTime`)
            value
                (#{taskId}, #{projectId}, #{userId}, #{oldHours}, #{newHours}, #{changeReason}, #{changeTime})
            """)
    int insertTaskChangeLog(@Param("userId") Integer userId,
                            @Param("taskId") Integer taskId,
                            @Param("projectId") Integer projectId,
                            @Param("oldHours") Integer oldHours,
                            @Param("newHours") Integer newHours,
                            @Param("changeReason") String changeReason,
                            @Param("changeTime") LocalDateTime changeTime);


    // 参数化查询 任务预估工时修改记录列表（分页）
    List<EstimatedHourChangeLogListVO> getTaskChangeLogsByQuery(@Param("userId") Integer userId,
                                                                @Param("role") Byte role,
                                                                @Param("dto") EstimatedHourChangeLogQueryDTO dto,
                                                                @Param("offset") Integer offset,
                                                                @Param("size") Integer size);

    // 参数化查询 任务预估工时修改记录总数
    long countTaskChangeLogsByQuery(@Param("userId") Integer userId,
                                    @Param("role") Byte role,
                                    @Param("dto") EstimatedHourChangeLogQueryDTO dto);

}
