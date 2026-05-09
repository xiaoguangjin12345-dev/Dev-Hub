package com.xgj.outsourcing.service.performance;

import com.xgj.outsourcing.common.context.UserContext;
import com.xgj.outsourcing.common.exception.AuthorizationException;
import com.xgj.outsourcing.common.exception.BusinessException;
import com.xgj.outsourcing.enums.performance.PerformanceStatus;
import com.xgj.outsourcing.enums.user.Role;
import com.xgj.outsourcing.mapper.TaskMapper;
import com.xgj.outsourcing.mapper.TaskPerformanceMapper;
import com.xgj.outsourcing.pojo.dto.performance.PerformanceSubjectiveScoreDTO;
import com.xgj.outsourcing.pojo.dto.performance.TaskPerformanceQueryDTO;
import com.xgj.outsourcing.pojo.entity.TaskEntity;
import com.xgj.outsourcing.pojo.entity.TaskPerformanceEntity;
import com.xgj.outsourcing.pojo.vo.common.PageResultVO;
import com.xgj.outsourcing.pojo.vo.performance.TaskPerformancePendingVO;
import com.xgj.outsourcing.pojo.vo.performance.TaskPerformanceReleasedVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskPerformanceService implements
        PerformanceService<TaskPerformancePendingVO, TaskPerformanceReleasedVO, TaskPerformanceQueryDTO> {
    private final TaskPerformanceMapper taskPerformanceMapper;
    private final TaskMapper taskMapper;

    // 生成任务级绩效记录
    public boolean createPerformance(int taskId){
        // 数据库查询 任务实体 与 项目经理编号
        TaskEntity task = taskMapper.getTaskEntity(taskId);
        Integer pmId = taskMapper.getPmIdByTaskId(taskId);
        // 查询不到该任务 或 项目经理
        if(task == null || pmId == null){
            throw new BusinessException(404, "查询不到该记录", true);
        }
        // 计算 质量分Q
        double quality = 100 - (task.getRevision() - 1) * 10;
        if (quality < 60){
            quality = 60;
        }
        // 计算 工时效率分E
        double efficiency = 100 * (double)task.getEstimatedHours() / (double)task.getActualHours();
        if(efficiency > 100){
            efficiency = 100;
        }
        // 构造绩效实体
        TaskPerformanceEntity performance= new TaskPerformanceEntity();
        // 填写关键信息
        performance.setTaskID(taskId);
        performance.setPMID(pmId);
        performance.setDevID(task.getDevID());
        // 填写客观得分信息
        performance.setQuality(BigDecimal.valueOf(quality));
        performance.setEfficiency(BigDecimal.valueOf(efficiency));
        // 填写初始值与初始状态
        performance.setPMScore(BigDecimal.valueOf(0));
        performance.setTotalScore(BigDecimal.valueOf(0));
        performance.setStatus(PerformanceStatus.Pending.getValue());

        // 数据库添加待评分绩效记录
        taskPerformanceMapper.insertTaskPerformance(performance);

        return true;
    }

    // 提交主观评分并结算
    public boolean updatePerformanceScore(int perfId, PerformanceSubjectiveScoreDTO dto){
        int userId = UserContext.getCurrentUserId();
        byte role = UserContext.getCurrentRole().getValue();

        // 非项目经理，不予操作
        if(role != Role.PM.getValue()){
            throw new AuthorizationException(403, "没有操作权限");
        }
        // 数据库查询绩效实体
        TaskPerformanceEntity performance = taskPerformanceMapper.getTaskPerformanceEntity(perfId);
        // 查询不到该记录
        if(performance == null){
            throw new BusinessException(404, "查询不到该记录", true);
        }
        // 该绩效的评价人非当前用户，不予操作
        if(performance.getPMID() != userId){
            throw new AuthorizationException(403, "没有操作权限");
        }
        // 该绩效不是待评分状态，但是任务级绩效的评价人是固定的，符合幂等性原则
        if(performance.getStatus() != PerformanceStatus.Pending.getValue()){
            return true;
        }

        // 计算任务绩效总分
        BigDecimal totalScore = calculateTotalScore(performance.getQuality(), performance.getEfficiency(), dto.getScore());
        // 更新主观评分、总分等信息
        taskPerformanceMapper.updateSubjectiveScoreInfo
                (perfId, dto, totalScore, PerformanceStatus.Released.getValue(), LocalDateTime.now());

        return true;
    }

    // 查看待评分任务级绩效
    public PageResultVO<TaskPerformancePendingVO> getPendingPerformances(Integer pageNum, Integer pageSize){
        int userId = UserContext.getCurrentUserId();
        byte role = UserContext.getCurrentRole().getValue();

        // 开发人员不予查看
        if(role == Role.DEV.getValue()){
            throw new AuthorizationException(403, "没有查看权限");
        }

        // 检查分页参数
        if(pageNum<=0){
            throw new BusinessException(500, "分页参数错误", false);
        }
        // 计算页面查询偏移量
        int offset = (pageNum - 1) * pageSize;

        // 数据库查询待评分绩效记录分页结果与总数
        List<TaskPerformancePendingVO> result = taskPerformanceMapper.getPendingTaskPerformances
                        (userId, role, PerformanceStatus.Pending.getValue(), offset, pageSize);
        long total = taskPerformanceMapper.countPendingTaskPerformances
                (userId, role, PerformanceStatus.Pending.getValue());

        return new PageResultVO<TaskPerformancePendingVO>(total, result);
    }

    // 查看已发布任务级绩效
    public PageResultVO<TaskPerformanceReleasedVO> getReleasedPerformances(TaskPerformanceQueryDTO dto){
        int userId = UserContext.getCurrentUserId();
        byte role = UserContext.getCurrentRole().getValue();

        // 检查分页参数
        if(dto.getPageNum()<=0){
            throw new BusinessException(500, "分页参数错误", false);
        }
        // 计算页面查询偏移量
        int offset = (dto.getPageNum() - 1) * dto.getPageSize();

        // 数据库查询已发布绩效分页结果与总数
        List<TaskPerformanceReleasedVO> result = taskPerformanceMapper.getReleasedTaskPerformances
                (userId, role, PerformanceStatus.Released.getValue(), dto, offset, dto.getPageSize());
        long total = taskPerformanceMapper.countReleasedTaskPerformances
                (userId, role, PerformanceStatus.Released.getValue(), dto);

        return new PageResultVO<TaskPerformanceReleasedVO>(total, result);
    }

    // 计算绩效总分
    public BigDecimal calculateTotalScore(BigDecimal quality, BigDecimal efficiency, BigDecimal pmScore){
        // 总分 = 质量分Q * 0.5 + 工时效率分E * 0.2 + 主观评分 * 0.3
        return BigDecimal.valueOf(quality.doubleValue() * 0.5 +
                efficiency.doubleValue() * 0.2 +
                pmScore.doubleValue() * 0.3);

    }
}
