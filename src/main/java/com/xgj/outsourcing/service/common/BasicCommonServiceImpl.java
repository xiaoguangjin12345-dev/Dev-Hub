package com.xgj.outsourcing.service.common;

import com.xgj.outsourcing.common.cache.RedisService;
import com.xgj.outsourcing.common.exception.BusinessException;
import com.xgj.outsourcing.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicCommonServiceImpl implements BasicCommonService {
    private final RedisService redisService;
    private final UserMapper userMapper;

    // 多模块通用方法：根据用户编号，获取真实姓名
    public String getNameById(Integer userId){
        // 构造Redis键
        String redisKey = "user:name:" + userId;
        // Redis查询
        String result = redisService.get(redisKey, String.class);
        // Redis命中
        if(result != null){
            redisService.expire(redisKey, redisService.getRandomTTL(6*60*60, 9*60*60));   // 重设有效时间
            return result;     // 直接返回结果
        }
        // 数据库查询
        result = userMapper.getNameById(userId);
        // 有结果
        if(result != null){
            // 设置Redis键值
            redisService.set(redisKey, result, redisService.getRandomTTL(6*60*60, 9*60*60));
            return result;
        }else{
            throw new BusinessException(404, "查询不到该用户");
        }
    }

}
