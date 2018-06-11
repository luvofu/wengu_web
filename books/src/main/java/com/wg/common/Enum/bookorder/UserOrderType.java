package com.wg.common.Enum.bookorder;

/**
 * Created by Administrator on 2016/9/12.
 */
public enum UserOrderType {
    All(0, "全部"), LeaseIn(1, "借入"), LeaseOut(2, "借出"), BuyIn(3, "买入"), SaleOut(4, "卖出");
    // 定义私有变量
    private int type;
    private String info;
    // 构造函数,枚举类型只能为私有

    UserOrderType(int type, String info) {
        this.type = type;
        this.info = info;
    }

    public int getType() {
        return type;
    }

    public String getInfo() {
        return info;
    }

    public static int getOrderType(int userOrderType) {
        if (userOrderType == UserOrderType.LeaseIn.getType() || userOrderType == UserOrderType.LeaseOut.getType()) {
            return OrderType.Lease.getType();
        } else if (userOrderType == UserOrderType.BuyIn.getType() || userOrderType == UserOrderType.SaleOut.getType()) {
            return OrderType.Sale.getType();
        }
        return UserOrderType.All.getType();
    }

    public static String getTypeInfo(int userOrderType) {
        if (userOrderType == LeaseIn.getType()) {
            return LeaseIn.getInfo();
        } else if (userOrderType == LeaseOut.getType()) {
            return LeaseOut.getInfo();
        } else if (userOrderType == BuyIn.getType()) {
            return BuyIn.getInfo();
        } else if (userOrderType == SaleOut.getType()) {
            return SaleOut.getInfo();
        }
        return "";
    }
}
