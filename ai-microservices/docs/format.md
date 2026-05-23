# OpenAI请求与响应格式大观
> 请求体的构造可以直接从这粘贴并修改


## 1. 普通问答
### 请求
```json
{
  "model": "gpt-3.5-turbo",
  "messages": [
    {
      "role": "system",
      "content": "You are a helpful assistant."
    },
    {
      "role": "user",
      "content": "Hello!"
    }
  ],
  // 以下可选可省略
  "temperature": 0.7,
  "max_tokens": 150,
  "top_p": 1,
  "frequency_penalty": 0,
  "presence_penalty": 0
}
```
### 响应
```json
{
  "id": "chatcmpl-123",    // Token日志使用
  "object": "chat.completion",
  "created": 1677652288,
  "model": "gpt-3.5-turbo-0613",
  "choices": [
    {
      "index": 0,
      "message": {
        "role": "assistant",
        "content": "Hello! How can I help you today?"
      },
      "finish_reason": "stop"
    }
  ],
  // Token日志核心数据
  "usage": {
    "prompt_tokens": 9,
    "completion_tokens": 12,
    "total_tokens": 21,
    "prompt_tokens_details": {
      "cached_tokens": 5
    }
  }
}
```

## 2. Tool Calls
### 请求
```json
{
  "model": "gpt-4o",
  "messages": [
    {
      "role": "system",
      "content": "You are a helpful assistant that can use tools."
    },
    {
      "role": "user",
      "content": "上海今天天气怎么样？"
    }
  ],
  "tools": [
    {
      "type": "function",
      "function": {
        "name": "get_weather",
        "description": "获取指定城市的天气信息",
        "parameters": {
          "type": "object",
          "properties": {
            "city": {
              "type": "string",
              "description": "城市名称，如：北京、上海"
            },
            "unit": {
              "type": "string",
              "enum": ["celsius", "fahrenheit"],
              "description": "温度单位"
            }
          },
          "required": ["city"]
        }
      }
    },
    {
      "type": "function",
      "function": {
        "name": "get_current_time",
        "description": "获取当前时间",
        "parameters": {
          "type": "object",
          "properties": {
            "timezone": {
              "type": "string",
              "description": "时区，如：Asia/Shanghai"
            }
          }
        }
      }
    }
  ],
  "tool_choice": "auto"    // 最好强制指定工具调用
}
```

### 强制指定工具调用
```json
// 指定特定的一个
{
  "tool_choice": {
    "type": "function",
    "function": { "name": "get_weather" }
  }
}

// 强制指定至少用一个工具
{
  "tool_choice": "required"
}
```

### 响应 (Tool Calling中间结果)
```json
{
  "id": "chatcmpl-123",
  "object": "chat.completion",
  "created": 1677652288,
  "model": "gpt-4o",
  "choices": [
    {
      "index": 0,
      "message": {
        "role": "assistant",
        "content": null,
        "tool_calls": [
          {
            "id": "call_abc123",
            "type": "function",
            "function": {
              "name": "get_weather",
              "arguments": "{\"city\": \"上海\", \"unit\": \"celsius\"}"
            }
          }
        ]
      },
      "finish_reason": "tool_calls"
    }
  ],
  "usage": {
    "prompt_tokens": 120,
    "completion_tokens": 45,
    "total_tokens": 165,
    "prompt_tokens_details": {
      "cached_tokens": 40
    }
  }
}
```