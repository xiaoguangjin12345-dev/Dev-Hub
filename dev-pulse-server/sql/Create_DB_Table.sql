CREATE DATABASE IF NOT EXISTS dev_pulse;

use dev_pulse;

-- 用户表
CREATE TABLE if not exists `User` (
    `UserID` int AUTO_INCREMENT COMMENT '主键，自增，用户唯一标识',
    `Username` varchar(50) NOT NULL COMMENT '登录用户名，唯一',
    `Password` varchar(100) NOT NULL COMMENT '登录密码（加密存储）',
    `RealName` varchar(50) NOT NULL COMMENT '用户真实姓名',
    `Role` tinyint NOT NULL COMMENT '用户角色（1-PMO, 2-PM, 3-开发人员, 4-系统管理员）',
    `Email` varchar(100) NULL COMMENT '电子邮箱',
    `Phone` varchar(20) NULL COMMENT '联系电话',
    `Status` tinyint NOT NULL COMMENT '状态（1-待验证, 2-已验证, 3-未通过）',
    `CreateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    PRIMARY KEY (`UserID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 开发人员简历表
CREATE TABLE if not exists `Dev_Profile` (
    `ProfileID` int AUTO_INCREMENT COMMENT '简历编号，主键自增',
    `UserID` int NOT NULL COMMENT '开发人员编号，对应User表的UserID',
    `IsFirst` tinyint NOT NULL DEFAULT 1 COMMENT '是否首次登录（1-是, 2-否）',
    `ResumeText` varchar(2000) NULL COMMENT '个人简历',
    `Skills` varchar(1000) NULL COMMENT '个人技能标签',
    PRIMARY KEY (`ProfileID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='开发人员简历表';

-- 项目表
CREATE TABLE if not exists `Project` (
    `ProjectID` int AUTO_INCREMENT COMMENT '项目编号，主键自增',
    `ProjectName` varchar(100) NOT NULL COMMENT '项目名称',
    `PMID` int NOT NULL COMMENT '项目经理编号，外键关联 User.UserID',
    `Status` tinyint NOT NULL COMMENT '状态（1-待审核, 2-待修改, 3-进行中, 4-待结项, 5-已归档）',
    `ClientName` varchar(100) NULL COMMENT '甲方代表姓名或公司名称',
    `ClientEmail` varchar(100) NULL COMMENT '甲方电子邮箱',
    `ClientPhone` varchar(20) NULL COMMENT '甲方联系电话',
    `ProjectDescription` varchar(2000) NULL COMMENT '项目描述',
    `Budget` decimal(18,2) NULL COMMENT '预算金额',
    `Personnel` int NULL COMMENT '预计人力数量',
    `RequirementDocUrl` varchar(500) NULL COMMENT '项目需求文档链接',
    `FinalReportUrl` varchar(500) NULL COMMENT '结项报告链接',
    `StartDate` date NULL COMMENT '设定开始日期',
    `EndDate` date NULL COMMENT '设定结束日期',
    `CreateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `ApprovalTime` datetime NULL COMMENT '立项时间',
    `FinishTime` datetime NULL COMMENT '结项归档时间',
    PRIMARY KEY (`ProjectID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目表';

-- 项目审批记录表
CREATE TABLE if not exists `Project_Approval` (
    `ApprovalID` int AUTO_INCREMENT COMMENT '项目审批记录编号，主键自增',
    `ProjectID` int NOT NULL COMMENT '项目编号，外键关联 Project.ProjectID',
    `PMOID` int NOT NULL COMMENT 'PMO编号，外键关联 User.UserID',
    `ApprovalType` tinyint NOT NULL COMMENT '审批类型（1-立项审批, 2-结项审批）',
    `Result` tinyint NOT NULL COMMENT '审批结果（1-通过, 2-驳回）',
    `Comment` varchar(500) NULL COMMENT '审批意见',
    `ApprovalTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '审批时间',
    PRIMARY KEY (`ApprovalID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目审批记录表';

-- 任务表
CREATE TABLE if not exists `Task` (
    `TaskID` int AUTO_INCREMENT COMMENT '任务编号，主键，自增',
    `ProjectID` int NOT NULL COMMENT '关联项目编号，外键，关联 Project.ProjectID',
    `TaskName` varchar(100) NOT NULL COMMENT '任务名称',
    `TaskDescription` varchar(2000) NULL COMMENT '任务描述',
    `RequiredSkills` varchar(1000) NULL COMMENT '任务技能标签',
    `DevID` int NULL COMMENT '开发人员编号，外键，关联 User.UserID',
    `Status` tinyint NOT NULL COMMENT '状态（1-待分配, 2-进行中, 3-待验收, 4-已完成）',
    `Revision` int NOT NULL DEFAULT 0 COMMENT '任务总版次',
    `EstimatedHours` int NOT NULL COMMENT '预估工时',
    `ActualHours` int NULL COMMENT '实际工时',
    `CreateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `AssignTime` datetime NULL COMMENT '成功匹配开发人员的时间',
    `FinishTime` datetime NULL COMMENT '完成时间',
    PRIMARY KEY (`TaskID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务表';

-- 任务申请记录表
CREATE TABLE if not exists `Task_Application` (
    `ApplicationID` int AUTO_INCREMENT COMMENT '任务申请记录编号，主键，自增',
    `TaskID` int NOT NULL COMMENT '关联任务编号，外键，关联Task.TaskID',
    `PMID` int NOT NULL COMMENT '项目经理编号，外键，关联User.UserID',
    `DevID` int NOT NULL COMMENT '开发人员编号，外键，关联User.UserID',
    `Type` tinyint NOT NULL COMMENT '发起方类型（1-PM邀请, 2-开发人员申请）',
    `Status` tinyint NOT NULL COMMENT '状态（1-待处理, 2-已同意, 3-已失效）',
    `ApplyTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请发起时间',
    `DealTime` datetime NULL COMMENT '处理申请时间',
    PRIMARY KEY (`ApplicationID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务申请记录表';

-- 任务实际工时记录表
CREATE TABLE if not exists `Work_Log` (
    `LogID` int AUTO_INCREMENT COMMENT '工时日志编号，主键，自增',
    `TaskID` int NOT NULL COMMENT '关联任务编号，外键，关联Task.TaskID',
    `UserID` int NOT NULL COMMENT '开发人员编号，外键，关联User.UserID',
    `Status` tinyint NOT NULL COMMENT '状态（1-可修改, 2-只读）',
    `WorkDate` date NOT NULL COMMENT '工作日期',
    `Hours` int NOT NULL COMMENT '投入工时',
    `Description` varchar(500) NULL COMMENT '工作内容描述',
    `LastTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
    PRIMARY KEY (`LogID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务实际工时记录表';

-- 任务预估工时修改记录表
CREATE TABLE if not exists `Task_Change_Log` (
    `ChangeID` int AUTO_INCREMENT COMMENT '预估工时修改记录编号，主键，自增',
    `TaskID` int NOT NULL COMMENT '关联任务编号，外键，关联Task.TaskID',
    `ProjectID` int NOT NULL COMMENT '关联项目编号，外键，关联Project.ProjectID',
    `PMID` int NOT NULL COMMENT '项目经理编号，外键，关联User.UserID',
    `OldHours` int NOT NULL COMMENT '原工时',
    `NewHours` int NOT NULL COMMENT '修改后工时',
    `ChangeReason` varchar(500) NOT NULL COMMENT '修改原因',
    `ChangeTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`ChangeID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务预估工时修改记录表';

-- 任务评审记录表
CREATE TABLE if not exists `Task_Review` (
    `ReviewID` int AUTO_INCREMENT COMMENT '任务评审记录编号，主键，自增',
    `TaskID` int NOT NULL COMMENT '关联任务编号，外键，关联Task.TaskID',
    `DevID` int NOT NULL COMMENT '开发人员编号，外键，关联User.UserID',
    `PMID` int NOT NULL COMMENT '项目经理编号，外键，关联User.UserID',
    `GitUrl` varchar(500) NULL COMMENT '交付物——Git链接',
    `ArchiveUrl` varchar(500) NULL COMMENT '交付物——代码压缩包链接',
    `DocUrl` varchar(500) NULL COMMENT '交付物——文档链接',
    `Revision` int NOT NULL COMMENT '当前提交的版次号',
    `Result` tinyint NOT NULL COMMENT '评审结果（1-待评审, 2-通过, 3-返工）',
    `Comment` varchar(500) NULL COMMENT '评审意见',
    `SubmitTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '开发人员提交时间',
    `ReviewTime` datetime NULL COMMENT '项目经理评审时间',
    PRIMARY KEY (`ReviewID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务评审记录表';

-- 任务级绩效记录表
CREATE TABLE if not exists `Task_Performance` (
    `TaskPerfID` int AUTO_INCREMENT COMMENT '任务级绩效记录编号，主键，自增',
    `TaskID` int NULL COMMENT '关联任务编号，外键，关联Task.TaskID',
    `PMID` int NOT NULL COMMENT '评价人（PM）编号，外键，关联User.UserID',
    `DevID` int NOT NULL COMMENT '被评价人（开发人员）编号，外键，关联User.UserID',
    `Quality` decimal(5,2) NOT NULL COMMENT '质量分',
    `Efficiency` decimal(5,2) NOT NULL COMMENT '工时效率分',
    `PMScore` decimal(5,2) NOT NULL COMMENT 'PM主观评分',
    `TotalScore` decimal(5,2) NOT NULL COMMENT '绩效总分',
    `Comment` varchar(500) NULL COMMENT '绩效评语',
    `Status` tinyint NOT NULL COMMENT '绩效状态（1-未发布, 2-已发布）',
    `EvaluateTime` datetime NULL COMMENT '绩效评价/发布时间',
    PRIMARY KEY (`TaskPerfID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务级绩效记录表';

-- 项目级绩效记录表
CREATE TABLE if not exists `Project_Performance` (
    `ProjectPerfID` int AUTO_INCREMENT COMMENT '项目级绩效记录编号，主键，自增',
    `ProjectID` int NULL COMMENT '关联项目编号，外键，关联 Project.ProjectID',
    `PMOID` int NULL COMMENT '评价人（PMO）编号，外键，关联 User.UserID',
    `PMID` int NOT NULL COMMENT '被评价人（PM）编号，外键，关联 User.UserID',
    `Resource` decimal(5,2) NOT NULL COMMENT '资源控制率得分',
    `Modify` decimal(5,2) NOT NULL COMMENT '预估工时修改审计扣分',
    `PMOScore` decimal(5,2) NOT NULL COMMENT 'PMO主观评分',
    `TotalScore` decimal(5,2) NOT NULL COMMENT '绩效总分',
    `Comment` varchar(500) NULL COMMENT '绩效评语',
    `Status` tinyint NOT NULL COMMENT '绩效状态（1-未发布, 2-已发布）',
    `EvaluateTime` datetime NULL COMMENT '绩效评价/发布时间',
    PRIMARY KEY (`ProjectPerfID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目级绩效记录表';

-- 消息通知表
CREATE TABLE if not exists `Notice` (
    `NoticeID` int AUTO_INCREMENT COMMENT '消息通知编号，主键，自增',
    `RecieverID` int NOT NULL COMMENT '接收人员编号，外键，关联 User.UserID',
    `SenderID` int NOT NULL COMMENT '发送人员编号，外键，关联 User.UserID',
    `Title` varchar(100) NOT NULL COMMENT '通知标题',
    `Content` varchar(2000) NOT NULL COMMENT '通知具体内容',
    `NoticeType` tinyint NOT NULL COMMENT '通知类型（1-系统通知, 2-审核结果, 3-任务邀请/申请, 4-工时预警, 5-验收结果, 6-其他）',
    `Status` tinyint NOT NULL COMMENT '通知状态（1-未读, 2-已读, 3-已删除）',
    `CreateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '消息产生时间',
    PRIMARY KEY (`NoticeID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息通知表';

-- 技能标签表
CREATE TABLE if not exists `Dict_Tag` (
    `TagID` int AUTO_INCREMENT COMMENT '标签编号，主键，自增',
    `TagName` varchar(50) NOT NULL COMMENT '标签名称',
    PRIMARY KEY (`TagID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='技能标签表';

-- 技能标签关联表
CREATE TABLE if not exists `Tag_Relation` (
    `RelationID` int AUTO_INCREMENT COMMENT '标签关联编号，主键，自增',
    `TagID` int NOT NULL COMMENT '关联的标签编号，外键，关联 Dict_Tag.TagID',
    `TargetType` tinyint NOT NULL COMMENT '关联对象类型（1-开发人员, 2-任务）',
    `TargetID` int NOT NULL COMMENT '关联对象编号（根据类型对应 UserID 或 TaskID）',
    PRIMARY KEY (`RelationID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='技能标签关联表';

-- 操作日志表
CREATE TABLE if not exists `Operation_Log` (
    `LogID` int AUTO_INCREMENT COMMENT '日志编号，主键，自增',
    `UserID` int NULL COMMENT '操作用户编号，外键，关联 User.UserID',
    `ApiRoute` varchar(255) NULL COMMENT '访问的 API 接口路径',
    `ExecutionTime` int NOT NULL COMMENT '接口执行耗时（毫秒）',
    `StatusCode` int NOT NULL COMMENT 'HTTP状态码（200, 401, 404等）',
    `IpAddress` varchar(100) NULL COMMENT '操作者的 IP 地址',
    `CreateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '具体操作时间',
    PRIMARY KEY (`LogID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- 系统异常日志表
CREATE TABLE if not exists `Error_Log` (
    `LogID` int AUTO_INCREMENT COMMENT '异常日志编号，主键，自增',
    `UserID` int NULL COMMENT '操作用户编号，外键，关联 User.UserID',
    `EndPoint` varchar(255) NULL COMMENT '抛出异常的 API 接口地址',
    `ExceptionMessage` MEDIUMTEXT NOT NULL COMMENT '简短的错误摘要信息',
    `StackTrace` MEDIUMTEXT NOT NULL COMMENT '详细的异常堆栈调用详情',
    `ErrorTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '异常发生时间',
    PRIMARY KEY (`LogID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统异常日志表结构';

-- Token使用日志表
CREATE TABLE if not exists `Token_Usage_Log` (
    `LogID` int AUTO_INCREMENT COMMENT 'Token使用日志编号，主键，自增',
    `UserID` int NULL COMMENT '用户编号，外键，关联 User.UserID',
    `ModelName` varchar(100) not null comment '模型名称',
    `ResponseID` varchar(128) not null comment '回答的唯一标识，即响应请求体的id字段',
    `Type` varchar(100) not NULL COMMENT '业务类型',
    `PromptTokens` int not null comment '输入Token',
    `CachedTokens` int not null comment '缓存命中的Token',
    `CompletionTokens` int not null comment '输出Token',
    `TotalTokens` int not null comment '总Token',
    `CreateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录产生时间',
    PRIMARY KEY (`LogID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Token使用日志表结构';

