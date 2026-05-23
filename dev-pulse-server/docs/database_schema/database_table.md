### 用户类
#### 1. 用户表 (User)

| 字段名 | 物理名 | 数据类型 | 长度 | 允许空 | 字段描述 |
|:---|:---|:---|:---:|:---:|:---|
| **用户编号** | UserID | int | - | NOT NULL | 主键，自增 |
| 用户名 | Username | varchar | 50 | NOT NULL | 登录用户名，唯一 |
| 密码 | Password | varchar | 100 | NOT NULL | 加密存储 |
| 真实姓名 | RealName | varchar | 50 | NOT NULL | 用户真实姓名 |
| 用户角色 | Role | tinyint | - | NOT NULL | 1-PMO, 2-PM, 3-开发人员, 4-系统管理员 |
| 电子邮箱 | Email | varchar | 100 | NULL | 电子邮箱 |
| 联系电话 | Phone | varchar | 20 | NULL | 联系电话 |
| 状态 | Status | tinyint | - | NOT NULL | 1-待验证, 2-已验证, 3-未通过 |
| 注册时间 | CreateTime | datetime | - | NOT NULL | 注册时间，默认当前时间 |



#### 2. 开发人员简历表 (Dev_Profile)

| 字段名 | 物理名 | 数据类型 | 长度 | 允许空 | 字段描述 |
|:---|:---|:---|:---:|:---:|:---|
| **简历编号** | ProfileID | int | - | NOT NULL | 主键，自增 |
| 开发人员编号 | UserID | int | - | NOT NULL | **外键，关联User.UserID** |
| 是否首次登录 | IsFirst | tinyint | - | NOT NULL | 1-是, 2-否，默认1 |
| 个人简历 | ResumeText | varchar | 2000 | NULL | 个人简历文本 |
| 个人技能标签 | Skills | varchar | 1000 | NULL | 个人技能标签 (字符串显示字段) |

### 项目类
#### 3. 项目表 (Project)

| 字段名 | 物理名 | 数据类型 | 长度 | 允许空 | 字段描述 |
|:---|:---|:---|:---:|:---:|:---|
| ****项目编号**** | ProjectID | int | - | NOT NULL | 主键，自增 |
| 项目名称 | ProjectName | varchar | 100 | NOT NULL | 项目名称 |
| 项目经理编号 | PMID | int | - | NOT NULL | **外键，关联User.UserID** |
| 项目状态 | Status | tinyint | - | NOT NULL | 1-待审核, 2-待修改, 3-进行中, 4-待结项, 5-已归档 |
| 甲方名称 | ClientName | varchar | 100 | NULL | 甲方代表姓名或公司名称 |
| 甲方邮箱 | ClientEmail | varchar | 100 | NULL | 甲方电子邮箱 |
| 甲方电话 | ClientPhone | varchar | 20 | NULL | 甲方联系电话 |
| 项目描述 | ProjectDescription | varchar | 2000 | NULL | 项目详细描述 |
| 预算金额 | Budget | decimal | 18,2 | NULL | 项目预算 |
| 预计人力数量 | Personnel | int | - | NULL | 预计所需人数 |
| 项目需求文档 | RequirementDocUrl | varchar | 500 | NULL | 需求文档存储的相对路径 |
| 结项报告 | FinalReportUrl | varchar | 500 | NULL | 结项报告存储的相对路径 |
| 设定开始日期 | StartDate | date | - | NULL | 计划开始日期 |
| 设定结束日期 | EndDate | date | - | NULL | 计划结束日期 |
| 创建时间 | CreateTime | datetime | - | NOT NULL | 默认当前时间 |
| 立项时间 | ApprovalTime | datetime | - | NULL | 审批通过时间 |
| 结项归档时间 | FinishTime | datetime | - | NULL | 结项完成时间 |



#### 4. 项目审批记录表 (Project_Approval)

| 字段名 | 物理名 | 数据类型 | 长度 | 允许空 | 字段描述 |
|:---|:---|:---|:---:|:---:|:---|
| **项目审批记录编号** | ApprovalID | int | - | NOT NULL | 主键，自增 |
| 项目编号 | ProjectID | int | - | NOT NULL | **外键，关联Project.ProjectID** |
| PMO编号 | PMOID | int | - | NOT NULL | **外键，关联User.UserID** |
| 审批类型 | ApprovalType | tinyint | - | NOT NULL | 1-立项审批, 2-结项审批 |
| 审批结果 | Result | tinyint | - | NOT NULL | 1-通过, 2-驳回 |
| 审批意见 | Comment | varchar | 500 | NULL | PMO审批备注 |
| 审批时间 | ApprovalTime | datetime | - | NOT NULL | 默认当前时间 |


