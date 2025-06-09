package com.yixuan.yh.commom.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result<E> {
    private Integer code; // 响应码(1：成功、 0：失败)
    private String msg; // 响应信息
    private E data; // 返回的数据

    public static <E> Result<E> success() {

        return new Result<E>(1, "success", null);
    }

    public static <E> Result<E> success(E data) {

        return new Result<E>(1, "success", data);
    }

    public static <E> Result<E> successWithMsg(E data, String msg) {
        return new Result<>(1, msg, data);
    }

    public static <E> Result<E> successWithMsg(String msg) {
        return new Result<>(1, msg, null);
    }

    public static <E> Result<E> error() {
        return new Result<E>(0, "error", null);
    }

    public static <E> Result<E> error(String msg) {
        return new Result<E>(0, msg, null);
    }

    public boolean isError() {
        return code.equals(0);
    }
}
