package com.wg.common.Enum.user;

/**
 * Created by Administrator on 2016/9/28.
 */
public enum ThirdType {
    Weixin(0), Weibo(1);
    // 定义私有变量
    private int type;
    // 构造函数,枚举类型只能为私有

    ThirdType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
