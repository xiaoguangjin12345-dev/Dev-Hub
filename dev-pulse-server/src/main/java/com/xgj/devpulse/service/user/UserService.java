package com.xgj.devpulse.service.user;

import com.xgj.devpulse.pojo.dto.notice.NoticeApproveDTO;
import com.xgj.devpulse.pojo.dto.user.UserProfileUpdateDTO;
import com.xgj.devpulse.pojo.dto.user.UserQueryDTO;
import com.xgj.devpulse.pojo.vo.common.PageResultVO;
import com.xgj.devpulse.pojo.vo.user.UserDetailsVO;
import com.xgj.devpulse.pojo.vo.user.UserListVO;

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
