package com.yixuan.yh.common.exception;

import com.yixuan.yh.common.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(YHClientException.class)
    public Result<Void> ex(YHClientException e) {
        log.info("业务异常: [Code: {}] [UserMsg: {}] [Detail: {}]",
                e.getErrorCode().getCode(),
                e.getErrorCode().getMsg(),
                e.getLogMessage());

        return Result.error(e.getErrorCode() != null ? e.getErrorCode().getMsg() : e.getLogMessage()); // 兼容旧异常类
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(YHServerException.class)
    public Result<Void> ex(YHServerException e) {
        log.error("系统异常: [Code: {}] [UserMsg: {}] [Detail: {}]",
                e.getErrorCode().getCode(),
                e.getErrorCode().getMsg(),
                e.getLogMessage(),
                e);

        return Result.error(e.getErrorCode() != null ? e.getErrorCode().getMsg() : e.getLogMessage()); // 兼容旧异常类
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> ex(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        List<String> errors = new ArrayList<>();
        bindingResult.getFieldErrors().forEach(item -> {
            errors.add(item.getDefaultMessage());
        });
        String errorMessage = String.join("，", errors);

        return Result.error(errorMessage);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class) // 捕获所有异常
    public Result<Void> ex(Exception e) {
        log.error("系统未知异常", e);

        return Result.error(e.getMessage());
    }

}
