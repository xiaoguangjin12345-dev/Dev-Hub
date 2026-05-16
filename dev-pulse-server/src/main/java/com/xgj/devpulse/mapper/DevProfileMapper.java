package com.xgj.devpulse.mapper;

import org.apache.ibatis.annotations.*;

@Mapper
public interface DevProfileMapper {

    // 添加开发人员简历（注册时用）
    @Insert("INSERT INTO `Dev_Profile`(`UserID`, `IsFirst`) values (#{userId}, 1)")
    int insertDevProfile(@Param("userId") int userId);

    // 查询用户首次登录状态
    @Select("select `IsFirst` from `Dev_Profile` where `UserID` = #{userId}")
    Byte getDevFirstLoginState(@Param("userId") int userId);

    // 更改开发人员首次登录状态
    @Update("""
            update `Dev_Profile`
            set `IsFirst` = 2
            where `UserID` = #{userId}
            """)
    int updateDevFirstLoginState(@Param("userId") int userId);

    // 更新开发人员简历
    // 按照当前前端版本，对技能标签执行动态更新，具体见xml
    int updateDevProfile(@Param("userId") int userId,
                         @Param("resumeText") String resumeText,
                         @Param("skills") String skills);
}
