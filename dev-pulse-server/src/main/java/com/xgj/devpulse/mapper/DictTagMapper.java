package com.xgj.devpulse.mapper;

import com.xgj.devpulse.pojo.vo.common.SelectOptionVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DictTagMapper {

    // 获取用户（开发人员）或任务的技能标签名称
    @Select("""
            select dt.`TagName`
            from `Dict_Tag` as dt
            left join `Tag_Relation` as tr
            on dt.`TagID` = tr.`TagID`
            where tr.`TargetType` = #{targetType}
            and tr.`TargetId` = #{targetId}
            """)
    List<String> getTagNames(@Param("targetType") int targetType,
                             @Param("targetId") int targetId);

    // 获取技能标签下拉框选项
    List<SelectOptionVO<Integer>> getTagSelectOptions();

}
