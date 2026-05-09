package com.xgj.outsourcing.service.common;

import com.alibaba.fastjson2.TypeReference;
import com.xgj.outsourcing.common.cache.RedisService;
import com.xgj.outsourcing.common.context.UserContext;
import com.xgj.outsourcing.common.exception.BusinessException;
import com.xgj.outsourcing.enums.user.Role;
import com.xgj.outsourcing.mapper.DictTagMapper;
import com.xgj.outsourcing.mapper.ProjectMapper;
import com.xgj.outsourcing.mapper.TaskMapper;
import com.xgj.outsourcing.mapper.UserMapper;
import com.xgj.outsourcing.pojo.vo.common.SelectOptionVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SelectOptionServiceImpl implements SelectOptionService {
    private final RedisService redisService;
    private final DictTagMapper dictTagMapper;
    private final ProjectMapper projectMapper;
    private final UserMapper userMapper;
    private final TaskMapper taskMapper;

    // 获取下拉框选项
    public List<SelectOptionVO<Integer>> getSelectOptions(String type){
        Integer userId = UserContext.getCurrentUserId();
        Byte role = UserContext.getCurrentRole().getValue();

        // 构造Redis键
        String redisKey = this.getRedisKey(type, userId, role);

        // Redis取值
        List<SelectOptionVO<Integer>> list = redisService.get(redisKey, new TypeReference<List<SelectOptionVO<Integer>>>(){});
        // Redis命中
        if(list != null){
            return list;
        }

        list = new ArrayList<>();
        long expireTime = redisService.getRandomTTL(12 * 60 * 60, 15 * 60 * 60);    // 随机偏移，防止大量key同时失效

        switch (type){
            // 用户角色
            case "user-role":
                list.add(new SelectOptionVO<Integer>(1, "PMO"));
                list.add(new SelectOptionVO<Integer>(2, "项目经理"));
                list.add(new SelectOptionVO<Integer>(3, "开发人员"));
                // 非系统管理员角色，不对外显示
                if(role == Role.ADMIN.getValue()){
                    list.add(new SelectOptionVO<Integer>(4, "系统管理员"));
                }
                break;

            // 用户状态
            case "user-status":
                list.add(new SelectOptionVO<Integer>(1, "待验证"));
                list.add(new SelectOptionVO<Integer>(2, "已验证"));
                list.add(new SelectOptionVO<Integer>(3, "未通过"));
                break;

            // 项目状态
            case "project-status":
                list.add(new SelectOptionVO<Integer>(1, "待审核"));
                list.add(new SelectOptionVO<Integer>(2, "待修改"));
                list.add(new SelectOptionVO<Integer>(3, "进行中"));
                list.add(new SelectOptionVO<Integer>(4, "待结项"));
                list.add(new SelectOptionVO<Integer>(5, "已归档"));
                break;

            // 项目审批类型
            case "project-approve-type":
                list.add(new SelectOptionVO<Integer>(1, "立项审批"));
                list.add(new SelectOptionVO<Integer>(2, "结项审批"));
                break;

            // 任务状态
            case "task-status":
                list.add(new SelectOptionVO<Integer>(1, "待分配"));
                list.add(new SelectOptionVO<Integer>(2, "进行中"));
                list.add(new SelectOptionVO<Integer>(3, "待验收"));
                list.add(new SelectOptionVO<Integer>(4, "已完成"));
                break;

            // 任务申请类型
            case "task-app-type":
                list.add(new SelectOptionVO<Integer>(1, "项目经理邀请"));
                list.add(new SelectOptionVO<Integer>(2, "开发人员申请"));
                break;

            // 任务申请状态
            case "task-app-status":
                list.add(new SelectOptionVO<Integer>(1, "待处理"));
                list.add(new SelectOptionVO<Integer>(2, "已同意"));
                list.add(new SelectOptionVO<Integer>(3, "已失效"));
                break;

            // 实际工时记录状态
            case "log-status":
                list.add(new SelectOptionVO<Integer>(1, "可修改"));
                list.add(new SelectOptionVO<Integer>(2, "只读"));
                break;

            // 任务评审结果
            case "review-result":
                list.add(new SelectOptionVO<Integer>(1, "待评审"));
                list.add(new SelectOptionVO<Integer>(2, "通过"));
                list.add(new SelectOptionVO<Integer>(3, "返工"));
                break;

            // 绩效状态（含任务级、项目级）
            case "pref-status":
                list.add(new SelectOptionVO<Integer>(1, "未发布"));
                list.add(new SelectOptionVO<Integer>(2, "已发布"));
                break;

            // 消息通知类型
            case "notice-type":
                list.add(new SelectOptionVO<Integer>(1, "系统通知"));
                list.add(new SelectOptionVO<Integer>(2, "审核通知"));
                list.add(new SelectOptionVO<Integer>(3, "申请通知"));
                list.add(new SelectOptionVO<Integer>(4, "工时预警"));
                list.add(new SelectOptionVO<Integer>(5, "验收通知"));
                list.add(new SelectOptionVO<Integer>(6, "其他"));
                break;

            // 消息通知状态
            case "notice-status":
                list.add(new SelectOptionVO<Integer>(1, "未读"));
                list.add(new SelectOptionVO<Integer>(2, "已读"));
                // 非系统管理员角色，不对外显示
                if(role == Role.ADMIN.getValue()){
                    list.add(new SelectOptionVO<Integer>(3, "已删除"));
                }
                break;

            // 技能标签
            case "tags":
                // 数据库获取数据并转换成SelectOption列表
                list = dictTagMapper.getTagSelectOptions();
                break;

            // 项目经理列表
            case "pms":
                list = userMapper.getPMSelectOptions(userId, role);
                expireTime = 5 * 60;
                break;

            // 开发人员列表
            case "devs":
                list = userMapper.getDevSelectOptions(userId, role);
                expireTime = 5 * 60;
                break;

            // 项目列表
            case "projects":
                list = projectMapper.getProjectSelectOptions(userId, role);
                expireTime = 5 * 60;
                break;

            // 任务列表（开发人员填报工时用，并为其他角色准备数据隔离版本）
            case "tasks":
                list = taskMapper.getTaskSelectOptions(userId, role);
                expireTime = 5 * 60;
                break;

            // 统计分析——工时偏差分析维度列表
            case "stats-hour-dimensions":
                list.add(new SelectOptionVO<Integer>(1, "按项目"));
                list.add(new SelectOptionVO<Integer>(2, "按开发人员"));
                list.add(new SelectOptionVO<Integer>(3, "按技术标签"));
                expireTime = 60 * 60;
                break;

            default:
                throw new BusinessException(404, "没有该类别的选项框数据", true);

        }

        // 如果列表为空，设置极短有效时间
        if(list == null){
            expireTime = Math.min(expireTime, 60);
        }

        // 存储键值（方法内部自推断类型）
        redisService.set(redisKey, list, expireTime);

        return list;
    }

    // 根据下拉框类型构造Redis键
    private String getRedisKey(String type, Integer userId, Byte role) {
        // 构造Redis键
        String redisKey = "select-option:" + type;

        // 部分业务的下拉框，由于权限，需要加字段
        switch (type){
            case "user-role":        // 用户角色
            case "notice-status":    // 消息通知状态
                if(role == Role.ADMIN.getValue()){
                    redisKey += ":admin";
                }else{
                    redisKey += ":no-admin";
                }
                break;

            case "pms":              // 项目经理列表
            case "devs":             // 开发人员列表
            case "projects":         // 项目列表
            case "tasks":            // 任务列表
                // PMO与系统管理员看全量，存一份即可
                if(role == Role.ADMIN.getValue() || role == Role.PMO.getValue()){
                    redisKey += ":role:admin-pmo";
                }
                else{
                    redisKey += ":role:pm-dev:userid:" + userId;
                }
                break;

            default:
                break;
        }
        return redisKey;
    }

}
