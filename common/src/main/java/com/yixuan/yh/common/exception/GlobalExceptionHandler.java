package com.yixuan.yh.common.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yixuan.yh.common.response.Result;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ObjectMapper objectMapper;

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(YHClientException.class)
    public Result<Void> ex(YHClientException e) {
        log.warn("业务异常 | Code: {} | UserMsg: {} | Detail: {}",
                e.getErrorCode() != null ? e.getErrorCode().getCode() : "unknown",
                e.getErrorCode() != null ? e.getErrorCode().getMsg() : "unknown",
                e.getLogMessage());

        return Result.error(e.getErrorCode() != null ? e.getErrorCode().getMsg() : e.getLogMessage()); // 兼容旧异常类
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(YHServerException.class)
    public Result<Void> ex(YHServerException e) {
        log.error("系统异常 | Code: {} | UserMsg: {} | Detail: {}",
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

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<Result<Void>> ex(FeignException e) {
        int status = e.status();

        String url = e.request() != null ? e.request().url() : "unknown-url";
        String method = e.request() != null ? e.request().httpMethod().name() : "unknown-method";

        String errorMsg;
        try {
            Result<?> responseBody = objectMapper.readValue(e.contentUTF8(), Result.class);
            errorMsg = responseBody.getMsg();
        } catch (Exception exception) {
            errorMsg = e.contentUTF8();
        }

        if (status >= 400 && status < 500) {
            log.warn("Feign远程调用业务异常 | Method: {} | URL: {} | Status: {} | Body: {}",
                    method, url, status, errorMsg);
        } else if (status >= 500 && status < 600) {
            log.error("Feign远程调用系统异常 | Method: {} | URL: {} | Status: {} | Body: {}",
                    method, url, status, errorMsg, e);
        } else {
            status = 500;
            errorMsg = "服务器异常";
            log.error("Feign远程调用系统异常 | Method: {} | URL: {} | Status: {} | Body: {}",
                    method, url, status, errorMsg, e);
        }

        return ResponseEntity.status(status).body(Result.error(errorMsg));
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class) // 捕获所有异常
    public Result<Void> ex(Exception e) {
        log.error("系统未知异常", e);

        return Result.error(e.getMessage());
    }

}
