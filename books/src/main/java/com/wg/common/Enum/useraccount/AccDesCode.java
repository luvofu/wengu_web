package com.wg.common.Enum.useraccount;

/**
 * Created by Administrator on 2016/11/30 0030.
 */
public enum AccDesCode {
    //acc code
    SUCCESS(200, "成功"),
    FAILED(400, "失败"),
    ROLLBACK(600, "回滚"),

    //third result 10000-10099
    TRADE_CLOSED(10001, "支付宝充值交易超时关闭"),
    RESULT_FAIL(10002, "微信充值支付失败"),
    NAME_MISMATCH(10003, "微信支付账号和姓名不匹配"),
    AMOUNT_LIMIT(10004, "微信提现最小额一元"),
    PAYEE_USER_INFO_ERROR(10005, "支付宝账号和姓名不匹配"),
    EXCEED_LIMIT_SM_AMOUNT(10006, "支付宝提现最小额0.1元"),
    PAYEE_NOT_EXIST(10007, "支付宝账号不存在"),
    TRYAGAIN_OR_CALLWENYA(10008, "请稍后再试，或联系客服！");

    private final int code;

    private final String des;

    AccDesCode(int code, String des) {
        this.code = code;
        this.des = des;
    }

    public int getCode() {
        return code;
    }

    public String getDes() {
        return des;
    }
}
