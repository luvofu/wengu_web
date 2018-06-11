package com.wg.bookorder.model.request;

/**
 * Created by Administrator on 2017/2/16 0016.
 */
public class BookOrderRequest {
    private String token;
    private long orderId = -1;
    private int orderType = -1;
    private int orderStatus = -1;
    private long userBookId = -1;
    private int leaseDay = -1;
    private String content;
    private double rating;
    private int dealStatus = -1;
    private String bookHandCode;
    private String bookReturnCode;
    private double brokenGold = -1;
    private int userOrderType = -1;
    private int page = 0;
    private String orderString;
    private int delayDay = -1;
    private int payType = -1;
    private int filterType = 0;
    private String reason;


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public int getOrderType() {
        return orderType;
    }

    public void setOrderType(int orderType) {
        this.orderType = orderType;
    }

    public int getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(int orderStatus) {
        this.orderStatus = orderStatus;
    }

    public long getUserBookId() {
        return userBookId;
    }

    public void setUserBookId(long userBookId) {
        this.userBookId = userBookId;
    }

    public int getLeaseDay() {
        return leaseDay;
    }

    public void setLeaseDay(int leaseDay) {
        this.leaseDay = leaseDay;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getDealStatus() {
        return dealStatus;
    }

    public void setDealStatus(int dealStatus) {
        this.dealStatus = dealStatus;
    }

    public String getBookHandCode() {
        return bookHandCode;
    }

    public void setBookHandCode(String bookHandCode) {
        this.bookHandCode = bookHandCode;
    }

    public String getBookReturnCode() {
        return bookReturnCode;
    }

    public void setBookReturnCode(String bookReturnCode) {
        this.bookReturnCode = bookReturnCode;
    }

    public double getBrokenGold() {
        return brokenGold;
    }

    public void setBrokenGold(double brokenGold) {
        this.brokenGold = brokenGold;
    }

    public int getUserOrderType() {
        return userOrderType;
    }

    public void setUserOrderType(int userOrderType) {
        this.userOrderType = userOrderType;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public String getOrderString() {
        return orderString;
    }

    public void setOrderString(String orderString) {
        this.orderString = orderString;
    }

    public int getDelayDay() {
        return delayDay;
    }

    public void setDelayDay(int delayDay) {
        this.delayDay = delayDay;
    }

    public int getPayType() {
        return payType;
    }

    public void setPayType(int payType) {
        this.payType = payType;
    }

    public int getFilterType() {
        return filterType;
    }

    public void setFilterType(int filterType) {
        this.filterType = filterType;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
