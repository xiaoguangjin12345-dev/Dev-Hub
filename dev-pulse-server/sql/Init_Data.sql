use dev_pulse;

-- 添加技能标签基础数据
INSERT INTO `dict_tag` (`TagName`) VALUES 
-- 后端开发
('Java'), ('Spring Boot'), 
('Docker'), ('Linux'), ('Node.js'), ('Go'), ('Python'), 
('C++'), ('.NET Core'), ('Nginx'),

-- 前端开发
('Vue.js'), ('React'), ('TypeScript'), ('JavaScript'), ('HTML5'), ('CSS3'), ('移动端开发'),

-- 数据库
('MySQL'), ('Redis'), ('SQL Server'), ('PostgreSQL'), ('MongoDB'), ('Oracle'),

-- 算法与AI
('机器学习'), ('深度学习'), ('PyTorch'), ('计算机视觉'), ('数据分析'), ('Agent'), ('LLM'), ('AI应用开发'),

-- 项目管理与文档
('项目管理'),

-- 新兴方向
('物联网'), ('信息安全'),

-- 软件工程
('系统架构设计');