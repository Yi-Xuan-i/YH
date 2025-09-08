package com.yixuan.yh.common.exception;

public class YHClientException extends RuntimeException {

    public YHClientException() {
    }

    public YHClientException(String message) {
        super(message);
    }

    public YHClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public YHClientException(Throwable cause) {
        super(cause);
    }
}
