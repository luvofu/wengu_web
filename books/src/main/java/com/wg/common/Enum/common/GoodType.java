package com.wg.common.Enum.common;

/**
 * Created with IntelliJ IDEA.
 * User: wzhonggo
 * Date: 16-9-4
 * Time: 下午4:35
 * To change this template use File | Settings | File Templates.
 */
public enum GoodType {
    Comment(0), Dynamic(1),BookSheet(2),Picword(3);
    // 定义私有变量
    private int type;
    // 构造函数,枚举类型只能为私有
    private GoodType(int type) {
        this.type = type;
    }
    public int getType() {
        return type;
    }
}
