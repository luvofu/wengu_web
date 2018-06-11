package com.wg.common.Enum.message;

/**
 * Created by Administrator on 2016/9/7.
 */
public enum MsgLinkType {
    Dynamic(1, "动态"),BookSheet(2, "书单"),BookCheck(3,"新书");
    private int type;
    private String info;

    MsgLinkType(int type, String info) {
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
