package com.wg.common.Enum.booksheet;

/**
 * Created by Administrator on 2016/9/9.
 */
public enum SortType {
    collectionNum(0),createdTime(1);
    // 定义私有变量
    private int type;
    // 构造函数,枚举类型只能为私有
    private SortType(int type) {
        this.type = type;
    }
    public int getType() {
        return type;
    }
}
