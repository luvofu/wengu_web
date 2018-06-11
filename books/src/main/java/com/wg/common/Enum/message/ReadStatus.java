package com.wg.common.Enum.message;

/**
 * Created by Administrator on 2016/9/7.
 */
public enum ReadStatus {
    NotRead(0),Read(1);
    private int status;
    private ReadStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
