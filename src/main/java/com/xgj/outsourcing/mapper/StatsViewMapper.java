package com.xgj.outsourcing.mapper;

import com.xgj.outsourcing.pojo.vo.stats.DevCapabilityVO;
import com.xgj.outsourcing.pojo.vo.stats.DevEfficiencyVO;
import com.xgj.outsourcing.pojo.vo.stats.HoursAnalysisVO;
import com.xgj.outsourcing.pojo.vo.stats.ProjectProgressVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StatsViewMapper {

    // 项目进度大盘
    List<ProjectProgressVO> getProjectProgressList(@Param("userId") Integer userId,
                                                   @Param("role") Byte role,
                                                   @Param("projectIds") List<Integer> projectIds);

    // 工时偏差分析
    List<HoursAnalysisVO> getHoursAnalysisList(@Param("userId") Integer userId,
                                               @Param("role") Byte role,
                                               @Param("dimension") String dimension);

    // 开发人员技能画像（针对某一开发人员，根据参与过的任务的技能标签的聚合分析）
    List<DevCapabilityVO> getDevCapabilityByDevId(@Param("currUserId") Integer currUserId,
                                                  @Param("role") Byte role,
                                                  @Param("devId") Integer devId);

    // 开发人员效能（任务完成数量、绩效平均分、总工时）
    List<DevEfficiencyVO> getDevEfficiencyList(@Param("userId") Integer userId,
                                               @Param("role") Byte role);

}
