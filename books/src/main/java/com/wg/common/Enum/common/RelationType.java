package com.wg.common.Enum.common;

/**
 * Created by Administrator on 2016/9/17.
 */
public enum RelationType {
    Stranger(0),Friend(1),Personal(2);
    private int type;

    RelationType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
