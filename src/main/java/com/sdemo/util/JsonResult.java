package com.sdemo.util;

import java.util.List;

public class JsonResult<T> {
    private boolean success;
    private int errCode;
    private String errMsg = "";
    private T data;
    private List<T> list;

    //set get...
    //记得写默认的构造方法


    public JsonResult(T result) {
        this.success=true;
        this.errCode = 0;
        this.errMsg = "OK";
        this.data = result;
    }

    public JsonResult(int code ,String result) {
        this.success=false;
        this.errCode = code;
        this.errMsg = result;
    }

    //ok构造参数
    public static <T> JsonResult<T> ok(T result) {
        return new JsonResult<T>(result);
    }

    //error
    public static <T> JsonResult<T> error(int code ,String result) {
        return new JsonResult<T>( code , result);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getErrCode() {
        return errCode;
    }

    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
