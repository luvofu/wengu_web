package com.wg.common.Enum.userbook;

/**
 * Created by Administrator on 2016/9/12.
 */
public enum CheckStatus {
    InCheck(0), NotPass(1), Pass(2);
    // 定义私有变量
    private int status;
    // 构造函数,枚举类型只能为私有
    private CheckStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
