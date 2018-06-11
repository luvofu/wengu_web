package com.wg.common.Enum.common;

/**
 * Created by Administrator on 2016/9/6.
 */
public enum HomeType {
    Book(0), BookSheet(1),CommunityComment(2),Picword(3);
    // 定义私有变量
    private int type;
    // 构造函数,枚举类型只能为私有
    private HomeType(int type) {
        this.type = type;
    }
    public int getType() {
        return type;
    }
}
