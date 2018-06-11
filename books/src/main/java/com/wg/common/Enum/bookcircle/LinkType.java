package com.wg.common.Enum.bookcircle;

/**
 * Created by Administrator on 2016/9/6.
 */
public enum LinkType {
    Common(0),Book(1), BookSheet(2),Comment(3);
    // 定义私有变量
    private int type;
    // 构造函数,枚举类型只能为私有
    private LinkType(int type) {
        this.type = type;
    }
    public int getType() {
        return type;
    }
}

