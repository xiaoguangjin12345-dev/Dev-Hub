package com.xgj.devpulse.mapper;

import com.xgj.devpulse.pojo.dto.auth.RegisterRequestDTO;
import com.xgj.devpulse.pojo.dto.user.UserQueryDTO;
import com.xgj.devpulse.pojo.entity.UserEntity;
import com.xgj.devpulse.pojo.vo.common.SelectOptionVO;
import com.xgj.devpulse.pojo.vo.user.UserDetailsVO;
import com.xgj.devpulse.pojo.vo.user.UserListVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface UserMapper {

    // 添加用户（注册）
    int insertUser(@Param("dto") RegisterRequestDTO dto,
                   @Param("createTime") LocalDateTime createTime);


    // 修改用户非关键信息（邮箱、电话）
    // 按照当前前端版本，执行全量更新
    @Update("""
            update `User`
            set `Email` = #{email}, `Phone` = #{phone}
            where `UserID` = #{userId}
            """)
    int updateUser(@Param("userId") int userId,
                   @Param("email") String email,
                   @Param("phone") String phone);

    // 修改用户审核状态
    @Update("""
            update `User`
            set `Status` = #{status}
            where `UserID` = #{userId}
            and `Status` = 1
            """)
    int updateAuditResult(@Param("userId") int userId,
                          @Param("status") Byte status);



    // 多条件查询用户（分页）
    List<UserListVO> getUsersByQuery(@Param("dto") UserQueryDTO dto,
                                     @Param("offset") Integer offset,
                                     @Param("size") Integer size);
    // 多条件查询用户总数
    long countUsersByQuery(@Param("dto") UserQueryDTO dto);



    // 根据用户编号查询详情
    UserDetailsVO getUserById(@Param("userId") int userId);


    // 根据用户名查询用户（登录、注册校验）
    @Select("select * from `User` where `Username` = #{userName}")
    UserEntity getUserByUserName(@Param("userName") String userName);

    // 根据用户编号查询真实姓名
    @Select("select `RealName` from `User` where `UserID` = #{userId}")
    String getNameById(@Param("userId") int userId);

    // 根据用户编号，获取用户实体（用于内部判断）
    @Select("select * from `User` where `UserID` = #{userId}")
    UserEntity getUserEntity(@Param("userId") Integer userId);

    // 获取项目经理下拉框选项
    List<SelectOptionVO<Integer>> getPMSelectOptions(@Param("userId") Integer userId,
                                                     @Param("role") Byte role);

    // 获取开发人员下拉框选项
    List<SelectOptionVO<Integer>> getDevSelectOptions(@Param("userId") Integer userId,
                                                      @Param("role") Byte role);

}