### 任务类
#### 5. 任务表 (Task)

| 字段名 | 物理名 | 数据类型 | 长度 | 允许空 | 字段描述 |
|:---|:---|:---|:---:|:---:|:---|
| **任务编号** | TaskID | int | - | NOT NULL | 主键，自增 |
| 关联项目编号 | ProjectID | int | - | NOT NULL | **外键，关联Project.ProjectID** |
| 任务名称 | TaskName | varchar | 100 | NOT NULL | 任务名称 |
| 任务描述 | TaskDescription | varchar | 2000 | NULL | 任务详细描述 |
| 任务所需技能 | RequiredSkills | varchar | 1000 | NULL | 任务所需技能标签 (字符串显示字段) |
| 开发人员编号 | DevID | int | - | **NULL** | **外键，关联User.UserID** |
| 任务状态 | Status | tinyint | - | NOT NULL | 1-待分配, 2-进行中, 3-待验收, 4-已完成 |
| 任务总版次 | Revision | int | - | NOT NULL | 版次号，默认0，开发人员每提交一次任务时自增 |
| 预估工时 | EstimatedHours | int | - | **NOT NULL** | 预估工时（小时） |
| 实际工时 | ActualHours | int | - | NULL | 实际工时（小时） |
| 创建时间 | CreateTime | datetime | - | NOT NULL | 默认当前时间 |
| 匹配时间 | AssignTime | datetime | - | NULL | 成功匹配开发人员的时间 |
| 完成时间 | FinishTime | datetime | - | NULL | 任务完成时间 |



#### 6. 任务申请记录表 (Task_Application)

| 字段名 | 物理名 | 数据类型 | 长度 | 允许空 | 字段描述 |
|:---|:---|:---|:---:|:---:|:---|
| **任务申请记录编号** | ApplicationID | int | - | NOT NULL | 主键。自增 |
| 关联任务编号 | TaskID | int | - | NOT NULL | **外键，关联Task.TaskID** |
| 项目经理编号 | PMID | int | - | NOT NULL | **外键，关联User.UserID** |
| 开发人员编号 | DevID | int | - | NOT NULL | **外键，关联User.UserID** |
| 任务申请类型 | Type | tinyint | - | NOT NULL | 1-项目经理邀请, 2-开发人员申请 |
| 任务申请状态 | Status | tinyint | - | NOT NULL | 1-待处理, 2-已同意, 3-已失效 |
| 申请时间 | ApplyTime | datetime | - | NOT NULL | 默认当前时间 |
| 处理申请时间 | DealTime | datetime | - | NULL | 状态变为已同意/已失效的时间 |

#### 7. 任务评审记录表 (Task_Review)

| 字段名 | 物理名 | 数据类型 | 长度 | 允许空 | 字段描述 |
|:---|:---|:---|:---:|:---:|:---|
| **任务评审记录编号** | ReviewID | int | - | NOT NULL | 主键，自增 |
| 关联任务编号 | TaskID | int | - | NOT NULL | **外键，关联Task.TaskID** |
| 开发人员编号 | DevID | int | - | NOT NULL | **外键，关联User.UserID** |
| 项目经理编号 | PMID | int | - | NOT NULL | 外键关联 User.UserID |
| Git链接 | GitUrl | varchar | 500 | NULL | 交付物——Git链接 |
| 代码文件 | ArchiveUrl | varchar | 500 | NULL | 交付物——代码压缩包链接 |
| 文档文件 | DocUrl | varchar | 500 | NULL | 交付物——文档链接 |
| 任务提交版次 | Revision | int | - | NOT NULL | 版次号 |
| 评审结果 | Result | tinyint | - | NOT NULL | 1-待评审, 2-通过, 3-返工 |
| 评审意见 | Comment | varchar | 500 | NULL | 评审意见 |
| 任务提交时间 | SubmitTime | datetime | - | NOT NULL | 开发人员提交时间 |
| 评审时间 | ReviewTime | datetime | - | NULL | 项目经理评审时间 |



### 工时类
#### 8. 任务实际工时记录表 (Work_Log)

| 字段名 | 物理名 | 数据类型 | 长度 | 允许空 | 字段描述 |
|:---|:---|:---|:---:|:---:|:---|
| **工时日志编号** | LogID | int | - | NOT NULL | 主键。自增 |
| 关联任务编号 | TaskID | int | - | NOT NULL | **外键，关联Task.TaskID** |
| 开发人员编号 | UserID | int | - | NOT NULL | **外键，关联User.UserID** |
| 日志状态 | Status | tinyint | - | NOT NULL | 1-可修改, 2-只读 |
| 工作日期 | WorkDate | date | - | NOT NULL | 填报工时日期 |
| 投入工时 | Hours | int | - | NOT NULL | 工时（小时） |
| 工作描述 | Description | varchar | 500 | NULL | 工作内容描述 |
| 日志最后修改时间 | LastTime | datetime | - | NOT NULL | 自动更新 |



