package com.wg.common.Enum.useraccount;

/**
 * Created by Administrator on 2016/9/12.
 */
public enum BillType {
    Charge(1, "充值"), Withdraw(2, "提现"), Income(3, "收益"), Cost(4, "支出");
    // 定义私有变量
    private int type;
    private String info;
    // 构造函数,枚举类型只能为私有

    BillType(int type, String info) {
        this.type = type;
        this.info = info;
    }

    public int getType() {
        return type;
    }

    public String getInfo() {
        return info;
    }
}
