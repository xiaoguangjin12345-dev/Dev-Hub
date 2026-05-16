from pydantic_settings import BaseSettings, SettingsConfigDict

class Settings(BaseSettings):
    # 自动从环境变量或.env寻找同名变量，忽略大小写

    # RabbitMQ 配置
    rabbitmq_host: str = "localhost"
    rabbitmq_port: int = 5672
    rabbitmq_user: str = "guest"
    rabbitmq_password: str = "guest"

    # Java后端配置
    java_client_url: str = "http://localhost:8080/v2/sdk/ai/redis"

    # API配置
    deepseek_api_url: str = "https://api.deepseek.com/v1"
    deepseek_api_key: str = ""
    qwen_api_url: str = "https://dashscope.aliyuncs.com/compatible-mode/v1"
    qwen_api_key: str = ""

    # 读取.env文件设置
    model_config = SettingsConfigDict(
        env_file=".env",
        env_file_encoding='utf-8',
        extra='ignore'
    )

settings = Settings()