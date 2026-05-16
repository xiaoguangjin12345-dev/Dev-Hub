package com.xgj.devpulse.service.common;

import com.xgj.devpulse.mapper.DictTagMapper;
import com.xgj.devpulse.mapper.TagRelationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DictTagServiceImpl implements DictTagService {
    private final DictTagMapper dictTagMapper;
    private final TagRelationMapper tagRelationMapper;

    // 设置标签
    @Transactional              // 预留事务（虽然现有用法都是被其他模块调用，并直接加入调用方的事务）
    public boolean setDictTag(byte targetType, int targetId, List<Byte> skills){
        if(skills != null && skills.size() > 0){
            // 删除原有标签
            tagRelationMapper.deleteByTargetID(targetType, targetId);
            // 设置新标签
            tagRelationMapper.insertTagRelation(targetType, targetId, skills);
        }
        return true;
    }

    // 获取标签字符串
    public String getDictTagName(int targetType, int targetId){
        // 根据用户（开发人员）或任务查询技能标签名称
        List<String> skills = dictTagMapper.getTagNames(targetType, targetId);

        if(skills != null && skills.size() > 0) {
            // 字符串分隔
            return String.join(",", skills);
        }else{
            return "";
        }
    }

}
