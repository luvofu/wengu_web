package com.wg.common.Enum.message;

/**
 * Created by Administrator on 2016/9/7.
 */
public enum SMsgTemplate {
    ValidCode("SMS_24890411"),
    NewOrder("SMS_63430383"),
    ApplyReturnNB("SMS_56695315"),
    ReturnBookNB("SMS_56600361");
    private String tempCode;

    SMsgTemplate(String tempCode) {
        this.tempCode = tempCode;
    }

    public String getTempCode() {
        return tempCode;
    }
}
