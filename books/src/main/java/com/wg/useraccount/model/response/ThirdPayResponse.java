package com.wg.useraccount.model.response;

import com.wg.useraccount.domain.ThirdPay;

/**
 * Created by Administrator on 2017/3/10 0010.
 */
public class ThirdPayResponse {
    private long thirdId;
    private int payType;
    private String account;
    private String name;
    private String nickname;

    public ThirdPayResponse(ThirdPay thirdPay) {
        this.thirdId = thirdPay.getThirdId();
        this.payType = thirdPay.getPayType();
        this.account = thirdPay.getAccount();
        this.name = thirdPay.getName();
        this.nickname = thirdPay.getNickname();
    }

    public long getThirdId() {
        return thirdId;
    }

    public void setThirdId(long thirdId) {
        this.thirdId = thirdId;
    }

    public int getPayType() {
        return payType;
    }

    public void setPayType(int payType) {
        this.payType = payType;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
