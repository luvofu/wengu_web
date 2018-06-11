package com.wg.common.Enum.community;

/**
 * Created by Administrator on 2017/3/27 0027.
 */
public enum MemberType {
    Stranger(0, "游客"), Commoner(1, "普通"), Manager(2, "管理员"), Owner(3, "社主");

    private int type;
    private String info;

    MemberType(int type, String info) {
        this.type = type;
        this.info = info;
    }

    public int getType() {
        return type;
    }

    public String getInfo() {
        return info;
    }
}
