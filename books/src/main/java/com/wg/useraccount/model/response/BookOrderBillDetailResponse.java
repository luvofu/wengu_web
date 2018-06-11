package com.wg.useraccount.model.response;

import com.wg.bookorder.domain.BookOrder;
import com.wg.common.Enum.useraccount.BillType;
import com.wg.common.utils.Utils;
import com.wg.common.utils.dbutils.DaoUtils;
import com.wg.user.domain.UserInfo;
import com.wg.user.utils.UserUtils;
import com.wg.useraccount.domain.UserBill;
import com.wg.useraccount.utils.UserAccUtils;

import java.util.Date;

/**
 * Created by Administrator on 2017/3/3 0003.
 */
public class BookOrderBillDetailResponse {
    private long billId;//账单id
    private double billGold;//账单金额
    private int billType;//账单类型:收入、支出、内转
    private Long orderId;//订单id
    private String orderNumber;//订单号
    private Long userId;//账单关系人
    private String avatar;//头像
    private String nickname;//昵称、提现账号昵称
    private Integer payType;//支付方式
    private String tradeNumber;//交易号
    private String serialNumber;//充值流水号
    private String account;//提现账号
    private String name;//提现账号实名
    private String payNickname;//提现账号昵称
    private Date dealTime;//充值、提现成功时间
    private Integer dealStatus;//充值、提现状态
    private String errorMsg;//充值提现错误描述
    private String billDescription;//账单描述
    private Date createdTime;//创建时间

    public BookOrderBillDetailResponse(UserBill userBill) {
        this.billId = userBill.getBillId();
        this.billGold = userBill.getBillGold().doubleValue();
        this.billType = userBill.getBillType();
        this.dealTime = userBill.getDealTime();
        this.dealStatus = userBill.getDealStatus();

        if (billType == BillType.Income.getType() || billType == BillType.Cost.getType()) {
            BookOrder bookOrder = DaoUtils.bookOrderDao.findByOrderNumber(userBill.getOrderNumber());
            this.orderId = bookOrder.getOrderId();
            this.orderNumber = bookOrder.getOrderNumber();

            UserInfo userInfo = DaoUtils.userInfoDao.findOne(bookOrder.getUserId() == userBill.getUserId() ? bookOrder.getOwnerId() : bookOrder.getUserId());
            this.userId = userInfo.getUserId();
            this.avatar = Utils.getUrl(userInfo.getAvatar());
            this.nickname = UserUtils.getSafeNickname(userInfo.getNickname());

            this.billDescription = UserAccUtils.billDes(userBill, bookOrder);
        } else {
            this.payType = userBill.getPayType();
            this.tradeNumber = userBill.getTradeNumber();
            this.serialNumber = userBill.getSerialNumber();
            this.account = userBill.getAccount();
            this.name = userBill.getName();
            this.payNickname = userBill.getNickname();
            this.errorMsg = UserAccUtils.desCodeInfo(userBill.getDesCode());
            this.billDescription = UserAccUtils.billDes(userBill, null);
        }

        this.createdTime = userBill.getCreatedTime();

    }

    public long getBillId() {
        return billId;
    }

    public void setBillId(long billId) {
        this.billId = billId;
    }

    public double getBillGold() {
        return billGold;
    }

    public void setBillGold(double billGold) {
        this.billGold = billGold;
    }

    public int getBillType() {
        return billType;
    }

    public void setBillType(int billType) {
        this.billType = billType;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
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

    public Integer getPayType() {
        return payType;
    }

    public void setPayType(Integer payType) {
        this.payType = payType;
    }

    public String getTradeNumber() {
        return tradeNumber;
    }

    public void setTradeNumber(String tradeNumber) {
        this.tradeNumber = tradeNumber;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
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

    public String getPayNickname() {
        return payNickname;
    }

    public void setPayNickname(String payNickname) {
        this.payNickname = payNickname;
    }

    public Date getDealTime() {
        return dealTime;
    }

    public void setDealTime(Date dealTime) {
        this.dealTime = dealTime;
    }

    public Integer getDealStatus() {
        return dealStatus;
    }

    public void setDealStatus(Integer dealStatus) {
        this.dealStatus = dealStatus;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getBillDescription() {
        return billDescription;
    }

    public void setBillDescription(String billDescription) {
        this.billDescription = billDescription;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }
}
