package com.yixuan.yh.common.exception;

public class YHServerException extends RuntimeException {

    private final IErrorCode errorCode;
    private final String logMessage;

    /**
     * 只有硬编码信息（保留主要是为了兼容旧异常类）
     */
    public YHServerException(String message) {
        super(message);
        this.errorCode = null;
        this.logMessage = message;
    }

    /**
     * 包装异常类（保留主要是为了兼容旧异常类）
     */
    public YHServerException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = null;
        this.logMessage = message;
    }

    /**
     * 只有枚举，没有动态详细信息的构造函数
     */
    public YHServerException(IErrorCode errorCode) {
        super(errorCode.getMsg());
        this.errorCode = errorCode;
        this.logMessage = errorCode.getMsg();
    }

    /**
     * 最常用的构造函数：包含错误枚举和动态详细原因
     * @param errorCode 错误码枚举（含给用户看的简短文案）
     * @param logMessageFormat 给开发看的详细信息模板（支持占位符）
     * @param args 动态参数
     */
    public YHServerException(IErrorCode errorCode, String logMessageFormat, Object... args) {
        super(String.format(logMessageFormat, args));
        this.errorCode = errorCode;
        this.logMessage = String.format(logMessageFormat, args);
    }

    /**
     * 包装第三方异常或系统异常
     */
    public YHServerException(IErrorCode errorCode, Throwable cause, String logMessageFormat, Object... args) {
        super(String.format(logMessageFormat, args), cause);
        this.errorCode = errorCode;
        this.logMessage = String.format(logMessageFormat, args);
    }

    public IErrorCode getErrorCode() {
        return errorCode;
    }

    public String getLogMessage() {
        return logMessage;
    }
}