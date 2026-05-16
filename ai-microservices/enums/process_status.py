from enum import Enum

# Redis状态枚举
class ProcessStatus(Enum):
    Pending = 1      # 待处理
    Success = 2      # 成功
    Fail = 3         # 失败