package com.xgj.devpulse.service.task;

import com.xgj.devpulse.common.cache.RedisService;
import com.xgj.devpulse.common.context.UserContext;
import com.xgj.devpulse.common.exception.BusinessException;
import com.xgj.devpulse.enums.IsCached;
import com.xgj.devpulse.enums.project.ProjectStatus;
import com.xgj.devpulse.enums.task.TaskStatus;
import com.xgj.devpulse.enums.user.Role;
import com.xgj.devpulse.mapper.ProjectMapper;
import com.xgj.devpulse.mapper.TaskMapper;
import com.xgj.devpulse.mapper.UserMapper;
import com.xgj.devpulse.pojo.dto.auth.RegisterRequestDTO;
import com.xgj.devpulse.pojo.dto.task.TaskQueryDTO;
import com.xgj.devpulse.pojo.entity.ProjectEntity;
import com.xgj.devpulse.pojo.entity.TaskEntity;
import com.xgj.devpulse.pojo.entity.UserEntity;
import com.xgj.devpulse.pojo.vo.common.PageResultVO;
import com.xgj.devpulse.pojo.vo.task.TaskDetailsVO;
import com.xgj.devpulse.pojo.vo.task.TaskListVO;
import com.xgj.devpulse.service.common.DictTagService;
import com.xgj.devpulse.utils.PasswordHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;


@SpringBootTest
@ActiveProfiles("test")    // 激活 application-test.yml
@Transactional             // 测试完自动回滚
class TaskServiceImplIT {
    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private ProjectMapper projectMapper;
    @Autowired
    private DictTagService dictTagService;
    @Autowired
    private RedisService redisService;

    @Autowired
    private TaskServiceImpl taskService; // 测试类
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        // 准备数据
        this.prepareTaskListData();
    }

    @AfterEach
    void resetDatabase() {
        // 重置自增主键从 1 开始
        jdbcTemplate.execute("ALTER TABLE `User` ALTER COLUMN `UserID` RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE `Project` ALTER COLUMN `ProjectID` RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE `Task` ALTER COLUMN `TaskID` RESTART WITH 1");
    }

    @Test
    void createTask() {
    }

    @ParameterizedTest
    @MethodSource("randomTaskQuery")
    void getTasksByQuery(int userId, Role role, int isPaged, TaskQueryDTO dto) {

        // 开启静态方法的Mock
        try (MockedStatic<UserContext> mockedUserContext = mockStatic(UserContext.class)) {
            // Mock用户上下文
            mockedUserContext.when(UserContext::getCurrentUserId).thenReturn(userId);
            mockedUserContext.when(UserContext::getCurrentRole).thenReturn(role);

            // 页号不为正时，应该抛出异常
            if(dto.getPageNum() <= 0){
                assertThrows(BusinessException.class, new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        taskService.getTasksByQuery(dto);
                    }
                });
            }
            else{
                if(isPaged == 0){
                    assertEquals(1, dto.getPageNum());
                    assertEquals(10, dto.getPageSize());
                }
                // 测试类
                PageResultVO<TaskListVO> result = taskService.getTasksByQuery(dto);
                // 验证
                assertNotNull(result);
            }
        }
    }

    @Test
    void getTaskSquareList() {
    }

    @Test
    void getTaskById() {
    }


    private void prepareTaskListData(){
        // 创建用户
        for (int i = 1; i <= 10; i++) {
            int userId = i;
            byte role = (byte)(new Random().nextInt(4) + 1);    // 角色
            String pwd = PasswordHelper.hashPassword("123456");

            RegisterRequestDTO dto = RegisterRequestDTO
                    .builder()
                    .username(UUID.randomUUID().toString())
                    .realName(UUID.randomUUID().toString())
                    .password(pwd)
                    .role(role)
                    .build();

            userMapper.insertUser(dto, LocalDateTime.now());

            // 创建项目
            if(role == Role.PM.getValue()){
                for(int j = 1; j <= 5; j++){
                    ProjectEntity project = new ProjectEntity();
                    // 填写关键信息
                    project.setProjectName(UUID.randomUUID().toString());
                    project.setPMID(userId);
                    project.setStatus(ProjectStatus.Ongoing.getValue());
                    // 填写详情信息
                    project.setProjectDescription(UUID.randomUUID().toString());
                    // 填写时间信息
                    project.setCreateTime(LocalDateTime.now());

                    projectMapper.insertProject(project);
                    for(int k = 1; k <= 5; k++){
                        // 构造实体类
                        TaskEntity task = new TaskEntity();
                        task.setProjectID(project.getProjectID());
                        task.setTaskName(UUID.randomUUID().toString());
                        task.setTaskDescription(UUID.randomUUID().toString());
                        task.setStatus(TaskStatus.Pending.getValue());
                        // 未进行任何提交时，设版次号为0，更符合语义
                        task.setRevision(0);
                        task.setEstimatedHours(0);
                        task.setActualHours(0);
                        task.setCreateTime(LocalDateTime.now());

                        taskMapper.insertTask(task);
                    }
                }
            }
        }
    }

    // 生成query数据
    static Stream<Arguments> randomTaskQuery() {
        List<Arguments> testCase = new ArrayList<>();
        Random rnd = new Random();

        for (int i = 1; i <= 20; i++) {
            // 用户编号
            int userId = (i - 1) % 10 + 1;
            // 角色
            Role role = Role.fromValue((byte)(rnd.nextInt(4) + 1));

            // query信息
            TaskQueryDTO dto = new TaskQueryDTO();
//            dto.setTaskName();
//            dto.setProjectName();


            int isPaged = rnd.nextInt(2);
            if(isPaged == 1){
                dto.setPageNum(rnd.nextInt(1000 - (-1000) + 1) -1000);  // [-1000, 1000]
                dto.setPageSize(rnd.nextInt(100) + 1);
            }

            testCase.add(Arguments.of(userId, role, isPaged, dto));
        }
        return testCase.stream();
    }
}