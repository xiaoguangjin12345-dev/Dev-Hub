package com.xgj.devpulse.service.stats;

import com.alibaba.fastjson2.TypeReference;
import com.xgj.devpulse.common.cache.RedisService;
import com.xgj.devpulse.common.context.UserContext;
import com.xgj.devpulse.common.exception.AuthorizationException;
import com.xgj.devpulse.common.exception.BusinessException;
import com.xgj.devpulse.common.mq.RabbitConfig;
import com.xgj.devpulse.enums.common.ProcessStatus;
import com.xgj.devpulse.enums.user.Role;
import com.xgj.devpulse.pojo.dto.stats.DevCapabilityMsg;
import com.xgj.devpulse.pojo.dto.stats.DevEfficiencyMsg;
import com.xgj.devpulse.pojo.dto.stats.HoursAnalysisMsg;
import com.xgj.devpulse.pojo.dto.stats.ProjectProgressMsg;
import com.xgj.devpulse.pojo.vo.stats.StatsResultVO;
import com.xgj.devpulse.pojo.vo.stats.DevCapabilityVO;
import com.xgj.devpulse.pojo.vo.stats.DevEfficiencyVO;
import com.xgj.devpulse.pojo.vo.stats.HoursAnalysisVO;
import com.xgj.devpulse.pojo.vo.stats.ProjectProgressVO;
import com.xgj.devpulse.service.common.BasicCommonService;
import com.xgj.devpulse.service.common.FileService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final BasicCommonService basicCommonService;
    // RabbitMQ服务
    private final RabbitTemplate rabbit;
    // Redis服务
    private final RedisService redisService;
    // 文件服务
    private final FileService fileService;

    // 项目进度大盘请求
    public String requestProjectProgress(List<Integer> projectIds){
        int userId = UserContext.getCurrentUserId();
        byte role = UserContext.getCurrentRole().getValue();

        // 构造消息体
        String redisKey = this.getProjectProgressKey(userId, role);
        ProjectProgressMsg msg = new ProjectProgressMsg(userId, role, projectIds, redisKey);

        // 将消息发送到队列
        rabbit.convertAndSend(RabbitConfig.stats_data, msg);
        // 设置Redis记录状态
        redisService.delete(redisKey);    // 先删除原始记录
        StatsResultVO<ProjectProgressVO> resultVO = new StatsResultVO<ProjectProgressVO>(ProcessStatus.Pending.getValue());
        redisService.hashSetAll(redisKey, resultVO);

        return redisKey;
    }

    // 项目进度大盘结果查询
    public StatsResultVO<ProjectProgressVO> projectProgressResponse(String redisKey){
        // Redis获取数据
        StatsResultVO<ProjectProgressVO> result = redisService.hashGetAll(redisKey, new TypeReference<StatsResultVO<ProjectProgressVO>>() {});
        // 数据过期
        if(result == null){
            throw new BusinessException(404, "数据已过期，请重新刷新获取", false);
        }
        // 失败
        if(result.getStatus() == ProcessStatus.Fail.getValue()){
            throw new BusinessException(500, "项目大盘数据获取失败，请重新刷新获取", true);
        }
        return result;
    }

    // 工时偏差分析（预估工时与实际工时）请求
    public String requestHoursAnalysis(byte dimensionNum){
        int userId = UserContext.getCurrentUserId();
        byte role = UserContext.getCurrentRole().getValue();

        String dimension;
        switch(dimensionNum){
            case 1:
                dimension = "project";
                break;
            case 2:
                dimension = "user";
                break;
            case 3:
                dimension = "tag";
                break;
            default:
                throw new BusinessException(404, "参数异常", true);
        }

        // 构造消息体
        String redisKey = this.getHoursAnalysisKey(userId, role, dimension);
        HoursAnalysisMsg msg = new HoursAnalysisMsg(userId, role, dimension, redisKey);

        // 将消息发送到队列
        rabbit.convertAndSend(RabbitConfig.stats_data, msg);
        // 设置Redis记录状态
        redisService.delete(redisKey);    // 先删除原始记录
        StatsResultVO<HoursAnalysisVO> resultVO = new StatsResultVO<HoursAnalysisVO>(ProcessStatus.Pending.getValue());
        redisService.hashSetAll(redisKey, resultVO);

        return redisKey;
    }

    // 工时偏差分析结果查询
    public StatsResultVO<HoursAnalysisVO> hoursAnalysisResponse(String redisKey){
        // Redis获取数据
        StatsResultVO<HoursAnalysisVO> result = redisService.hashGetAll(redisKey, new TypeReference<StatsResultVO<HoursAnalysisVO>>() {});
        // 数据过期
        if(result == null){
            throw new BusinessException(404, "数据已过期，请重新刷新获取", false);
        }
        // 失败
        if(result.getStatus() == ProcessStatus.Fail.getValue()){
            throw new BusinessException(500, "工时偏差分析数据获取失败，请重新刷新获取", true);
        }
        return result;
    }

    // 开发人员能力画像（按照开发人员参与过的任务的技能标签聚合分析）请求
    public String requestDevCapability(int targetDevId){
        int userId = UserContext.getCurrentUserId();
        byte role = UserContext.getCurrentRole().getValue();
        // 开发人员越权访问非本人的数据
        if(role == Role.DEV.getValue() && userId != targetDevId){
            throw new AuthorizationException(403, "没有访问权限");
        }

        // 构造消息体
        String redisKey = this.getDevCapabilityKey(targetDevId);
        DevCapabilityMsg msg = new DevCapabilityMsg(userId, role, targetDevId, redisKey);

        // 将消息发送到队列
        rabbit.convertAndSend(RabbitConfig.stats_data, msg);
        // 设置Redis记录状态
        redisService.delete(redisKey);    // 先删除原始记录
        StatsResultVO<DevCapabilityVO> resultVO = new StatsResultVO<DevCapabilityVO>(ProcessStatus.Pending.getValue());
        redisService.hashSetAll(redisKey, resultVO);

        return redisKey;
    }

    // 开发人员能力画像结果查询
    public StatsResultVO<DevCapabilityVO> devCapabilityResponse(String redisKey){
        Integer userId = UserContext.getCurrentUserId();
        Byte role = UserContext.getCurrentRole().getValue();

        // Redis获取数据
        StatsResultVO<DevCapabilityVO> result = redisService.hashGetAll(redisKey, new TypeReference<StatsResultVO<DevCapabilityVO>>() {});
        // 数据过期
        if(result == null){
            throw new BusinessException(404, "数据已过期，请重新刷新获取", false);
        }
        // 失败
        if(result.getStatus() == ProcessStatus.Fail.getValue()){
            throw new BusinessException(500, "开发人员能力画像数据获取失败，请重新刷新获取", true);
        }
        // 开发人员越权访问非本人的数据
        if(result.getStatus() == ProcessStatus.Success.getValue() &&
                role == Role.DEV.getValue() && !userId.equals(result.getTargetDevId())){
            throw new AuthorizationException(403, "没有访问权限");
        }

        return result;
    }

    // 开发人员效能（任务完成数量、绩效平均分、总工时）请求
    public String requestDevEfficiency(){
        int userId = UserContext.getCurrentUserId();
        byte role = UserContext.getCurrentRole().getValue();

        // 构造消息体
        String redisKey = this.getDevEfficiencyKey(userId, role);
        DevEfficiencyMsg msg = new DevEfficiencyMsg(userId, role, redisKey);

        // 将消息发送到队列
        rabbit.convertAndSend(RabbitConfig.stats_data, msg);
        // 设置Redis记录状态
        redisService.delete(redisKey);    // 先删除原始记录
        StatsResultVO<DevEfficiencyVO> resultVO = new StatsResultVO<DevEfficiencyVO>(ProcessStatus.Pending.getValue());
        redisService.hashSetAll(redisKey, resultVO);

        return redisKey;
    }

    // 开发人员效能结果查询
    public StatsResultVO<DevEfficiencyVO> devEfficiencyResponse(String redisKey){
        // Redis获取数据
        StatsResultVO<DevEfficiencyVO> result = redisService.hashGetAll(redisKey, new TypeReference<StatsResultVO<DevEfficiencyVO>>() {});
        // 数据过期
        if(result == null){
            throw new BusinessException(404, "数据已过期，请重新刷新获取", false);
        }
        // 失败
        if(result.getStatus() == ProcessStatus.Fail.getValue()){
            throw new BusinessException(500, "开发人员效能数据获取失败，请重新刷新获取", true);
        }
        return result;
    }


    // 项目进度大盘Excel导出
    public void getProjectProgressExcel(HttpServletResponse response){
        int userId = UserContext.getCurrentUserId();
        byte role = UserContext.getCurrentRole().getValue();

        // 构造Redis键
        String redisKey = this.getProjectProgressKey(userId, role);
        // Redis获取数据
        StatsResultVO<ProjectProgressVO> result = this.projectProgressResponse(redisKey);
        // 数据未生成完毕时，提示信息
        if(result.getStatus() == ProcessStatus.Pending.getValue()){
            throw new BusinessException(404, "数据正在导出中，请稍后再试", false);
        }

        // 格式化时间戳
        String timestamp = result.getExportTime().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        // Excel文件导出
        fileService.getExportExcel(response, "项目进度大盘_" + timestamp, result.getData(), ProjectProgressVO.class);
    }

    // 工时偏差分析Excel导出
    public void getHoursAnalysisExcel(byte dimensionNum, HttpServletResponse response){
        int userId = UserContext.getCurrentUserId();
        byte role = UserContext.getCurrentRole().getValue();

        String dimension;
        switch(dimensionNum){
            case 1:
                dimension = "project";
                break;
            case 2:
                dimension = "user";
                break;
            case 3:
                dimension = "tag";
                break;
            default:
                throw new BusinessException(404, "参数异常", true);
        }

        // 构造Redis键
        String redisKey = this.getHoursAnalysisKey(userId, role, dimension);
        // Redis获取数据
        StatsResultVO<HoursAnalysisVO> result = this.hoursAnalysisResponse(redisKey);
        // 数据未生成完毕时，提示信息
        if(result.getStatus() == ProcessStatus.Pending.getValue()){
            throw new BusinessException(404, "数据正在导出中，请稍后再试", false);
        }

        // 获取自定列名
        List<List<String>> head = this.getExportHeadForHoursAnalysis(dimensionNum);
        // 格式化时间戳
        String timestamp = result.getExportTime().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        // Excel文件导出
        fileService.getExportExcel(response, String.format("工时偏差分析_按%s维度_%s", head.get(1).get(0), timestamp),
                                   result.getData(), HoursAnalysisVO.class, head);

    }

    // 开发人员能力画像Excel导出
    public void getDevCapabilityExcel(Integer targetDevId, HttpServletResponse response){
        // 构造Redis键
        String redisKey = this.getDevCapabilityKey(targetDevId);
        // Redis获取数据
        StatsResultVO<DevCapabilityVO> result = this.devCapabilityResponse(redisKey);
        // 数据未生成完毕时，提示信息
        if(result.getStatus() == ProcessStatus.Pending.getValue()){
            throw new BusinessException(404, "数据正在导出中，请稍后再试", false);
        }

        // 获取目标开发人员姓名
        String devName = basicCommonService.getNameById(targetDevId);
        // 格式化时间戳
        String timestamp = result.getExportTime().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        // Excel文件导出
        fileService.getExportExcel(response, String.format("开发人员能力画像_%s_%s", devName, timestamp),
                                   result.getData(), DevCapabilityVO.class);
    }

    // 开发人员效能Excel导出
    public void getDevEfficiencyExcel(HttpServletResponse response){
        int userId = UserContext.getCurrentUserId();
        byte role = UserContext.getCurrentRole().getValue();

        // 构造Redis键
        String redisKey = this.getDevEfficiencyKey(userId, role);
        // Redis获取数据
        StatsResultVO<DevEfficiencyVO> result = this.devEfficiencyResponse(redisKey);
        // 数据未生成完毕时，提示信息
        if(result.getStatus() == ProcessStatus.Pending.getValue()){
            throw new BusinessException(404, "数据正在导出中，请稍后再试", false);
        }

        // 格式化时间戳
        String timestamp = result.getExportTime().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        // Excel文件导出
        fileService.getExportExcel(response, "开发人员效能分析_" + timestamp, result.getData(), DevEfficiencyVO.class);
    }

    // 获取工时偏差分析的专用列名
    private List<List<String>> getExportHeadForHoursAnalysis(byte dimensionNum){
        int userId = UserContext.getCurrentUserId();
        byte role = UserContext.getCurrentRole().getValue();

        List<List<String>> head = new ArrayList<>();
        // 根据维度选择，确定第1列的列名
        switch(dimensionNum){
            case 1:
                head.add(Collections.singletonList("项目"));
                break;
            case 2:
                head.add(Collections.singletonList("开发人员"));
                break;
            case 3:
                head.add(Collections.singletonList("技能标签"));
                break;
            default:
                throw new BusinessException(404, "参数异常", true);
        }
        head.add(Collections.singletonList("预估工时总计"));
        head.add(Collections.singletonList("实际工时总计"));
        head.add(Collections.singletonList("绝对偏差值"));
        head.add(Collections.singletonList("相对偏差比例"));

        return head;
    }


    // 获取项目进度大盘的key
    private String getProjectProgressKey(Integer userId, Byte role){
        String redisKey = "stats:project-progress:";
        // 针对不同角色，设置不同的键
        if(role == Role.PMO.getValue() || role == Role.ADMIN.getValue()){
            redisKey += "role:pmo-admin";
        }else{
            redisKey += "role:pm-dev:userid:" + userId;
        }
        return redisKey;
    }

    // 获取工时偏差分析的key
    private String getHoursAnalysisKey(Integer userId, Byte role, String dimension){
        String redisKey = "stats:hours-analysis:dimension:" + dimension;
        // 针对不同角色，设置不同的键
        if(role == Role.PMO.getValue() || role == Role.ADMIN.getValue()){
            redisKey += ":role:pmo-admin";
        }else{
            redisKey += ":role:pm-dev:userid:" + userId;
        }
        return redisKey;
    }

    // 获取开发人员能力画像的key
    private String getDevCapabilityKey(Integer targetDevId){
        String redisKey = "stats:dev-capability:target-dev-id:" + targetDevId;
        return redisKey;
    }

    // 获取开发人员效能的key
    private String getDevEfficiencyKey(Integer userId, Byte role){
        String redisKey = "stats:dev-efficiency:";
        // 针对不同角色，设置不同的键
        if(role == Role.PMO.getValue() || role == Role.ADMIN.getValue()){
            redisKey += "role:pmo-admin";
        }else{
            redisKey += "role:pm-dev:userid:" + userId;
        }
        return redisKey;
    }

}
