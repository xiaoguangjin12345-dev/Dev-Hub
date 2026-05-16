package com.xgj.devpulse.service.user;

import com.xgj.devpulse.common.cache.RedisService;
import com.xgj.devpulse.common.context.UserContext;
import com.xgj.devpulse.common.exception.AuthenticationException;
import com.xgj.devpulse.common.exception.BusinessException;
import com.xgj.devpulse.enums.user.Role;
import com.xgj.devpulse.enums.tag.TagType;
import com.xgj.devpulse.enums.user.UserStatus;
import com.xgj.devpulse.mapper.*;
import com.xgj.devpulse.pojo.dto.notice.NoticeApproveDTO;
import com.xgj.devpulse.pojo.dto.user.UserProfileUpdateDTO;
import com.xgj.devpulse.pojo.dto.user.UserQueryDTO;
import com.xgj.devpulse.pojo.entity.UserEntity;
import com.xgj.devpulse.pojo.vo.common.PageResultVO;
import com.xgj.devpulse.pojo.vo.user.UserDetailsVO;
import com.xgj.devpulse.pojo.vo.user.UserListVO;
import com.xgj.devpulse.service.common.DictTagService;
import com.xgj.devpulse.service.common.NoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final DictTagService dictTagService;
    private final DevProfileMapper devProfileMapper;
    private final NoticeService noticeService;
    // Redis服务
    private final RedisService redisService;

    // 参数化查询用户列表
    public PageResultVO<UserListVO> getUsers(UserQueryDTO dto){
        // 检查分页参数
        if(dto.getPageNum()<=0){
            throw new BusinessException(500, "分页参数错误", false);
        }
        // 计算页面查询偏移量
        int offset = (dto.getPageNum() - 1) * dto.getPageSize();

        // 数据库查询分页结果与总数
        List<UserListVO> result = userMapper.getUsersByQuery(dto, offset, dto.getPageSize());
        long total = userMapper.countUsersByQuery(dto);

        return new PageResultVO<UserListVO>(total, result);
    }

    // 查询指定用户详情
    public UserDetailsVO getUserDetails(int userId){
        // 设置Redis查询键
        String redisKey = "user:details:" + userId;
        // Redis查询
        UserDetailsVO result = redisService.get(redisKey, UserDetailsVO.class);
        // Redis命中
        if(result != null && result.getUserId() == userId){
            redisService.expire(redisKey, 15 * 60);     // 重置有效时间
            return result;                                       // 直接返回结果
        }

        // Redis不命中，数据库查询
        result = userMapper.getUserById(userId);
        // 若查找不到，直接抛404异常
        if(result == null){
            throw new BusinessException(404, "未找到该用户");
        }
        // 设置Redis键值
        redisService.set(redisKey, result, 15 * 60);

        return result;
    }

    // 用户修改非关键信息
    @Transactional   // 针对开发人员，需要同步更新 开发人员简历表，故需要事务
    public boolean updateUserProfile(UserProfileUpdateDTO dto){
        int userId = UserContext.getCurrentUserId();
        byte role = UserContext.getCurrentRole().getValue();

        // 数据库更新用户记录
        userMapper.updateUser(userId, dto.getEmail(), dto.getPhone());

        if(role == Role.DEV.getValue()){     // 为开发人员修改简历信息
            // 设置标签关联信息
            dictTagService.setDictTag(TagType.DEV.getValue(), userId, dto.getSkills());
            // 获取标签名称字符串
            String skills = dictTagService.getDictTagName(TagType.DEV.getValue(), userId);
            try{
                devProfileMapper.updateDevProfile(userId, skills, dto.getResumeText());
            }catch(Exception e){
                throw new BusinessException(500, "用户信息修改失败", false);
            }
        }
        // 使该用户详情的Redis记录失效（异步）
        redisService.deleteUserDetailsKey(userId);

        return true;
    }

    // 管理员审核用户
    public boolean auditUser(int targetUserId, NoticeApproveDTO dto){
        int currUserId = UserContext.getCurrentUserId();
        byte currRole = UserContext.getCurrentRole().getValue();

        // 非管理员不予操作
        if(currRole != Role.ADMIN.getValue()){
            throw new AuthenticationException(403, "越权操作");
        }
        // 根据用户编号，获取用户实体
        UserEntity user = userMapper.getUserEntity(targetUserId);
        // 查询不到该用户 或 用户状态不是 待审核 状态，不予操作
        if(user == null || user.getStatus() != UserStatus.PENDING.getValue()){
            throw new BusinessException(404, "用户不存在或状态不合法");
        }

        // 根据审核结果赋枚举值
        UserStatus status = dto.isResult()? UserStatus.VERIFIED: UserStatus.REJECTED;
        // 数据库更改用户审核状态
        int row = userMapper.updateAuditResult(targetUserId, status.getValue());
        if(row == 0){     // 并发状态下可能出现
            throw new BusinessException(500, "该用户已被审核", false);
        }

        // 创建消息通知，提醒目标用户（异步）
        noticeService.createUserAuditNotice(targetUserId, currUserId, dto);

        // 若审核通过，删除项目经理或开发人员下拉框的Redis键（异步）
        if(dto.isResult()){
            redisService.deleteUserSelectOptionKey(user.getRole());
        }

        return true;
    }

}
