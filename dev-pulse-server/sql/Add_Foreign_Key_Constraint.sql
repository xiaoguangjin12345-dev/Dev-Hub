use dev_pulse;

-- 开发人员简历表
ALTER TABLE `Dev_Profile` 
ADD CONSTRAINT `fk_profile_user` FOREIGN KEY (`UserID`) REFERENCES `User` (`UserID`) ON DELETE CASCADE;

-- 项目表
ALTER TABLE `Project` 
ADD CONSTRAINT `fk_project_pm` FOREIGN KEY (`PMID`) REFERENCES `User` (`UserID`);

-- 项目审批记录表
ALTER TABLE `Project_Approval`
ADD CONSTRAINT `fk_projectappr_project` FOREIGN KEY (`ProjectID`) REFERENCES `Project` (`ProjectID`) ON DELETE CASCADE,
ADD CONSTRAINT `fk_projectappr_pmo` FOREIGN KEY (`PMOID`) REFERENCES `User` (`UserID`);

-- 任务表
ALTER TABLE `Task`
ADD CONSTRAINT `fk_task_project` FOREIGN KEY (`ProjectID`) REFERENCES `Project` (`ProjectID`) ON DELETE CASCADE,
ADD CONSTRAINT `fk_task_dev` FOREIGN KEY (`DevID`) REFERENCES `User` (`UserID`);

-- 任务申请记录表
ALTER TABLE `Task_Application`
ADD CONSTRAINT `fk_taskapp_task` FOREIGN KEY (`TaskID`) REFERENCES `Task` (`TaskID`) ON DELETE CASCADE,
ADD CONSTRAINT `fk_taskapp_pm` FOREIGN KEY (`PMID`) REFERENCES `User` (`UserID`),
ADD CONSTRAINT `fk_taskapp_dev` FOREIGN KEY (`DevID`) REFERENCES `User` (`UserID`);

-- 任务实际工时记录表
ALTER TABLE `Work_Log`
ADD CONSTRAINT `fk_worklog_task` FOREIGN KEY (`TaskID`) REFERENCES `Task` (`TaskID`) ON DELETE CASCADE,
ADD CONSTRAINT `fk_worklog_user` FOREIGN KEY (`UserID`) REFERENCES `User` (`UserID`);

-- 任务预估工时修改记录表
ALTER TABLE `Task_Change_Log`
ADD CONSTRAINT `fk_changelog_task` FOREIGN KEY (`TaskID`) REFERENCES `Task` (`TaskID`) ON DELETE CASCADE,
ADD CONSTRAINT `fk_changelog_project` FOREIGN KEY (`ProjectID`) REFERENCES `Project` (`ProjectID`) ON DELETE CASCADE,
ADD CONSTRAINT `fk_changelog_pm` FOREIGN KEY (`PMID`) REFERENCES `User` (`UserID`);

-- 任务评审记录表
ALTER TABLE `Task_Review`
ADD CONSTRAINT `fk_review_task` FOREIGN KEY (`TaskID`) REFERENCES `Task` (`TaskID`) ON DELETE CASCADE,
ADD CONSTRAINT `fk_review_dev` FOREIGN KEY (`DevID`) REFERENCES `User` (`UserID`),
ADD CONSTRAINT `fk_review_pm` FOREIGN KEY (`PMID`) REFERENCES `User` (`UserID`);

-- 任务级绩效记录表
ALTER TABLE `Task_Performance`
ADD CONSTRAINT `fk_taskperf_task` FOREIGN KEY (`TaskID`) REFERENCES `Task` (`TaskID`) ON DELETE SET NULL,
ADD CONSTRAINT `fk_taskperf_pm` FOREIGN KEY (`PMID`) REFERENCES `User` (`UserID`),
ADD CONSTRAINT `fk_taskperf_dev` FOREIGN KEY (`DevID`) REFERENCES `User` (`UserID`);

-- 项目级绩效记录表
ALTER TABLE `Project_Performance`
ADD CONSTRAINT `fk_projperf_project` FOREIGN KEY (`ProjectID`) REFERENCES `Project` (`ProjectID`) ON DELETE SET NULL,
ADD CONSTRAINT `fk_projperf_pmo` FOREIGN KEY (`PMOID`) REFERENCES `User` (`UserID`),
ADD CONSTRAINT `fk_projperf_pm` FOREIGN KEY (`PMID`) REFERENCES `User` (`UserID`);

-- 消息通知表
ALTER TABLE `Notice`
ADD CONSTRAINT `fk_notice_receiver` FOREIGN KEY (`RecieverID`) REFERENCES `User` (`UserID`) ON DELETE CASCADE,
ADD CONSTRAINT `fk_notice_sender` FOREIGN KEY (`SenderID`) REFERENCES `User` (`UserID`);

-- 技能标签关联表
ALTER TABLE `Tag_Relation`
ADD CONSTRAINT `fk_tagrel_tag` FOREIGN KEY (`TagID`) REFERENCES `Dict_Tag` (`TagID`);

-- 操作日志表
ALTER TABLE `Operation_Log`
ADD CONSTRAINT `fk_oplog_user` FOREIGN KEY (`UserID`) REFERENCES `User` (`UserID`) ON DELETE SET NULL;

-- 系统异常日志表
ALTER TABLE `Error_Log`
ADD CONSTRAINT `fk_errlog_user` FOREIGN KEY (`UserID`) REFERENCES `User` (`UserID`) ON DELETE SET NULL;

