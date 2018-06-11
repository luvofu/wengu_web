package com.wg.bookorder.model.response;

import com.wg.book.domain.Book;
import com.wg.bookorder.domain.BookOrder;
import com.wg.bookorder.utils.BookOrderUtils;
import com.wg.common.Enum.bookorder.OrderType;
import com.wg.common.utils.Utils;
import com.wg.common.utils.dbutils.DaoUtils;
import com.wg.user.domain.UserInfo;
import com.wg.user.domain.UserToken;
import com.wg.user.utils.UserUtils;

import java.util.Date;

/**
 * Created by Administrator on 2017/2/20 0020.
 */
public class BookOrderDetailResponse {
    private long orderId;
    private String orderNumber;//订单号
    private int userOrderType;//用户订单类型
    private int orderStatus;//订单状态
    private int finishType;//订单完成类型
    private boolean isComplaint;//申述标志
    private String remark;//备注

    private long userId;//申请人
    private String avatar;//申请人头像
    private String nickname;//申请人昵称
    private String address;//申请人地址

    private long ownerId;//拥有人
    private String ownerAvatar;//拥有人头像
    private String ownerNickname;//拥有人昵称
    private String ownerAddress;//拥有人地址

    private long bookId;//书籍id
    private String title;//书名
    private String cover;//封面

    private double evaluation;//估价
    private double dayRentGold;//日租金
    private double orderPayGold;//付款金
    private double totalRentGold;//总租金
    private double breakGold;//违约金
    private double brokenGold;//折损金
    private double receiveGold;//应收金额

    private int leaseDay;//租借天数
    private int maxLeaseDay;//最长租借天数
    private int realLeaseDay;//实际租借天数
    private int delayDay;//延期天数
    private String bookHandCode;//交书码
    private String bookReturnCode;//还书码

    private Date createdTime;//创建时间,申请订单时间
    private Date agreeApplyTime;//同意订单时间
    private Date handBookTime;//取书时间、交书时间、借阅开始时间
    private Date applyReturnTime;//申请还书时间、租借结束时间
    private Date returnBookTime;//还书时间
    private Date updatedTime;//结束时间

    private Date notifyTime;//通知时间、超时时间
    private String notifyInfo;//提醒信息

    public BookOrderDetailResponse(BookOrder bookOrder, UserToken userToken) {
        this.orderId = bookOrder.getOrderId();
        this.orderNumber = bookOrder.getOrderNumber();
        this.userOrderType = OrderType.getUserOrderType(bookOrder.getOrderType(), userToken.getUserId() == bookOrder.getUserId());
        this.orderStatus = bookOrder.getOrderStatus();
        this.finishType = bookOrder.getFinishType();
        this.isComplaint = bookOrder.isComplaint();
        this.remark = bookOrder.getRemark();

        UserInfo userInfo = DaoUtils.userInfoDao.findOne(bookOrder.getUserId());
        this.userId = userInfo.getUserId();
        this.avatar = Utils.getUrl(userInfo.getAvatar());
        this.nickname = UserUtils.getSafeNickname(userInfo.getNickname());
        this.address = UserUtils.combinAddress(userInfo, true);

        UserInfo owner = DaoUtils.userInfoDao.findOne(bookOrder.getOwnerId());
        this.ownerId = owner.getUserId();
        this.ownerAvatar = Utils.getUrl(owner.getAvatar());
        this.ownerNickname = UserUtils.getSafeNickname(owner.getNickname());
        this.ownerAddress = UserUtils.combinAddress(owner, true);

        Book book = DaoUtils.bookDao.findOne(bookOrder.getBookId());
        this.bookId = book.getBookId();
        this.title = book.getTitle();
        this.cover = Utils.getUrl(book.getCover());

        this.evaluation = bookOrder.getEvaluation().doubleValue();
        this.dayRentGold = bookOrder.getDayRentGold().doubleValue();
        this.orderPayGold = bookOrder.getOrderPayGold().doubleValue();
        this.breakGold = bookOrder.getBreakGold().doubleValue();
        this.brokenGold = bookOrder.getBrokenGold().doubleValue();
        this.totalRentGold = bookOrder.getTotalRentGold().doubleValue();
        this.receiveGold = BookOrderUtils.transferGold(bookOrder).doubleValue();

        this.leaseDay = bookOrder.getLeaseDay();
        this.maxLeaseDay = bookOrder.getLeaseDay() + BookOrderUtils.LEASE_DELAY_DAY;
        this.realLeaseDay = BookOrderUtils.generRealLeaseDay(bookOrder);
        this.delayDay = bookOrder.getDelayDay();
        this.bookHandCode = bookOrder.getBookHandCode();
        this.bookReturnCode = bookOrder.getBookReturnCode();

        this.createdTime = bookOrder.getCreatedTime();
        this.agreeApplyTime = bookOrder.getAgreeApplyTime();
        this.handBookTime = bookOrder.getHandBookTime();
        this.applyReturnTime = bookOrder.getApplyReturnTime();
        this.returnBookTime = bookOrder.getReturnBookTime();
        this.updatedTime = bookOrder.getUpdatedTime();

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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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

    public long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerAvatar() {
        return ownerAvatar;
    }

    public void setOwnerAvatar(String ownerAvatar) {
        this.ownerAvatar = ownerAvatar;
    }

    public String getOwnerNickname() {
        return ownerNickname;
    }

    public void setOwnerNickname(String ownerNickname) {
        this.ownerNickname = ownerNickname;
    }

    public String getOwnerAddress() {
        return ownerAddress;
    }

    public void setOwnerAddress(String ownerAddress) {
        this.ownerAddress = ownerAddress;
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

    public double getOrderPayGold() {
        return orderPayGold;
    }

    public void setOrderPayGold(double orderPayGold) {
        this.orderPayGold = orderPayGold;
    }

    public double getTotalRentGold() {
        return totalRentGold;
    }

    public void setTotalRentGold(double totalRentGold) {
        this.totalRentGold = totalRentGold;
    }

    public double getBreakGold() {
        return breakGold;
    }

    public void setBreakGold(double breakGold) {
        this.breakGold = breakGold;
    }

    public double getBrokenGold() {
        return brokenGold;
    }

    public void setBrokenGold(double brokenGold) {
        this.brokenGold = brokenGold;
    }

    public double getReceiveGold() {
        return receiveGold;
    }

    public void setReceiveGold(double receiveGold) {
        this.receiveGold = receiveGold;
    }

    public int getLeaseDay() {
        return leaseDay;
    }

    public void setLeaseDay(int leaseDay) {
        this.leaseDay = leaseDay;
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

    public int getDelayDay() {
        return delayDay;
    }

    public void setDelayDay(int delayDay) {
        this.delayDay = delayDay;
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

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getAgreeApplyTime() {
        return agreeApplyTime;
    }

    public void setAgreeApplyTime(Date agreeApplyTime) {
        this.agreeApplyTime = agreeApplyTime;
    }

    public Date getHandBookTime() {
        return handBookTime;
    }

    public void setHandBookTime(Date handBookTime) {
        this.handBookTime = handBookTime;
    }

    public Date getApplyReturnTime() {
        return applyReturnTime;
    }

    public void setApplyReturnTime(Date applyReturnTime) {
        this.applyReturnTime = applyReturnTime;
    }

    public Date getReturnBookTime() {
        return returnBookTime;
    }

    public void setReturnBookTime(Date returnBookTime) {
        this.returnBookTime = returnBookTime;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
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
