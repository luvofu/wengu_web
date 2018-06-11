package com.wg.admin.model.request;

/**
 * Created by Administrator on 2017/3/3 0003.
 */
public class TestRequest {
    /*
    * 测试类型 :
    * 0 orderpay payType:ali serialNumber:TestSerialNumber
    * 1 timetravel
    * */
    String token;
    int testType;
    long id = -1;
    long orderId;//订单id
    int day = 0;
    int hour = 0;
    int minute = 0;
    String keyword;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getTestType() {
        return testType;
    }

    public void setTestType(int testType) {
        this.testType = testType;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}

