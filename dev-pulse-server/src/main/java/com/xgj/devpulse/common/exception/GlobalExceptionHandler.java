package com.xgj.devpulse.common.exception;

import com.xgj.devpulse.common.context.UserContext;
import com.xgj.devpulse.common.response.APIResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private final AsyncErrorLogService asyncErrorLogService;

    // 参数校验异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public APIResponse<String> handleValid(MethodArgumentNotValidException e, HttpServletRequest request) {
        // 这里不必在数据库添加日志记录
        // 获取参数校验异常提示
        String message = e.getBindingResult().getFieldError().getDefaultMessage();
        if(message == null){
            message = "参数校验失败";
        }
        return APIResponse.fail(400, message);
    }

    // 用户认证异常
    @ExceptionHandler(AuthenticationException.class)
    public APIResponse<String> handleAuthentication(AuthenticationException e, HttpServletRequest request) {
        // 这里不必在数据库添加日志记录
        return APIResponse.fail(e.getCode(), e.getMessage());
    }

    // 业务异常
    @ExceptionHandler(BusinessException.class)
    public APIResponse<String> handleBusiness(BusinessException e, HttpServletRequest request) {
        // 需要记录日志时，再添加记录
        if(e.isLog()){
            saveErrorLog(e, "业务异常", request);
        }
        return APIResponse.fail(e.getCode(), e.getMessage());
    }

    // 已认证用户的权限类异常
    @ExceptionHandler(AuthorizationException.class)
    public APIResponse<String> handleAuthorization(AuthorizationException e, HttpServletRequest request) {
        // 添加记录
        saveErrorLog(e, "权限异常", request);
        return APIResponse.fail(e.getCode(), e.getMessage());
    }

    // 运行时异常
    @ExceptionHandler(RuntimeException.class)
    public APIResponse<String> handleRuntime(RuntimeException e, HttpServletRequest request) {
        // 添加记录
        saveErrorLog(e, "运行时异常", request);
        return APIResponse.fail(500, e.getMessage());
    }

    // 全局异常
    @ExceptionHandler(Exception.class)
    public APIResponse<String> handleException(Exception e, HttpServletRequest request) {
        // 添加记录
        saveErrorLog(e, "系统异常", request);
        return APIResponse.fail(500, "系统异常");
    }

    // 保存异常日志
    public void saveErrorLog(Throwable e, String type, HttpServletRequest request) {
        // 获取用户编号（未登录状态下，编号为0，需填入空值）
        Integer userId = UserContext.getCurrentUserId();
        if(userId == 0){
            userId = null;
        }
        // 获取异常接口
        String endPoint = request.getRequestURI();
        // 构造异常信息
        String msg = type;
        if(e.getMessage() != null){
            msg = msg + ": \n" + e.getMessage();
        }else{
            msg = msg + ": \n" + e.getClass().getSimpleName();
        }
        // 使用commons-lang3的工具，将堆栈信息转为字符串
        String stackTrace = ExceptionUtils.getStackTrace(e);

        // 异步添加异常日志记录
        asyncErrorLogService.saveErrorLogAsync(userId, endPoint, msg, stackTrace, LocalDateTime.now());

    }

}
