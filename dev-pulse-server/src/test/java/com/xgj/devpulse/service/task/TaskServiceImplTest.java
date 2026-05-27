package com.xgj.devpulse.service.task;

import com.xgj.devpulse.common.cache.RedisService;
import com.xgj.devpulse.common.context.UserContext;
import com.xgj.devpulse.enums.IsCached;
import com.xgj.devpulse.enums.user.Role;
import com.xgj.devpulse.mapper.ProjectMapper;
import com.xgj.devpulse.mapper.TaskMapper;
import com.xgj.devpulse.pojo.dto.task.TaskUpdateDTO;
import com.xgj.devpulse.pojo.vo.task.TaskDetailsVO;
import com.xgj.devpulse.service.common.DictTagService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.annotation.Argument;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // 让 Mockito 介入
class TaskServiceImplTest {
    @Mock
    private TaskMapper taskMapper;
    @Mock
    private ProjectMapper projectMapper;
    @Mock
    private DictTagService dictTagService;
    @Mock
    private RedisService redisService;

    @InjectMocks
    private TaskServiceImpl taskService; // 测试类

    @BeforeEach
    void setUp() {
    }

    @Test
    void createTask() {
    }

    @Test
    void getTasksByQuery() {
    }

    @Test
    void getTaskSquareList() {
    }


    // 查找特定任务详情
    @ParameterizedTest
    @MethodSource("randomTaskDetails")
    void getTaskById(Role role, IsCached isCached, TaskDetailsVO vo) {
        // 设置Redis键
        String redisKey = "task:details:" + vo.getTaskId();
        redisKey += (role == Role.DEV) ? ":dev" : ":no-dev";

        // 开启静态方法的Mock
        try (MockedStatic<UserContext> mockedUserContext = mockStatic(UserContext.class)) {
            // Mock数据
            mockedUserContext.when(UserContext::getCurrentRole).thenReturn(role);       // Mock用户上下文（角色）

            switch (isCached){
                case IsCached.Cached:  // 缓存命中
                    when(redisService.get(redisKey, TaskDetailsVO.class)).thenReturn(vo);   // 查找Redis有结果
                    break;
                case IsCached.NoCached:
                    when(redisService.get(redisKey, TaskDetailsVO.class)).thenReturn(null);   // 缓存未命中
                    when(taskMapper.getTaskDetails(vo.getTaskId(), role.getValue())).thenReturn(vo);    // 查找数据库
                    break;
            }

            // 测试目标方法
            TaskDetailsVO result = taskService.getTaskById(vo.getTaskId());

            // 验证
            assertNotNull(result);    // 验证非空
            assertEquals(vo.getTaskName(), result.getTaskName());   // 任务信息准确性

            switch (isCached){
                case IsCached.Cached:   // 缓存命中时，未查询数据库
                    verify(taskMapper, times(0)).getTaskDetails(vo.getTaskId(), role.getValue());
                    break;
                case IsCached.NoCached:    // 缓存未命中时，查询1次数据库，且有缓存设置
                    verify(taskMapper, times(1)).getTaskDetails(vo.getTaskId(), role.getValue());
                    verify(redisService).set(eq(redisKey), any(TaskDetailsVO.class), anyLong());
                    break;
            }

        }
    }

    // 生成批量数据（任务详情）
    static Stream<Arguments> randomTaskDetails() {
        List<Arguments> testCase = new ArrayList<>();
        for (int i = 0; i < 500; i++) {
            // 角色
            Role role = Role.fromValue((byte)(new Random().nextInt(4) + 1));

            // 缓存是否命中
            IsCached isCached = IsCached.fromValue((byte)(new Random().nextInt(2) + 1));

            // 任务详情信息
            TaskDetailsVO dto = new TaskDetailsVO();
            dto.setTaskId(new Random().nextInt(10000) + 1);
            dto.setTaskName("任务-" + UUID.randomUUID().toString().substring(0, 8));

            testCase.add(Arguments.of(role, isCached, dto));
        }
        return testCase.stream();
    }

}