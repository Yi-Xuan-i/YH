package com.yixuan.yh.common.exception;

public class YHServerException extends RuntimeException {

    public YHServerException() {
    }

    public YHServerException(String message) {
        super(message);
    }

    public YHServerException(String message, Throwable cause) {
        super(message, cause);
    }

    public YHServerException(Throwable cause) {
        super(cause);
    }
}