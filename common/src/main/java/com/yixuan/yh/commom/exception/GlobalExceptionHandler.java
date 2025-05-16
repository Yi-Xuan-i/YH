package com.yixuan.yh.commom.exception;

import com.yixuan.yh.commom.response.Result;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadRequestException.class)
    public Result<String> ex(BadRequestException ex) {
        ex.printStackTrace();
        return Result.error(ex.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<String> ex(MethodArgumentNotValidException ex) {
        ex.printStackTrace();
        BindingResult bindingResult = ex.getBindingResult();
        // 解析异常信息
        List<String> errors = new ArrayList<>();
        bindingResult.getFieldErrors().forEach(item -> {
            errors.add(item.getDefaultMessage());
        });
        // 错误信息拼接
        String errorMessage = String.join("，", errors);
        // 返回错误信息
        return Result.error(errorMessage);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class) // 捕获所有异常
    public Result<String> ex(Exception ex) {
        ex.printStackTrace();
        return Result.error(ex.getMessage());
    }

}
