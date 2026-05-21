import json
import textwrap
from openai import OpenAI, AsyncOpenAI

from client.java_client import set_redis
from core.config import settings

from enums.process_status import ProcessStatus
from schemas.task_assign_dto import TaskAssignResponse, TaskAssignMsg

# 系统角色定义
role_setting = textwrap.dedent("""
         请严格按照要求输出格式正确且内容合法的JSON数据，
         不能有任何前导语和后缀，只能是标准的JSON格式，
         具体要求见用户prompt与Function Calling定义
         """).strip()

# Prompt模板
prompt_format = textwrap.dedent("""
        你是一名信息系统分析师、软件架构师、资深的项目经理
        请你根据以下信息，将该软件开发项目拆解成若干个单人执行的任务，辅助我们公司的项目经理在项目管理系统中完成对指定项目的任务拆解工作
        需要的信息是：任务名称、任务描述、该任务所需技术栈（这里你直接列举可行、常见、推荐的即可）、预估总工时（应填写大于0的整数）
        结果生成格式详见Function Calling的定义
        再次提醒，一条任务只能由一个人完成，若需要多人完成相似任务，请将其拆解为相应数量的描述信息不完全相同的任务
        以下是你需要进行任务拆解的项目信息：
        项目名称：{0}
        项目描述：{1}
        
        {2}
        
        请认真消化以上信息，并给出合理、可靠的决策
        """).strip()

# 定义tools，约束json
tools = [
            {
                "type": "function",
                "function": {
                    "name": "task_assign",
                    "description": "根据项目名称、项目描述等信息，将其拆解为若干条任务",
                    "parameters": {
                        "type": "object",
                        "properties": {
                            "tasks": {
                                "type": "array",
                                "items": {
                                    "type": "object",
                                    "properties": {
                                        "task_name": {"type": "string", "description": "任务名称"},
                                        "task_description": {"type": "string", "description": "任务描述"},
                                        "estimated_hours": {"type": "integer", "description": "任务预估工时，应填写大于0的整数"},
                                        "skill_analysis": {"type": "string", "description": "任务需要的技术栈，这里你直接列举可行、常见、推荐的即可"}
                                    },
                                    "required": ["task_name", "task_description", "estimated_hours"]
                                },
                                "description": "注意，一条任务只能由一个人承担，若相似任务需要多人完成，可以拆解成相应数量且描述信息不完全的任务"
                            }
                        },
                        "required": ["tasks"]
                    }
                }
            }
        ]

# 强制调用tool
tool_choice = {
            "type": "function",
            "function": { "name": "task_assign" }
        }


client = AsyncOpenAI(
    api_key = settings.qwen_api_key,
    base_url = settings.qwen_api_url,
)

# 执行AI任务拆解模块
async def execute_task_assign(msg: dict):
    # 转换类型
    dto: TaskAssignMsg = TaskAssignMsg(**msg)
    try:
        user_prompt:str = ""
        # 补充项目经理的个性化需求
        if dto.user_prompt is not None and dto.user_prompt != "":
            user_prompt = f"补充信息：\n这里是项目经理输入的个性化需求：{dto.user_prompt}"

        # 拼接完整Prompt
        prompt = prompt_format.format(dto.project_name, dto.project_description, user_prompt)

        # 构造请求体
        request = await client.chat.completions.create(
            model = "qwen-plus",
            messages = [
                {"role": "system", "content": role_setting},
                {"role": "user", "content": prompt}
            ],
            tools = tools,
            tool_choice = tool_choice
        )
        # 反序列化结果并获取
        reply = json.loads(request.choices[0].message.tool_calls[0].function.arguments)["tasks"]
        reply = [TaskAssignResponse(**item) for item in reply]

    except Exception as e:
        # Redis写入失败态
        await set_redis(dto.redis_key, 60, ProcessStatus.Fail.value, None)
        raise Exception("AI任务拆解服务失败", e)

    # 校验失败
    if check_result(reply) == False:
        # Redis写入失败态
        await set_redis(dto.redis_key, 60, ProcessStatus.Fail.value, None)
        raise Exception("格式校验失败")

    # Redis写入成功态及其数据
    reply = [item.model_dump(by_alias=True) for item in reply]     # 转换为小驼峰
    await set_redis(dto.redis_key, dto.redis_ttl, ProcessStatus.Success.value, reply)

# 校验数据是否合法
def check_result(result : list[TaskAssignResponse]) -> bool:
    if result is None:
        return False

    for item in result:
        # 参数不合法
        if item.task_name is None or item.task_description is None\
            or item.estimated_hours is None or item.estimated_hours <= 0:
            return False

    return True


