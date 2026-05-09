package com.xgj.outsourcing.mapper;

import com.xgj.outsourcing.pojo.entity.TaskReviewEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xgj.outsourcing.pojo.vo.taskreview.TaskReviewListVO;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface TaskReviewMapper {

    // 开发人员提交任务后，添加任务评审记录
    @Insert("""
            insert into `Task_Review`
                (`TaskID`, `DevID`, `PMID`,
                 `GitUrl`, `ArchiveUrl`, `DocUrl`,
                 `Revision`, `Result`, `SubmitTime`)
            value
                (TaskID, DevID, PMID,
                 GitUrl, ArchiveUrl, DocUrl,
                 Revision, Result, SubmitTime)
            """)
    int insertTaskReview(TaskReviewEntity dto);

    // 项目经理执行任务评审时，更新以下信息
    @Update("""
            update `Task_Review`
            set `Result` = #{result},
                `Comment` = #{comment},
                `ReviewTime` = #{reviewTime}
            where `ReviewID` = #{reviewId}
            """)
    int updateExecuteTaskReviewInfo(@Param("reviewId") Integer reviewId,
                                    @Param("result") Byte result,
                                    @Param("comment") String comment,
                                    @Param("reviewTime") LocalDateTime reviewTime);


    // 查询任务评审记录列表（分页）
    List<TaskReviewListVO> getTaskReviews(@Param("userId") Integer userId,
                                          @Param("role") Byte role,
                                          @Param("offset") Integer offset,
                                          @Param("size") Integer size);
    // 查询任务评审记录总数
    long countTaskReviews(@Param("userId") Integer userId,
                          @Param("role") Byte role);


    // 根据任务评审编号，获取任务评审实体（用于内部判断）
    @Select("select * from `Task_Review` where `ReviewID` = #{reviewId}")
    TaskReviewEntity getTaskReviewEntity(@Param("reviewId") Integer reviewId);

}
