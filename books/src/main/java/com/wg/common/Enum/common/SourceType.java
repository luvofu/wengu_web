package com.wg.common.Enum.common;

/**
 * Created by Administrator on 2016/9/7.
 */
public enum SourceType {
    Home(0),DouBan(1);
    // 定义私有变量
    private int type;
    // 构造函数,枚举类型只能为私有
    private SourceType(int type) {
        this.type = type;
    }
    public int getType() {
        return type;
    }
}
