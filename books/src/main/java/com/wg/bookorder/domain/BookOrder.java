package com.wg.bookorder.domain;

import com.alibaba.fastjson.annotation.JSONField;
import com.wg.book.domain.Book;
import com.wg.common.utils.TimeUtils;
import com.wg.user.domain.UserInfo;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Administrator on 2017/2/14 0014.
 */
@Cacheable
@Entity
@Table(name = "book_order", indexes = {
        @Index(name = "ukey_orderNumber", columnList = "orderNumber",unique = true),
        @Index(name = "key_userId", columnList = "userId"),
        @Index(name = "key_ownerId", columnList = "ownerId"),
        @Index(name = "key_notifyTime", columnList = "notifyTime")})
public class BookOrder {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private long orderId;
    private String orderNumber;
    private long userId;//申请人
    private long ownerId;//拥有人
    private int orderType;//订单类型
    private int orderStatus;//订单状态
    private int finishType;//订单完成类型
    private long bookId;//藏书id
    private BigDecimal evaluation;//估价
    private BigDecimal dayRentGold;//日租金
    private BigDecimal orderPayGold;//订单付款
    private BigDecimal totalRentGold;//总租金
    private BigDecimal breakGold;//违约金
    private BigDecimal brokenGold;//折损金
    private int leaseDay;//租借天数
    private int delayDay;//延期天数
    private String bookHandCode;//交书码
    private String bookReturnCode;//还书码
    private Date agreeApplyTime;//同意申请租售时间
    private Date handBookTime;//取书时间（交书时间,借阅开始时间）
    private Date applyReturnTime;//申请还书时间（租借结束时间）
    private Date returnBookTime;//还书时间(订单完成时间)
    @Column(length = 10000)
    private String remark;//备注
    private boolean isComplaint;//申述标志
    private boolean isDelete;//删除标志
    private boolean isOwnerDelete;//拥有人删除标志
    private Date notifyTime;//通知时间
    @org.hibernate.annotations.UpdateTimestamp
    @Column(columnDefinition = "TIMESTAMP")
    private Date updatedTime;
    private Date createdTime = TimeUtils.getCurrentDate();

    @JSONField(serialize = false)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", insertable = false, updatable = false)
    private UserInfo userInfo;

    @JSONField(serialize = false)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ownerId", insertable = false, updatable = false)
    private UserInfo ownerInfo;

    @JSONField(serialize = false)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bookId", insertable = false, updatable = false)
    private Book book;

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

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
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

    public int getFinishType() {
        return finishType;
    }

    public void setFinishType(int finishType) {
        this.finishType = finishType;
    }

    public long getBookId() {
        return bookId;
    }

    public void setBookId(long bookId) {
        this.bookId = bookId;
    }

    public BigDecimal getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(BigDecimal evaluation) {
        this.evaluation = evaluation;
    }

    public BigDecimal getDayRentGold() {
        return dayRentGold;
    }

    public void setDayRentGold(BigDecimal dayRentGold) {
        this.dayRentGold = dayRentGold;
    }

    public BigDecimal getOrderPayGold() {
        return orderPayGold;
    }

    public void setOrderPayGold(BigDecimal orderPayGold) {
        this.orderPayGold = orderPayGold;
    }

    public BigDecimal getTotalRentGold() {
        return totalRentGold;
    }

    public void setTotalRentGold(BigDecimal totalRentGold) {
        this.totalRentGold = totalRentGold;
    }

    public BigDecimal getBreakGold() {
        return breakGold;
    }

    public void setBreakGold(BigDecimal breakGold) {
        this.breakGold = breakGold;
    }

    public BigDecimal getBrokenGold() {
        return brokenGold;
    }

    public void setBrokenGold(BigDecimal brokenGold) {
        this.brokenGold = brokenGold;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public boolean isComplaint() {
        return isComplaint;
    }

    public void setComplaint(boolean complaint) {
        isComplaint = complaint;
    }

    public boolean isDelete() {
        return isDelete;
    }

    public void setDelete(boolean delete) {
        isDelete = delete;
    }

    public boolean isOwnerDelete() {
        return isOwnerDelete;
    }

    public void setOwnerDelete(boolean ownerDelete) {
        isOwnerDelete = ownerDelete;
    }

    public Date getNotifyTime() {
        return notifyTime;
    }

    public void setNotifyTime(Date notifyTime) {
        this.notifyTime = notifyTime;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public UserInfo getOwnerInfo() {
        return ownerInfo;
    }

    public void setOwnerInfo(UserInfo ownerInfo) {
        this.ownerInfo = ownerInfo;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }
}
