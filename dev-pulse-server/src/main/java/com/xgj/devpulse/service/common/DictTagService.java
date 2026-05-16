package com.xgj.devpulse.service.common;

import java.util.List;

public interface DictTagService {

    // 设置标签
    boolean setDictTag(byte targetType, int targetId, List<Byte> skills);

    // 获取标签字符串
    String getDictTagName(int targetType, int targetId);

}
