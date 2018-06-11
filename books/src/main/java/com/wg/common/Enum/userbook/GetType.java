package com.wg.common.Enum.userbook;

/**
 * Created by Administrator on 2016/9/12.
 */
public enum GetType {
    //0其他、1购买、2赠予
    Other(0), Buy(1), Present(2);
    // 定义私有变量
    private int type;
    // 构造函数,枚举类型只能为私有
    private GetType(int type) {
        this.type = type;
    }
    public int getType() {
        return type;
    }
}
