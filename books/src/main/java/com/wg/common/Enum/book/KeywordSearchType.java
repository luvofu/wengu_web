package com.wg.common.Enum.book;

/**
 * Created by Administrator on 2016/9/9.
 */
public enum KeywordSearchType {
    User(0),Backend(1);
    // 定义私有变量
    private int type;
    // 构造函数,枚举类型只能为私有
    private KeywordSearchType(int type) {
        this.type = type;
    }
    public int getType() {
        return type;
    }
}
