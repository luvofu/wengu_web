package com.wg.common.Enum.bookgroup;

/**
 * Created by Administrator on 2016/9/6.
 */
public enum ReplyType {
    Comment(0), Reply(1);
    // 定义私有变量
    private int type;
    // 构造函数,枚举类型只能为私有
    private ReplyType(int type) {
        this.type = type;
    }
    public int getType() {
        return type;
    }
}
