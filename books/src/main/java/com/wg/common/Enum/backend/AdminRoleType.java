package com.wg.common.Enum.backend;

/**
 * Created with IntelliJ IDEA.
 * User: wzhonggo
 * Date: 16-11-6
 * Time: 上午10:57
 * To change this template use File | Settings | File Templates.
 */
public enum AdminRoleType {
    READ(0), WRITE(1), READ_WRITE(2);
    // 定义私有变量
    private int type;
    // 构造函数,枚举类型只能为私有

    AdminRoleType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
