package com.wg.common.Enum.news;

/**
 * Created by Administrator on 2016/12/25 0025.
 */
public enum GetType {
    WenyaBack(0), ShowApi(1);

    int type;

    GetType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
