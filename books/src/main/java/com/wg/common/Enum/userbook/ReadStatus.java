package com.wg.common.Enum.userbook;

/**
 * Created by Administrator on 2016/9/12.
 */
public enum ReadStatus {
    Finish(0), NotRead(1),Reading(2);
    // 定义私有变量
    private int type;
    // 构造函数,枚举类型只能为私有
    private ReadStatus(int type) {
        this.type = type;
    }
    public int getType() {
        return type;
    }
}
