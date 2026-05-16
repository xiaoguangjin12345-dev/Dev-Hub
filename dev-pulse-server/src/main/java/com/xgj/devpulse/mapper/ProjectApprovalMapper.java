package com.xgj.devpulse.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

@Mapper
public interface ProjectApprovalMapper {
    // 添加审批记录（含立项、结项审批）
    @Insert("""
            insert into `Project_Approval`
                (`ProjectID`, `PMOID`, `ApprovalType`,
                 `Result`, `Comment`, `ApprovalTime`)
            value
                (#{projectId}, #{pmoId}, #{approveType},
                 #{result}, #{comment}, #{approvalTime});
            """)
    int insertProjectApproval(@Param("approveType") Byte approveType,
                              @Param("pmoId") Integer pmoId,
                              @Param("projectId") Integer projectId,
                              @Param("result") Byte result,
                              @Param("comment") String comment,
                              @Param("approvalTime")LocalDateTime approvalTime);

}
