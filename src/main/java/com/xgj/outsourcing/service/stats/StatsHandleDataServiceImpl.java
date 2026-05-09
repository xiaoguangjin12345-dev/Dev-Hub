package com.xgj.outsourcing.service.stats;

import com.xgj.outsourcing.common.cache.RedisService;
import com.xgj.outsourcing.common.exception.BusinessException;
import com.xgj.outsourcing.enums.common.ProcessStatus;
import com.xgj.outsourcing.enums.user.Role;
import com.xgj.outsourcing.mapper.StatsViewMapper;
import com.xgj.outsourcing.pojo.dto.stats.DevCapabilityMsg;
import com.xgj.outsourcing.pojo.dto.stats.DevEfficiencyMsg;
import com.xgj.outsourcing.pojo.dto.stats.HoursAnalysisMsg;
import com.xgj.outsourcing.pojo.dto.stats.ProjectProgressMsg;
import com.xgj.outsourcing.pojo.vo.stats.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsHandleDataServiceImpl implements StatsHandleDataService {
    private final StatsViewMapper statsViewMapper;
    private final RedisService redisService;

    // 获取项目进度大盘结果
    public boolean handleProjectProgress(ProjectProgressMsg msg){
        // 获取Redis键
        String redisKey = msg.getRedisKey();
        try{
            // 针对不同角色，设置不同的有效时间
            long expireTime = 15 * 60;
            if(msg.getRole() == Role.PM.getValue() || msg.getRole() == Role.DEV.getValue()){
                expireTime = 5 * 60;
            }
            // 数据库获取统计数据
            List<ProjectProgressVO> result = statsViewMapper.getProjectProgressList(msg.getUserId(), msg.getRole(), msg.getProjectIds());

            // 设置结果数据
            redisService.hashSetAll(redisKey, new StatsResultVO<ProjectProgressVO>
                    (ProcessStatus.Success.getValue(), LocalDateTime.now(), result));
            // 设置有效时间
            redisService.expire(redisKey, expireTime);

            return true;

        }catch (Exception e){
            // 设置结果状态失败
            redisService.hashSetAll(redisKey, new StatsResultVO<ProjectProgressVO>(ProcessStatus.Fail.getValue()));
            // 设置较短有效时间
            redisService.expire(redisKey, 60);

            throw new BusinessException(500, "项目进度大盘结果获取失败", true);
        }

    }

    // 获取工时偏差分析结果
    public boolean handleHoursAnalysis(HoursAnalysisMsg msg){
        // 获取Redis键
        String redisKey = msg.getRedisKey();
        try{
            // 针对不同角色，设置不同的有效时间
            long expireTime = 15 * 60;
            if(msg.getRole() == Role.PM.getValue() || msg.getRole() == Role.DEV.getValue()){
                expireTime = 5 * 60;
            }
            // 数据库获取统计数据
            List<HoursAnalysisVO> result = statsViewMapper.getHoursAnalysisList(msg.getUserId(), msg.getRole(), msg.getDimension());

            // 设置结果数据
            redisService.hashSetAll(redisKey, new StatsResultVO<HoursAnalysisVO>
                    (ProcessStatus.Success.getValue(), LocalDateTime.now(), result));
            // 设置有效时间
            redisService.expire(redisKey, expireTime);

            return true;

        }catch (Exception e){
            // 设置结果状态失败
            redisService.hashSetAll(redisKey, new StatsResultVO<HoursAnalysisVO>(ProcessStatus.Fail.getValue()));
            // 设置较短有效时间
            redisService.expire(redisKey, 60);

            throw new BusinessException(500, "工时偏差分析结果获取失败", true);
        }
    }

    // 获取开发人员能力画像结果
    public boolean handleDevCapability(DevCapabilityMsg msg){
        // 获取Redis键
        String redisKey = msg.getRedisKey();
        try{
            // 设置有效时间
            long expireTime = 15 * 60;
            // 数据库获取统计数据
            List<DevCapabilityVO> result = statsViewMapper.getDevCapabilityByDevId(msg.getUserId(), msg.getRole(), msg.getTargetDevId());
            // 设置结果数据
            redisService.hashSetAll(redisKey, new StatsResultVO<DevCapabilityVO>
                    (ProcessStatus.Success.getValue(), LocalDateTime.now(), result));
            // 设置有效时间
            redisService.expire(redisKey, expireTime);

            return true;

        }catch (Exception e){
            // 设置结果状态失败
            redisService.hashSetAll(redisKey, new StatsResultVO<DevCapabilityVO>(ProcessStatus.Fail.getValue()));
            // 设置较短有效时间
            redisService.expire(redisKey, 60);

            throw new BusinessException(500, "开发人员能力画像结果获取失败", true);
        }
    }

    // 获取开发人员效能结果
    public boolean handleDevEfficiency(DevEfficiencyMsg msg){
        // 获取Redis键
        String redisKey = msg.getRedisKey();
        try{
            // 针对不同角色，设置不同的有效时间
            long expireTime = 15 * 60;
            if(msg.getRole() == Role.PM.getValue() || msg.getRole() == Role.DEV.getValue()){
                expireTime = 5 * 60;
            }
            // 数据库获取统计数据
            List<DevEfficiencyVO> result = statsViewMapper.getDevEfficiencyList(msg.getUserId(), msg.getRole());
            // 设置结果数据
            redisService.hashSetAll(redisKey, new StatsResultVO<DevEfficiencyVO>
                    (ProcessStatus.Success.getValue(), LocalDateTime.now(), result));
            // 设置有效时间
            redisService.expire(redisKey, expireTime);

            return true;

        }catch (Exception e){
            // 设置结果状态失败
            redisService.hashSetAll(redisKey, new StatsResultVO<DevEfficiencyVO>(ProcessStatus.Fail.getValue()));
            // 设置较短有效时间
            redisService.expire(redisKey, 60);

            throw new BusinessException(500, "开发人员效能结果获取失败", true);
        }

    }
}
