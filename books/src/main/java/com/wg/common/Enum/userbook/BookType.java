package com.wg.common.Enum.userbook;

/**
 * Created by Administrator on 2016/9/12.
 */
public enum BookType {
    Paper(0), Electronic(1),Other(2);
    // 定义私有变量
    private int type;
    // 构造函数,枚举类型只能为私有
    private BookType(int type) {
        this.type = type;
    }
    public int getType() {
        return type;
    }
}
