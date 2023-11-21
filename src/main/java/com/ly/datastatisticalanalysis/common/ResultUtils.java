package com.ly.datastatisticalanalysis.common;

public class ResultUtils {
    private static final String SUCCESS = "success";
    private static final String FAIL = "fail";
    private static final String ERROR = "error";

    public static BaseResponse success() {
        return new BaseResponse(200, SUCCESS, null);
    }
}
