package com.wg.common.Enum.useraccount;

/**
 * Created by Administrator on 2016/9/12.
 */
public enum PayType {
    None(0, "无"), Alipay(1, "支付宝"), Weixin(2, "微信支付");
    // 定义私有变量
    private int type;
    private String info;
    // 构造函数,枚举类型只能为私有

    PayType(int type, String info) {
        this.type = type;
        this.info = info;
    }

    public int getType() {
        return type;
    }

    public String getInfo() {
        return info;
    }

    public static String getInfo(int payType) {
        if (payType == Alipay.getType()) {
            return Alipay.getInfo();
        } else if (payType == Weixin.getType()) {
            return Weixin.getInfo();
        } else {
            return None.getInfo();
        }
    }
}
