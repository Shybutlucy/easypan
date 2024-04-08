package com.swx.common.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class R implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer code;

    private String message;

    private Object data;

    private R() {

    }

    public R(ResultCode resultCode, Object data) {
        this.code = resultCode.code();
        this.message = resultCode.message();
        this.data = data;
    }

    public void setResultCode(ResultCode resultCode) {
        this.code = resultCode.code();
        this.message = resultCode.message();
    }

    // 返回成功
    public static R success() {
        R result = new R();
        result.setResultCode(ResultCode.SUCCESS);
        return result;
    }
    // 返回成功
    public static R success(Object data) {
        R result = new R();
        result.setResultCode(ResultCode.SUCCESS);
        result.setData(data);
        return result;
    }

    // 返回失败
    public static R fail(Integer code, String message) {
        R result = new R();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
    // 返回失败
    public static R fail(ResultCode resultCode) {
        R result = new R();
        result.setResultCode(resultCode);
        return result;
    }
}
