package com.wg.common;

import com.alibaba.fastjson.JSONObject;
import com.wg.common.Enum.common.ResponseCode;

/**
 * Created by wzhonggo on 8/4/2016.
 */
public class ResponseContent {
    private int code;
    private String msg;
    private Object data;

    public ResponseContent() {
        this.code = ResponseCode.SUCCESS.getCode();
        this.msg = ResponseCode.SUCCESS.getMsg();
        this.data = new JSONObject();
    }

    public void putData(String key, Object value) {
        ((JSONObject) data).put(key, value);
    }

    public void update(ResponseCode responseCode) {
        this.code = responseCode.getCode();
        this.msg = responseCode.getMsg();
    }

    public void update(ResponseCode responseCode, Object data) {
        this.code = responseCode.getCode();
        this.msg = responseCode.getMsg();
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