#### 9. 任务预估工时修改记录表 (Task_Change_Log)

| 字段名 | 物理名 | 数据类型 | 长度 | 允许空 | 字段描述 |
|:---|:---|:---|:---:|:---:|:---|
| **预估工时修改记录编号** | ChangeID | int | - | NOT NULL | 主键，自增 |
| 关联任务编号 | TaskID | int | - | NOT NULL | **外键，关联Task.TaskID** |
| 关联项目编号 | ProjectID | int | - | NOT NULL | **外键，关联Project.ProjectID** |
| 项目经理编号 | PMID | int | - | NOT NULL | **外键，关联User.UserID** |
| 原预估工时 | OldHours | int | - | NOT NULL | 修改前预估工时（小时） |
| 修改后预估工时 | NewHours | int | - | NOT NULL | 修改后预估工时（小时） |
| 修改原因 | ChangeReason | varchar | 500 | NOT NULL | 修改原因说明 |
| 修改时间 | ChangeTime | datetime | - | NOT NULL | 默认当前时间 |

(设计`关联项目编号`字段，反3NF设计，提高聚合计算的效率)




### 绩效类
#### 10. 任务级绩效记录表 (Task_Performance)

| 字段名 | 物理名 | 数据类型 | 长度 | 允许空 | 字段描述 |
|:---|:---|:---|:---:|:---:|:---|
| **任务级绩效记录编号** | TaskPerfID | int | - | NOT NULL | 主键，自增 |
| 任务编号 | TaskID | int | - | NULL | **外键，关联Task.TaskID** |
| 项目经理编号 | PMID | int | - | NOT NULL | 评价的项目经理，**外键，关联User.UserID** |
| 开发人员编号 | DevID | int | - | NOT NULL | 被评价的开发人员，**外键关联 User.UserID** |
| 质量分 | Quality | decimal | 5,2 | NOT NULL | 质量分 |
| 工时效率分 | Efficiency | decimal | 5,2 | NOT NULL | 工时效率分 |
| 主观评分 | PMScore | decimal | 5,2 | NOT NULL | 项目经理的主观评分 |
| 绩效总分 | TotalScore | decimal | 5,2 | NOT NULL | 综合总分 |
| 绩效评语 | Comment | varchar | 500 | NULL | 绩效评语 |
| 绩效状态 | Status | tinyint | - | NOT NULL | 1-未发布, 2-已发布 |
| 绩效评价时间 | EvaluateTime | datetime | - | **NULL** | 项目经理提交主观评价的时间 |


#### 11. 项目级绩效记录表 (Project_Performance)

| 字段名 | 物理名 | 数据类型 | 长度 | 允许空 | 字段描述 |
|:---|:---|:---|:---:|:---:|:---|
| **项目级绩效记录编号** | ProjectPerfID | int | - | NOT NULL | 主键，自增 |
| 项目编号 | ProjectID | int | - | NULL | **外键，关联Project.ProjectID** |
| PMO编号 | PMOID | int | - | **NULL** | 评价的PMO，**外键，关联User.UserID** |
| 项目经理编号 | PMID | int | - | NOT NULL | 被评价的项目经理，**外键，关联User.UserID** |
| 资源控制率得分 | Resource | decimal | 5,2 | NOT NULL | 资源控制评分 |
| 预估工时修改审计扣分 | Modify | decimal | 5,2 | NOT NULL | 工时修改扣分 |
| 主观评分 | PMOScore | decimal | 5,2 | NOT NULL | PMO的主观评分 |
| 绩效总分 | TotalScore | decimal | 5,2 | NOT NULL | 综合总分 |
| 绩效评语 | Comment | varchar | 500 | NULL | 绩效评语 |
| 绩效状态 | Status | tinyint | - | NOT NULL | 1-未发布, 2-已发布 |
| 绩效评价时间 | EvaluateTime | datetime | - | **NULL** | PMO提交主观评价的时间 |

### 通知类
#### 12. 消息通知表 (Notice)

