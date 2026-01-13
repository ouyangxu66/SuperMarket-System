package com.supermarket.common.result;

/**
 * API 统一返回状态码
 * 定义了后端返回给前端的状态码及其含义
 */
public enum ResultCode {
    
    SUCCESS(200, "操作成功"),
    ERROR(500, "系统内部异常"),
    unauthorized(401, "暂未登录或Token已失效"),
    FORBIDDEN(403, "没有权限进行此操作"),
    PARAM_ERROR(400, "参数错误"),
    USER_EXIST(2001, "用户已存在"),
    USER_NOT_LOGIN(2002, "用户不存在或密码错误");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}