package com.xgj.devpulse.common.cache;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import com.xgj.devpulse.enums.user.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService {
    private final StringRedisTemplate redis;

    // ------------------------ 单键值操作 -------------------------

    // 存键值（设置有效时间，单位为秒）
    public <T> void set(String redisKey, T value, long expireSeconds) {
        // 对象序列化
        String redisValue = JSON.toJSONString(value, "yyyy-MM-dd HH:mm:ss");
        // 保存（并设置有效时间）
        redis.opsForValue().set(redisKey, redisValue, expireSeconds, TimeUnit.SECONDS);
    }

    // 存键值（永久有效）
    public <T> void set(String redisKey, T value) {
        // 对象序列化
        String redisValue = JSON.toJSONString(value, "yyyy-MM-dd HH:mm:ss");
        // 保存
        redis.opsForValue().set(redisKey, redisValue);
    }

    // 取值（简单类型重载）
    public <T> T get(String redisKey, Class<T> clazz) {
        // 取值
        String redisValue = redis.opsForValue().get(redisKey);
        // 若为空，返回空
        if(redisValue == null){
            return null;
        }
        // 转为原JSON对象返回
        return JSON.parseObject(redisValue, clazz);
    }

    // 取值（复杂泛型重载）
    public <T> T get(String redisKey, TypeReference<T> clazz) {
        // 取值
        String redisValue = redis.opsForValue().get(redisKey);
        // 若为空，返回空
        if(redisValue == null){
            return null;
        }
        // 转为原JSON对象返回
        return JSON.parseObject(redisValue, clazz);
    }


    // ------------------------ Hash单键值操作 -------------------------
    // 注意，针对同一类对象在同一线的操作，不能与Hash对象整体操作HashSetAll、HashGetAll混用

    // 存hash键值
    public <T> void hashSet(String redisKey, String field, T value) {
        // 对象序列化
        String redisValue = JSON.toJSONString(value, "yyyy-MM-dd HH:mm:ss");
        // 保存
        redis.opsForHash().put(redisKey, field, redisValue);
    }

    // 取hash值（简单类型重载）
    public <T> T hashGet(String redisKey, String field, Class<T> clazz) {
        // 取值
        Object redisValue = redis.opsForHash().get(redisKey, field);
        // 若为空，返回空
        if(redisValue == null){
            return null;
        }
        // 转为原JSON对象返回
        return JSON.parseObject(redisValue.toString(), clazz);
    }

    // 取hash值（复杂泛型重载）
    public <T> T hashGet(String redisKey, String field, TypeReference<T> clazz) {
        // 取值
        Object redisValue = redis.opsForHash().get(redisKey, field);
        // 若为空，返回空
        if(redisValue == null){
            return null;
        }
        // 转为原JSON对象返回
        return JSON.parseObject(redisValue.toString(), clazz);
    }


    // ------------------------ Hash对象整体操作 -------------------------
    // 注意，针对同一类对象在同一线的操作，不能与Hash单键值操作HashSet、HashGet混用

    // 存整体hash对象
    public <T> void hashSetAll(String redisKey, T object) {
        // 对象序列化
        String json = JSON.toJSONString(object, "yyyy-MM-dd HH:mm:ss");
        // 将字符串解析为Map<String, String>
        Map<String, String> map = JSON.parseObject(json, new TypeReference<Map<String, String>>() {});
        // 保存
        redis.opsForHash().putAll(redisKey, map);
    }

    // 取整个hash对象（简单类型重载）
    public <T> T hashGetAll(String redisKey, Class<T> clazz) {
        // 获取整个Hash对象
        Map<Object, Object> object = redis.opsForHash().entries(redisKey);
        // 判空
        if (object == null || object.isEmpty()){
            return null;
        }

        JSONObject json = new JSONObject();
        // 将原Hash的键与字符串序列值，填入新的json对象中
        for (Map.Entry<Object, Object> entry : object.entrySet()) {
            json.put(entry.getKey().toString(), entry.getValue());
        }
        // 将字符串序列值转成对象
        return json.toJavaObject(clazz);
    }

    // 取整个hash对象（复杂泛型重载）
    public <T> T hashGetAll(String redisKey, TypeReference<T> clazz) {
        // 获取整个Hash对象
        Map<Object, Object> object = redis.opsForHash().entries(redisKey);
        // 判空
        if (object == null || object.isEmpty()){
            return null;
        }

        JSONObject json = new JSONObject();
        // 将原Hash的键与字符串序列值，填入新的json对象中
        for (Map.Entry<Object, Object> entry : object.entrySet()) {
            json.put(entry.getKey().toString(), entry.getValue());
        }
        // 将字符串序列值转成对象
        return json.to(clazz);
    }


    // ------------------------ 设置有效时间 -------------------------

    // 设置有效时间，单位为秒
    public Boolean expire(String redisKey, long expireTime) {
        // 设置有效时间
        return redis.expire(redisKey, expireTime, TimeUnit.SECONDS);
    }

    // 设置指定范围的随机TTL
    public long getRandomTTL(long minSeconds, long maxSeconds) {
        // 指定范围随机公式
        // Random().nextInt(x)/nextLong(x): 0~x-1的整数
        return minSeconds + new Random().nextLong(maxSeconds - minSeconds + 1);
    }

    // ------------------------ 判断存在 -------------------------

    // 判断键是否存在
    public Boolean hasKey(String redisKey) {
        return redis.hasKey(redisKey);
    }


    // ------------------------ 删除操作 -------------------------

    // 删除键
    public Boolean delete(String redisKey) {
        return redis.delete(redisKey);
    }

    // 删除键（模式匹配，批量删除）
    public Boolean deleteByPattern(String redisKeyPattern) {
        // 按照通配符找出所有key
        Set<String> redisKeys = redis.keys(redisKeyPattern);
        // 删除找到的所有key
        if(redisKeys != null && redisKeys.size() > 0){
            redis.delete(redisKeys);
        }
        return true;
    }


    // ------------------------ 专用操作 -------------------------

    // 删除项目经理或开发人员下拉框选项的key
    @Async
    public void deleteUserSelectOptionKey(Byte role){
        String redisKey = "select-option:";
        // 目前只有项目经理与开发人员的下拉框，因此只删这些
        if(role == Role.PM.getValue()){
            redisKey += "pms:*";
        }
        else if(role == Role.DEV.getValue()){
            redisKey += "devs:*";
        }
        else{
            return;
        }
        if(!this.deleteByPattern(redisKey)){
            // 若删除失败，记录log日志
            log.error("项目经理或开发人员下拉框Redis键删除失败，目标键为:{}", redisKey);
        }
    }

    // 删除项目下拉框选项的key
    @Async
    public void deleteProjectSelectOptionKey(){
        String redisKey = "select-option:projects:*";
        if(!this.deleteByPattern(redisKey)){
            // 若删除失败，记录log日志
            log.error("项目下拉框Redis键删除失败，目标键为:{}", redisKey);
        }
    }

    // 删除任务下拉框选项的key
    @Async
    public void deleteTaskSelectOptionKey(){
        String redisKey = "select-option:projects:*";
        if(!this.deleteByPattern(redisKey)){
            // 若删除失败，记录log日志
            log.error("任务下拉框Redis键删除失败，目标键为:{}", redisKey);
        }
    }

    // 删除用户详情的key
    @Async
    public void deleteUserDetailsKey(Integer userId){
        String redisKey = "user:details:" + userId;
        if(!this.delete(redisKey)){
            log.error("用户详情Redis键删除失败，目标键为:{}", redisKey);
        }
    }

//    // 删除用户姓名的key（本项目用户信息的修改功能，暂时不开放真实姓名的修改，故仅预留但不使用）
//    public void deleteUserNameKey(Integer userId){
//        String redisKey = "user:name:" + userId;
//        if(!this.delete(redisKey)){
//            log.error("用户姓名Redis键删除失败，目标键为:{}", redisKey);
//        }
//    }

//    // 删除项目实体的key（纯读的方法极少，暂时取消）
//    @Async
//    public void deleteProjectEntityKey(Integer projectId){
//        String redisKey = "project:entity:" + projectId;
//        if(!this.delete(redisKey)){
//            // 若删除失败，记录log日志
//            log.error("项目实体Redis键删除失败，目标键为:{}", redisKey);
//        }
//    }

    // 删除项目详情的key
    @Async
    public void deleteProjectDetailsKey(Integer projectId){
        String redisKey = "project:details:" + projectId + "*";
        if(!this.deleteByPattern(redisKey)){
            // 若删除失败，记录log日志
            log.error("项目详情Redis键删除失败，目标键为:{}", redisKey);
        }
    }

//    // 删除任务实体的key（纯读的方法极少，暂时取消）
//    @Async
//    public void deleteTaskEntityKey(Integer taskId){
//        String redisKey = "task:entity:" + taskId;
//        if(!this.delete(redisKey)){
//            // 若删除失败，记录log日志
//            log.error("任务实体Redis键删除失败，目标键为:{}", redisKey);
//        }
//    }

    // 删除任务详情的key
    @Async
    public void deleteTaskDetailsKey(Integer taskId){
        String redisKey = "task:details:" + taskId + "*";
        if(!this.deleteByPattern(redisKey)){
            // 若删除失败，记录log日志
            log.error("任务详情Redis键删除失败，目标键为:{}", redisKey);
        }
    }

}
