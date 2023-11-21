package com.ly.datastatisticalanalysis.exception;

import com.ly.datastatisticalanalysis.common.ErrorCode;

public class BusinessException extends RuntimeException{

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMsg());
    }
}
