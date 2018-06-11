package com.wg.common.Enum.common;

/**
 * Created by Administrator on 2016/9/7.
 */
public enum Permission {
    Open(0),Friend(1), Personal(2);
    private int type;

    Permission(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

}
