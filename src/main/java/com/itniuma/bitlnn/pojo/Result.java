package com.itniuma.bitlnn.pojo;

import com.itniuma.bitlnn.enums.ResultCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一响应结果
 *
 * @author aceFelix
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result<T> {
    private Integer code;
    private String message;
    private T data;
    private Long timestamp;

    public static <E> Result<E> success(E data) {
        return new Result<>(200, "操作成功", data, System.currentTimeMillis());
    }

    public static <E> Result<E> success(E data, String message) {
        return new Result<>(200, message, data, System.currentTimeMillis());
    }

    public static <E> Result<E> success() {
        return new Result<>(200, "操作成功", null, System.currentTimeMillis());
    }

    public static <E> Result<E> error(Integer code, String message) {
        return new Result<>(code, message, null, System.currentTimeMillis());
    }

    public static <E> Result<E> error(String message) {
        return new Result<>(500, message, null, System.currentTimeMillis());
    }

    public static <E> Result<E> error(ResultCode resultCode) {
        return new Result<>(resultCode.getCode(), resultCode.getMessage(), null, System.currentTimeMillis());
    }
}
