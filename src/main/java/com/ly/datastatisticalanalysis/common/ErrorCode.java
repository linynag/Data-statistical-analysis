package com.ly.datastatisticalanalysis.common;

/**
 * 自定义错误码
 */
public enum ErrorCode {
    SUCCESS(200, "success"),
    FAIL(400, "fail"),
    ERROR(500, "error");

    private final int code;
    private final String msg;

    ErrorCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public Object getCode() {
        return code;
    }
}
