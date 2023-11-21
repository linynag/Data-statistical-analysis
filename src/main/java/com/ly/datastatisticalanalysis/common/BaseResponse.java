package com.ly.datastatisticalanalysis.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用返回类
 *
 * @author: LY
 **/
@Data
public class BaseResponse<T> implements Serializable {

    private Integer code;
    private String msg;
    private T data;

    public BaseResponse(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }


}
