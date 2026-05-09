package com.xgj.outsourcing.service.user;

import com.xgj.outsourcing.pojo.dto.notice.NoticeApproveDTO;
import com.xgj.outsourcing.pojo.dto.user.UserProfileUpdateDTO;
import com.xgj.outsourcing.pojo.dto.user.UserQueryDTO;
import com.xgj.outsourcing.pojo.vo.common.PageResultVO;
import com.xgj.outsourcing.pojo.vo.user.UserDetailsVO;
import com.xgj.outsourcing.pojo.vo.user.UserListVO;

import java.util.List;

public interface UserService {
    // 参数化查询用户列表
    PageResultVO<UserListVO> getUsers(UserQueryDTO dto);
    // 查询指定用户详情
    UserDetailsVO getUserDetails(int userId);
    // 用户修改非关键信息
    boolean updateUserProfile(UserProfileUpdateDTO dto);
    // 管理员审核用户
    boolean auditUser(int targetUserId, NoticeApproveDTO dto);

}
