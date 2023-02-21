package com.cpirh.common.response;

import lombok.Data;

/**
 * @author ronghui
 * @Description
 * @date 2023/2/21 9:23
 */
@Data
public class ResponseModel<T> {
    private static final Integer SUCCESS = 200;
    private static final Integer ERROR = 400;
    public static final String DEFAULT_SUCCESS_MESSAGE = "请求成功";
    public static final String DEFAULT_ERROR_MESSAGE = "请求失败";
    private Integer code;
    private String message;
    private T t;

    public ResponseModel(Integer code, String message, T t) {
        this.code = code;
        this.message = message;
        this.t = t;
    }

    public static <T> ResponseModel success(T data) {
        return new ResponseModel(SUCCESS, DEFAULT_SUCCESS_MESSAGE, data);
    }

    public static <T> ResponseModel success(Integer code, T t) {
        return new ResponseModel(code, DEFAULT_SUCCESS_MESSAGE, t);
    }

    public static <T> ResponseModel success(Integer code, String message, T t) {
        return new ResponseModel(code, message, t);
    }

    public static ResponseModel error() {
        return new ResponseModel(ERROR, DEFAULT_ERROR_MESSAGE, null);
    }

    public static ResponseModel error(String message) {
        return new ResponseModel(ERROR, message, null);
    }

    public static ResponseModel error(Integer code, String message) {
        return new ResponseModel(code, message, null);
    }

    public static <T> ResponseModel error(Integer code, String message, T t) {
        return new ResponseModel(code, message, t);
    }

}
