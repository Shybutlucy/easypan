package com.swx.common.handler;

import com.swx.common.pojo.BizException;
import com.swx.common.pojo.ErrorResult;
import com.swx.common.pojo.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
/**
 * 全局异常处理类
 * @RestControllerAdvice(@ControllerAdvice)，拦截异常并统一处理
 * @author sw-code
 *
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理自定义的业务异常
     * @param e	异常对象
     * @param request	request
     * @return	错误结果
     */
    @ExceptionHandler(BizException.class)
    public ErrorResult bizExceptionHandler(BizException e, HttpServletRequest request) {
        log.error("发生业务异常！原因是: {}", e.getMessage());
        return ErrorResult.fail(e.getCode(), e.getMessage());
    }

    // 拦截抛出的异常，@ResponseStatus：用来改变响应状态码
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Throwable.class)
    public ErrorResult handlerThrowable(Throwable e, HttpServletRequest request) {
        log.error("发生未知异常！原因是: ", e);
        return ErrorResult.fail(ResultCode.SYSTEM_ERROR, e);
    }

    // 参数校验异常
    @ExceptionHandler(BindException.class)
    public ErrorResult handleBindException(BindException e, HttpServletRequest request) {
        log.error("发生参数校验异常！原因是：",e);
        return ErrorResult.fail(ResultCode.PARAM_IS_INVALID, e, e.getAllErrors().get(0).getDefaultMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResult handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        log.error("发生参数校验异常！原因是：",e);
        return ErrorResult.fail(ResultCode.PARAM_IS_INVALID,e,e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ErrorResult handleConstraintViolationException(ConstraintViolationException e, HttpServletRequest request) {
        log.error("发生参数校验异常！原因是：",e);
        return ErrorResult.fail(ResultCode.PARAM_IS_INVALID,e,e.getMessage());
    }
}

