package com.xgj.devpulse.mapper;

import com.xgj.devpulse.pojo.dto.ai.TokenUsageDTO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

@Mapper
public interface TokenUsageLogMapper {

    // 添加Token使用记录
    @Insert("""
            insert `Token_Usage_Log`
                (`UserID`, `ModelName`, `ResponseID`, `Type`,
                 `PromptTokens`, `CachedTokens`, `CompletionTokens`, `TotalTokens`,
                 `CreateTime`)
            value 
                (#{dto.userId}, #{dto.modelName}, #{dto.ResponseId}, #{dto.type},
                 #{dto.promptTokens}, #{dto.cachedTokens}, #{dto.completionTokens}, #{dto.totalTokens},
                 #{createTime})
            """)
    int insertTokenUsageLog(@Param("dto") TokenUsageDTO dto,
                            @Param("createTime") LocalDateTime createTime);

}