| 字段名 | 物理名 | 数据类型 | 长度 | 允许空 | 字段描述 |
|:---|:---|:---|:---:|:---:|:---|
| **消息通知编号** | NoticeID | int | - | NOT NULL | 主键，自增 |
| 接收人员编号 | RecieverID | int | - | NOT NULL | 接收者，**外键，关联User.UserID** |
| 发送人员编号 | SenderID | int | - | NOT NULL | 发送者，**外键，关联User.UserID** |
| 消息通知标题 | Title | varchar | 100 | NOT NULL | 通知标题 |
| 消息通知内容 | Content | varchar | 2000 | NOT NULL | 通知内容 |
| 消息通知类型 | NoticeType | tinyint | - | NOT NULL | 1-系统通知, 2-审核结果, 3-任务邀请/申请, 4-工时预警, 5-验收结果, 6-其他 |
| 消息通知状态 | Status | tinyint | - | NOT NULL | 1-未读, 2-已读, 3-已删除 |
| 创建时间 | CreateTime | datetime | - | NOT NULL | 当前时间 |

### 技能标签类
#### 13. 技能标签表 (Dict_Tag)

| 字段名 | 物理名 | 数据类型 | 长度 | 允许空 | 字段描述 |
|:---|:---|:---|:---:|:---:|:---|
| **标签编号** | TagID | int | - | NOT NULL | 主键，自增 |
| 标签名称 | TagName | varchar | 50 | NOT NULL | 技能标签名称 |


#### 14. 技能标签关联表 (Tag_Relation)

| 字段名 | 物理名 | 数据类型 | 长度 | 允许空 | 字段描述 |
|:---|:---|:---|:---:|:---:|:---|
| **标签关联编号** | RelationID | int | - | NOT NULL | 主键，自增 |
| 标签编号 | TagID | int | - | NOT NULL | **外键，关联Dict_Tag.TagID** |
| 关联对象类型 | TargetType | tinyint | - | NOT NULL | 1-用户(开发人员), 2-任务 |
| 关联对象编号 | TargetID | int | - | NOT NULL | 关联对象编号(用户或任务编号) |


### 系统日志类
#### 15. 操作日志表 (Operation_Log)

| 字段名 | 物理名 | 数据类型 | 长度 | 允许空 | 字段描述 |
|:---|:---|:---|:---:|:---:|:---|
| **操作日志编号** | LogID | int | - | NOT NULL | 主键，自增 |
| 操作用户编号 | UserID | int | - | **NULL** | **外键，关联User.UserID** |
| 操作接口 | ApiRoute | varchar | 255 | NULL | 请求接口地址 |
| 接口耗时 | ExecutionTime | int | - | NOT NULL | 耗时（毫秒） |
| 状态码 | StatusCode | int | - | NOT NULL | 状态码（200, 401, 404等） |
| 操作IP | IpAddress | varchar | 100 | NULL | 操作IP地址 |
| 操作时间 | CreateTime | datetime | - | NOT NULL | 当前时间 |



#### 16. 系统异常日志表 (Error_Log)

| 字段名 | 物理名 | 数据类型 | 长度 | 允许空 | 字段描述 |
|:---|:---|:---|:---:|:---:|:---|
| **异常日志编号** | LogID | int | - | NOT NULL | 主键，自增 |
| 操作用户编号 | UserID | int | - | **NULL** | **外键，关联User.UserID** |
| 异常接口 | EndPoint | varchar | 255 | NULL | 异常发生接口 |
| 错误信息 | ExceptionMessage | **mediumtext** | - | NOT NULL | 错误信息 |
| 堆栈详情 | StackTrace | **mediumtext** | - | NOT NULL | 堆栈详情 |
| 异常时间 | ErrorTime | datetime | - | NOT NULL | 当前时间 |

#### 17. Token使用日志表 (Token_Usage_Log)

| 字段名 | 物理名 | 数据类型 | 长度 | 允许空 | 字段描述 |
|:---|:---|:---|:---:|:---:|:---|
| **使用日志编号** | LogID | int | - | NOT NULL | 主键，自增 |
| 请求AI的用户编号 | UserID | int | - | **NULL** | **外键，关联User.UserID** |
| 模型名称 | ModelName | varchar | 100 | NOT NULL | 模型名称 |
| 响应ID | ResponseID | varchar | 128 | NOT NULL | 回答的唯一标识，即响应请求体的id字段 |
| 业务类型 | Type | varchar | 100 | NOT NULL | 根据具体的AI服务赋值 |
| 输入Token | PromptTokens | int | - | NOT NULL | 输入Token |
| 缓存命中的Token | CachedTokens | int | - | NOT NULL | 缓存命中的Token |
| 输出Token | CompletionTokens | int | - | NOT NULL | 输出Token |
| 总Token | TotalTokens | int | - | NOT NULL | 总Token |
| 记录产生时间 | CreateTime | datetime | - | NOT NULL | 当前时间 |

