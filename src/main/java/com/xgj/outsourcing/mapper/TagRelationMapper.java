package com.xgj.outsourcing.mapper;

import com.xgj.outsourcing.pojo.entity.TagRelationEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TagRelationMapper {
    // 删除标签
    @Delete("""
            delete from `Tag_Relation`
            where `TargetType` = #{targetType}
            and `TargetID` = #{targetId}
            """)
    int deleteByTargetID(@Param("targetType") int targetType,
                         @Param("targetId") int targetId);

    // 添加标签
    int insertTagRelation(@Param("targetType") int targetType,
                          @Param("targetId") int targetId,
                          @Param("skills") List<Byte> skills);

}
