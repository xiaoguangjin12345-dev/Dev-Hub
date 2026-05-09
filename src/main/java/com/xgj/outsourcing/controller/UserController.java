package com.xgj.outsourcing.controller;

import com.xgj.outsourcing.common.operationlog.annotation.Log;
import com.xgj.outsourcing.pojo.dto.notice.NoticeApproveDTO;
import com.xgj.outsourcing.pojo.dto.user.UserProfileUpdateDTO;
import com.xgj.outsourcing.pojo.dto.user.UserQueryDTO;
import com.xgj.outsourcing.pojo.vo.common.PageResultVO;
import com.xgj.outsourcing.pojo.vo.user.UserDetailsVO;
import com.xgj.outsourcing.pojo.vo.user.UserListVO;
import com.xgj.outsourcing.service.user.UserService;
import com.xgj.outsourcing.common.response.APIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    // 参数化查询用户列表
    @Log("查询用户列表")
    @GetMapping("/")
    public APIResponse<PageResultVO<UserListVO>> getUsers(@ModelAttribute UserQueryDTO dto){
        PageResultVO<UserListVO> result = userService.getUsers(dto);
        return APIResponse.success(result, "用户列表查询成功");
    }

    // 查询指定用户详情
    @Log("查询指定用户")
    @GetMapping("/{userId}")
    public APIResponse<UserDetailsVO> getUserDetails(@PathVariable int userId){
        UserDetailsVO result = userService.getUserDetails(userId);
        return APIResponse.success(result, "指定用户查询成功");
    }

    // 用户修改非关键信息
    @Log("用户修改信息")
    @PutMapping("/profile")
    public APIResponse<Boolean> updateProfile(@RequestBody UserProfileUpdateDTO dto){
        userService.updateUserProfile(dto);
        return APIResponse.success(true, "用户信息修改成功");
    }

    // 管理员审核用户
    @Log("管理员审核用户")
    @PutMapping("/{id}/audit")
    public APIResponse<Boolean> auditUser(@PathVariable int id,
                                         @RequestBody NoticeApproveDTO dto){
        userService.auditUser(id, dto);
        return APIResponse.success(true, "审核成功");
    }
}
