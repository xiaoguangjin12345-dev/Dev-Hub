package com.xgj.outsourcing.service.performance;

import com.xgj.outsourcing.pojo.dto.performance.PerformanceSubjectiveScoreDTO;
import com.xgj.outsourcing.pojo.vo.common.PageResultVO;

import java.math.BigDecimal;
import java.util.List;

public interface PerformanceService<TPending, TReleased, TQuery> {
    // 生成绩效记录
    boolean createPerformance(int targetId);
    // 计算绩效总分
    BigDecimal calculateTotalScore(BigDecimal m1, BigDecimal m2, BigDecimal m3);
    // 提交主观评分并结算
    boolean updatePerformanceScore(int perfId, PerformanceSubjectiveScoreDTO dto);

    // 查看待评分绩效（仅针对评价人及以上）
    PageResultVO<TPending> getPendingPerformances(Integer pageNum, Integer pageSize);
    // 查看已发布绩效
    PageResultVO<TReleased> getReleasedPerformances(TQuery dto);
}
