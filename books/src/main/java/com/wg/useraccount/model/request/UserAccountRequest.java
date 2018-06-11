package com.wg.useraccount.model.request;

/**
 * Created by Administrator on 2017/3/12 0012.
 */
public class UserAccountRequest {
    private String token;
    private long billId = -1;
    private String tradeNumber;//账单交易号
    private int payType = -1;
    private int billType = -1;
    private double billGold;
    private String orderString;
    private int page = 0;
    private long thirdId = -1;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getBillId() {
        return billId;
    }

    public void setBillId(long billId) {
        this.billId = billId;
    }

    public String getTradeNumber() {
        return tradeNumber;
    }

    public void setTradeNumber(String tradeNumber) {
        this.tradeNumber = tradeNumber;
    }

    public int getPayType() {
        return payType;
    }

    public void setPayType(int payType) {
        this.payType = payType;
    }

    public int getBillType() {
        return billType;
    }

    public void setBillType(int billType) {
        this.billType = billType;
    }

    public double getBillGold() {
        return billGold;
    }

    public void setBillGold(double billGold) {
        this.billGold = billGold;
    }

    public String getOrderString() {
        return orderString;
    }

    public void setOrderString(String orderString) {
        this.orderString = orderString;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public long getThirdId() {
        return thirdId;
    }

    public void setThirdId(long thirdId) {
        this.thirdId = thirdId;
    }
}
