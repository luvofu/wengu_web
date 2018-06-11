package com.wg.common.Enum.community;

/**
 * Created by Administrator on 2017/3/27 0027.
 */
public enum  JoinStatus {
    Accept(1, "允许任何人加入"), Vertify(2, "验证加入"), Refuse(3, "拒绝任何人加入");

    private int status;
    private String info;

    JoinStatus(int status, String info) {
        this.status = status;
        this.info = info;
    }

    public int getStatus() {
        return status;
    }

    public String getInfo() {
        return info;
    }
}
