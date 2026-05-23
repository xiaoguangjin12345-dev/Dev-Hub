from pydantic import BaseModel, ConfigDict
from pydantic.alias_generators import to_camel

# 后端的消息
class TaskAssignMsg(BaseModel):
    model_config = ConfigDict(
        alias_generator = to_camel,
        populate_by_name = True     # 允许通过小驼峰名称从JSON中取值
    )
    redis_key: str
    redis_ttl: int
    current_user_id: int
    project_name: str
    project_description: str
    user_prompt: str

# AI任务拆解的单个条目
class TaskAssignResponse(BaseModel):
    # 允许自动转换
    model_config = ConfigDict(
        alias_generator = to_camel,
        populate_by_name = True       # 允许通过小驼峰名称从JSON中取值
    )
    task_name: str
    task_description: str
    estimated_hours: int
    skill_analysis: str

