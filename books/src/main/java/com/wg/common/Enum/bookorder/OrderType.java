package com.wg.common.Enum.bookorder;

/**
 * Created by Administrator on 2016/9/12.
 */
public enum OrderType {
    Lease(1, "租借"), Sale(2, "出售");
    // 定义私有变量
    private int type;
    private String info;
    // 构造函数,枚举类型只能为私有

    OrderType(int type, String info) {
        this.type = type;
        this.info = info;
    }


    public int getType() {
        return type;
    }

    public String getInfo() {
        return info;
    }

    public static int getUserOrderType(int orderType, boolean isAppler) {
        if (orderType == Lease.getType()) {
            if (isAppler) {
                return UserOrderType.LeaseIn.getType();
            } else {
                return UserOrderType.LeaseOut.getType();
            }
        } else {
            if (isAppler) {
                return UserOrderType.BuyIn.getType();
            } else {
                return UserOrderType.SaleOut.getType();
            }
        }
    }
}
