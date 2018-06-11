package com.wg.useraccount.utils.alipay;

import java.util.LinkedHashMap;

/**
 * Created by Administrator on 2017/3/23 0023.
 */
public class PaySynRes {
    LinkedHashMap<String,String> alipay_trade_app_pay_response;
    String sign;
    String sign_type;

    public LinkedHashMap<String, String> getAlipay_trade_app_pay_response() {
        return alipay_trade_app_pay_response;
    }

    public void setAlipay_trade_app_pay_response(LinkedHashMap<String, String> alipay_trade_app_pay_response) {
        this.alipay_trade_app_pay_response = alipay_trade_app_pay_response;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getSign_type() {
        return sign_type;
    }

    public void setSign_type(String sign_type) {
        this.sign_type = sign_type;
    }
}
