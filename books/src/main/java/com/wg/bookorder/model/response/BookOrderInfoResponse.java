package com.wg.bookorder.model.response;

import com.wg.book.domain.Book;
import com.wg.common.Enum.bookorder.OrderType;
import com.wg.common.utils.Utils;
import com.wg.common.utils.dbutils.DaoUtils;
import com.wg.bookorder.domain.BookOrder;
import com.wg.bookorder.utils.BookOrderUtils;
import com.wg.user.domain.UserInfo;
import com.wg.user.domain.UserToken;
import com.wg.user.utils.UserUtils;

import java.util.Date;

/**
 * Created by Administrator on 2017/2/20 0020.
 */
public class BookOrderInfoResponse {
    //order info
    private long orderId;
    private String orderNumber;//订单号
    private int userOrderType;//用户订单类型
    private int orderStatus;//订单状态
    private int finishType;//完成类型
    private boolean isComplaint;//申述标志

    //user info
    private long userId;//人
    private String avatar;//头像
    private String nickname;//昵称
    private String address;//地址

    //book info
    private long bookId;//书籍id
    private String title;//书名
    private String cover;//封面

    private double evaluation;//估价
    private double dayRentGold;//日租金
    private int leaseDay;//租借天数
    private int delayDay;//延期天数
    private int maxLeaseDay;//最长租借天数
    private int realLeaseDay;//实际租借天数
    private double orderPayGold;//付款金

    private String bookHandCode;//交书码 （区分）
    private String bookReturnCode;//还书码 （区分）

    private Date notifyTime;
    private String notifyInfo;//提醒信息


    public BookOrderInfoResponse(BookOrder bookOrder, UserToken userToken) {
        this.orderId = bookOrder.getOrderId();
        this.orderNumber = bookOrder.getOrderNumber();
        this.userOrderType = OrderType.getUserOrderType(bookOrder.getOrderType(), bookOrder.getUserId() == userToken.getUserId());
        this.orderStatus = bookOrder.getOrderStatus();
        this.finishType = bookOrder.getFinishType();
        this.isComplaint = bookOrder.isComplaint();

        UserInfo userInfo = DaoUtils.userInfoDao.findOne(userToken.getUserId() != bookOrder.getUserId() ? bookOrder.getUserId() : bookOrder.getOwnerId());
        this.userId = userInfo.getUserId();
        this.avatar = Utils.getUrl(userInfo.getAvatar());
        this.nickname = UserUtils.getSafeNickname(userInfo.getNickname());
        this.address = UserUtils.combinAddress(userInfo, true);

        Book book = DaoUtils.bookDao.findOne(bookOrder.getBookId());
        this.bookId = book.getBookId();
        this.title = book.getTitle();
        this.cover = Utils.getUrl(book.getCover());

        this.evaluation = bookOrder.getEvaluation().doubleValue();
        this.dayRentGold = bookOrder.getDayRentGold().doubleValue();
        this.leaseDay = bookOrder.getLeaseDay();
        this.delayDay = bookOrder.getDelayDay();
        this.maxLeaseDay = bookOrder.getLeaseDay() + BookOrderUtils.LEASE_DELAY_DAY;
        this.realLeaseDay = BookOrderUtils.generRealLeaseDay(bookOrder);
        this.orderPayGold = bookOrder.getOrderPayGold().doubleValue();

        this.bookHandCode = bookOrder.getBookHandCode();
        this.bookReturnCode = bookOrder.getBookReturnCode();

        this.notifyTime = bookOrder.getNotifyTime();
        this.notifyInfo = BookOrderUtils.generNotifyInfo(bookOrder, userToken);
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public int getUserOrderType() {
        return userOrderType;
    }

    public void setUserOrderType(int userOrderType) {
        this.userOrderType = userOrderType;
    }

    public int getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(int orderStatus) {
        this.orderStatus = orderStatus;
    }

    public int getFinishType() {
        return finishType;
    }

    public void setFinishType(int finishType) {
        this.finishType = finishType;
    }

    public boolean isComplaint() {
        return isComplaint;
    }

    public void setComplaint(boolean complaint) {
        isComplaint = complaint;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getBookId() {
        return bookId;
    }

    public void setBookId(long bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public double getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(double evaluation) {
        this.evaluation = evaluation;
    }

    public double getDayRentGold() {
        return dayRentGold;
    }

    public void setDayRentGold(double dayRentGold) {
        this.dayRentGold = dayRentGold;
    }

    public int getLeaseDay() {
        return leaseDay;
    }

    public void setLeaseDay(int leaseDay) {
        this.leaseDay = leaseDay;
    }

    public int getDelayDay() {
        return delayDay;
    }

    public void setDelayDay(int delayDay) {
        this.delayDay = delayDay;
    }

    public int getMaxLeaseDay() {
        return maxLeaseDay;
    }

    public void setMaxLeaseDay(int maxLeaseDay) {
        this.maxLeaseDay = maxLeaseDay;
    }

    public int getRealLeaseDay() {
        return realLeaseDay;
    }

    public void setRealLeaseDay(int realLeaseDay) {
        this.realLeaseDay = realLeaseDay;
    }

    public double getOrderPayGold() {
        return orderPayGold;
    }

    public void setOrderPayGold(double orderPayGold) {
        this.orderPayGold = orderPayGold;
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

    public Date getNotifyTime() {
        return notifyTime;
    }

    public void setNotifyTime(Date notifyTime) {
        this.notifyTime = notifyTime;
    }

    public String getNotifyInfo() {
        return notifyInfo;
    }

    public void setNotifyInfo(String notifyInfo) {
        this.notifyInfo = notifyInfo;
    }
}
