package com.xgj.outsourcing.service.performance;

import com.xgj.outsourcing.common.context.UserContext;
import com.xgj.outsourcing.common.exception.AuthorizationException;
import com.xgj.outsourcing.common.exception.BusinessException;
import com.xgj.outsourcing.enums.performance.PerformanceStatus;
import com.xgj.outsourcing.enums.user.Role;
import com.xgj.outsourcing.mapper.ProjectMapper;
import com.xgj.outsourcing.mapper.ProjectPerformanceMapper;
import com.xgj.outsourcing.pojo.dto.performance.PerformanceSubjectiveScoreDTO;
import com.xgj.outsourcing.pojo.dto.performance.ProjectPerformanceQueryDTO;
import com.xgj.outsourcing.pojo.dto.performance.SumHoursInfoDTO;
import com.xgj.outsourcing.pojo.entity.ProjectEntity;
import com.xgj.outsourcing.pojo.entity.ProjectPerformanceEntity;
import com.xgj.outsourcing.pojo.vo.common.PageResultVO;
import com.xgj.outsourcing.pojo.vo.performance.ProjectPerformancePendingVO;
import com.xgj.outsourcing.pojo.vo.performance.ProjectPerformanceReleasedVO;
import com.xgj.outsourcing.pojo.vo.performance.TaskPerformancePendingVO;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ProjectPerformanceService implements
        PerformanceService<ProjectPerformancePendingVO, ProjectPerformanceReleasedVO, ProjectPerformanceQueryDTO> {
    private final ProjectPerformanceMapper projectPerformanceMapper;
    private final ProjectMapper projectMapper;
    private final RedissonClient redissonClient;

    // 生成项目级绩效记录
    public boolean createPerformance(int projectId){
        // 数据库查询项目实体
        ProjectEntity project = projectMapper.getProjectEntity(projectId);
        // 查询不到该项目
        if(project == null){
            throw new BusinessException(404, "查询不到该项目", true);
        }

        // 计算 资源控制率得分R
        // 获取该项目下所有任务的总预估、实际工时数据
        SumHoursInfoDTO sumHours = projectPerformanceMapper.getSumHoursInfo(projectId);
        // 调用方法计算
        double resource = this.calculateResource(sumHours);

        // 计算 预估工时修改审计扣分
        int K = 2;
        double modify = (double)K * projectPerformanceMapper.getCountModify(projectId);

        // 构造绩效实体
        ProjectPerformanceEntity performance= new ProjectPerformanceEntity();
        // 填写关键信息
        performance.setProjectID(projectId);
        performance.setPMID(project.getPMID());
        // PMO信息不填，因为项目级绩效是池化评定机制，初始还不知道是哪个PMO评分
        // 填写客观得分信息
        performance.setResource(BigDecimal.valueOf(resource));
        performance.setModify(BigDecimal.valueOf(modify));
        // 填写初始值与初始状态
        performance.setPMOScore(BigDecimal.valueOf(0));
        performance.setTotalScore(BigDecimal.valueOf(0));
        performance.setStatus(PerformanceStatus.Pending.getValue());

        // 数据库添加待评分绩效记录
        projectPerformanceMapper.insertProjectPerformance(performance);

        return true;
    }

    // 提交主观评分并结算
    public boolean updatePerformanceScore(int perfId, PerformanceSubjectiveScoreDTO dto){
        int userId = UserContext.getCurrentUserId();
        byte role = UserContext.getCurrentRole().getValue();

        // 非PMO，不予操作
        if(role != Role.PMO.getValue()){
            throw new AuthorizationException(403, "没有操作权限");
        }
        // 数据库查询绩效实体
        ProjectPerformanceEntity performance = projectPerformanceMapper.getProjectPerformanceEntity(perfId);
        // 查询不到该记录
        if(performance == null){
            throw new BusinessException(404, "查询不到该记录", true);
        }
        // 该绩效不是待评分状态时，分类讨论
        if(performance.getStatus() != PerformanceStatus.Pending.getValue()){
            if(performance.getPMOID() == userId){     // 评价人是本人，符合幂等性
                return true;
            }
            else{                                    // 评价人非本人，需要throw异常
                throw new BusinessException(500, "该绩效不处于待评分状态", true);
            }
        }

        // 计算项目绩效总分
        BigDecimal totalScore = calculateTotalScore(performance.getResource(), performance.getModify(), dto.getScore());
        // 更新主观评分、总分等信息
        // 项目级绩效是池化评定机制，初始PMO不定，因此参数需要多传一个PMO编号
        int row = projectPerformanceMapper.updateSubjectiveScoreInfo
                  (perfId, userId, dto, totalScore, PerformanceStatus.Released.getValue(), LocalDateTime.now());

        if(row == 0){       // 并发状态下可能出现的情况
            throw new BusinessException(500, "该绩效不处于待评分状态", true);
        }

        return true;
    }

    // 查看待评分项目级绩效（项目经理与开发人员不予查看）
    public PageResultVO<ProjectPerformancePendingVO> getPendingPerformances(Integer pageNum, Integer pageSize){
        byte role = UserContext.getCurrentRole().getValue();

        // 项目经理与开发人员不予查看
        if(role == Role.PM.getValue() || role == Role.DEV.getValue()){
            throw new AuthorizationException(403, "没有查看权限");
        }

        // 检查分页参数
        if(pageNum<=0){
            throw new BusinessException(500, "分页参数错误", false);
        }
        // 计算页面查询偏移量
        int offset = (pageNum - 1) * pageSize;

        // 数据库查询待评分绩效记录分页结果与总数
        List<ProjectPerformancePendingVO> result = projectPerformanceMapper.getPendingProjectPerformances
                (role, PerformanceStatus.Pending.getValue(), offset, pageSize);
        long total = projectPerformanceMapper.countPendingProjectPerformances
                (role, PerformanceStatus.Pending.getValue());

        return new PageResultVO<ProjectPerformancePendingVO>(total, result);
    }

    // 查看已发布项目级绩效
    public PageResultVO<ProjectPerformanceReleasedVO> getReleasedPerformances(ProjectPerformanceQueryDTO dto){
        int userId = UserContext.getCurrentUserId();
        byte role = UserContext.getCurrentRole().getValue();

        // 开发人员不予查看
        if(role == Role.DEV.getValue()){
            throw new AuthorizationException(403, "没有查看权限");
        }

        // 检查分页参数
        if(dto.getPageNum()<=0){
            throw new BusinessException(500, "分页参数错误", false);
        }
        // 计算页面查询偏移量
        int offset = (dto.getPageNum() - 1) * dto.getPageSize();

        // 数据库查询已发布绩效分页结果与总数
        List<ProjectPerformanceReleasedVO> result = projectPerformanceMapper.getReleasedProjectPerformances
                (userId, role, PerformanceStatus.Released.getValue(), dto, offset, dto.getPageSize());
        long total = projectPerformanceMapper.countReleasedProjectPerformances
                (userId, role, PerformanceStatus.Released.getValue(), dto);

        return new PageResultVO<ProjectPerformanceReleasedVO>(total, result);
    }

    // 计算绩效总分
    public BigDecimal calculateTotalScore(BigDecimal resource, BigDecimal modify, BigDecimal pmoScore){
        // 总分 = 资源控制率得分 * 0.7 + 主观评分 * 0.3 - 预估工时修改审计扣分
        return BigDecimal.valueOf(resource.doubleValue() * 0.7
                - modify.doubleValue()
                + pmoScore.doubleValue() * 0.3);
    }

    // 根据总预估、实际工时数据，计算资源控制率得分
    private double calculateResource(SumHoursInfoDTO dto){
        // 提取总预估工时、总实际工时
        double sumEstimatedHours = dto.getSumEstimatedHours().doubleValue();
        double sumActualHours = dto.getSumActualHours().doubleValue();

        if(sumEstimatedHours == 0){
            // 预估工时为0，直接得100分
            return 100;
        }else{
            // 根据公式计算
            double resource = 100 * (1 - Math.abs(sumActualHours - sumEstimatedHours) / sumEstimatedHours);
            if(resource < 0){
                // 预估工时不能为负数
                resource = 0;
            }
            return resource;
        }
    }

}
