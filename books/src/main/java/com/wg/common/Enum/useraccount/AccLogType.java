package com.wg.common.Enum.useraccount;

/**
 * Created by Administrator on 2016/9/12.
 */
public enum AccLogType {
    Common("Common"),
    ChargeAccount("ChargeAccount"),
    WithdrawAccount("WithdrawAccount"),
    OrderGoldFrozen("OrderGoldFrozen"),
    OrderGoldDefrozen("OrderGoldDefrozen"),
    OrderIncomeTrans("OrderIncomeTrans"),
    OrderCostTrans("OrderCostTrans"),
    ChargeAlipayVer("ChargeAlipayVer"),
    ChargeWeixinVer("ChargeWeixinVer"),
    FundAlipayRes("FundAlipayRes"),
    FundWeixinRes("FundWeixinRes"),
    RollbackAccount("RollbackAccount"),
    FundWeixinQury("FundWeixinQury"),
    FundAlipayQury("FundAlipayQury");
    // 定义私有变量
    private String type;
    // 构造函数,枚举类型只能为私有

    AccLogType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
