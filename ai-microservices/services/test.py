import json
from openai import OpenAI


API_KEY = "--------------------------"
BASE_URL = "https://dashscope.aliyuncs.com/compatible-mode/v1"


client = OpenAI(
    api_key = API_KEY,
    base_url = BASE_URL
)

def analysis_meaning(prompt):
    request = client.chat.completions.create(
        model = "qwen-turbo",
        messages = [
            {"role": "system", "content": "你善于分类, 请判断用户请求的类型"},
            {"role": "user", "content": prompt}
        ],
        tools = [
            {
                "type": "function",
                "function": {
                    "name": "analysis_meaning",
                    "description": "你是意图分类专家。分析用户请求，输出三个概率值，总和必须为1。",
                    "parameters": {
                        "type": "object",
                        "properties": {
                            "query": {"type": "number",
                                      "description":"查询请求，概率值, 范围0-1的浮点数",
                                      "minimum": 0,
                                      "maximum": 1,
                                      "multipleOf": 0.01
                                      },
                            "chat": {"type": "number",
                                      "description":"对话请求，概率值, 范围0-1的浮点数",
                                      "minimum": 0,
                                      "maximum": 1,
                                      "multipleOf": 0.01
                                      },
                            "unknown": {"type": "number",
                                      "description":"未知请求，概率值, 范围0-1的浮点数",
                                      "minimum": 0,
                                      "maximum": 1,
                                      "multipleOf": 0.01
                                      },
                        },
                        "required": ["query", "chat", "unknown"]
                    }
                }
            }
        ],
        tool_choice={
            "type": "function",
            "function": {"name": "analysis_meaning"}
        }

    )

    reply = json.loads(request.choices[0].message.tool_calls[0].function.arguments)

    return reply


def analysis_according_to_data(data, prompt):
    json_data = json.dumps(data)
    request = client.chat.completions.create(
        model="qwen-plus",
        messages=[
            {"role": "system", "content": "请你根据数据说话，并根据用户的原始请求，给出有价值的结论"},
            {"role": "user", "content": "用户原始请求:" + prompt},
            {"role": "user", "content": "数据获取结果:" + json_data}
        ]
    )
    reply = request.choices[0].message.content
    return reply

def execute_query():
    return [
        {
            "dev_id": "D001",
            "name": "张三",
            "work_efficiency": 0.92,
            "quality_score": 88.5,
            "performance_score": 90.2
        },
        {
            "dev_id": "D002",
            "name": "李四",
            "work_efficiency": 0.85,
            "quality_score": 92.0,
            "performance_score": 88.5
        },
        {
            "dev_id": "D003",
            "name": "王五",
            "work_efficiency": 0.96,
            "quality_score": 85.5,
            "performance_score": 90.8
        },
        {
            "dev_id": "D004",
            "name": "赵六",
            "work_efficiency": 0.78,
            "quality_score": 90.0,
            "performance_score": 84.0
        },
        {
            "dev_id": "D005",
            "name": "孙七",
            "work_efficiency": 0.98,
            "quality_score": 95.5,
            "performance_score": 96.7
        },
        {
            "dev_id": "D006",
            "name": "周八",
            "work_efficiency": 0.88,
            "quality_score": 87.0,
            "performance_score": 87.5
        },
        {
            "dev_id": "D007",
            "name": "吴九",
            "work_efficiency": 0.94,
            "quality_score": 91.5,
            "performance_score": 92.8
        },
        {
            "dev_id": "D008",
            "name": "郑十",
            "work_efficiency": 0.75,
            "quality_score": 82.0,
            "performance_score": 78.5
        },
        {
            "dev_id": "D009",
            "name": "陈十一",
            "work_efficiency": 0.91,
            "quality_score": 89.5,
            "performance_score": 90.3
        },
        {
            "dev_id": "D010",
            "name": "林十二",
            "work_efficiency": 0.95,
            "quality_score": 93.0,
            "performance_score": 94.0
        }
    ]

if __name__ == "__main__":
    prompt = "查询员工绩效并分析谁最牛"

    result = analysis_meaning(prompt)
    print(result)

    # 比较value，并获取value值最大的key
    max_key = max(result, key=result.get)
    print(max_key)

    if max_key == "query":
        data = execute_query()
        result = analysis_according_to_data(data, prompt)
        print(result)
