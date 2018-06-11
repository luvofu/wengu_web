package com.wg.common.Enum.book;

/**
 * Created by Administrator on 2016/9/9.
 */
public enum DownStatus {
    NotDown(0), DownSuccess(1), DownFailed(2);
    // 定义私有变量
    private int status;

    DownStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
