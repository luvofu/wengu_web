package com.wg.common.Enum.userbook;

/**
 * Created by Administrator on 2016/11/11 0011.
 */
public enum CategoryType {
    Normal(0), Custom(1),All(2);
    // 定义私有变量
    private int type;
    // 构造函数,枚举类型只能为私有
    private CategoryType(int type) {
        this.type = type;
    }
    public int getType() {
        return type;
    }
}
