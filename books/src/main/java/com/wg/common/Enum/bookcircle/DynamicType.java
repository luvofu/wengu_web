package com.wg.common.Enum.bookcircle;

/**
 * Created by Administrator on 2016/9/11.
 */
public enum DynamicType {
    Personal(0), System(1);
    // 定义私有变量
    private int type;
    // 构造函数,枚举类型只能为私有
    private DynamicType(int type) {
        this.type = type;
    }
    public int getType() {
        return type;
    }
}
