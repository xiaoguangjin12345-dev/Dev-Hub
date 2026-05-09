package com.xgj.outsourcing.mapper;

import com.xgj.outsourcing.pojo.dto.notice.NoticeApproveDTO;
import com.xgj.outsourcing.pojo.entity.ProjectApprovalEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
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
