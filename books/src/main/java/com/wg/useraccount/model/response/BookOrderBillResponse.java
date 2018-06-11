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
 * Created by Administrator on 2017/2/23 0023.
 */
public class BookOrderBillResponse {
    private long billId;//账单id
    private double billGold;//账单金额
    private int billType;//账单类型
    private Long userId;//账单关系人
    private String avatar;//头像
    private String nickname;//昵称
    private Integer payType;//充值、提现支付方式
    private String billDescription;//账单描述
    private Date dealTime;//充值、提现成功时间
    private Integer dealStatus;//充值、提现状态
    private Date createdTime;//创建时间

    public BookOrderBillResponse(UserBill userBill) {
        this.billId = userBill.getBillId();
        this.billGold = userBill.getBillGold().doubleValue();
        this.billType = userBill.getBillType();
        this.dealTime = userBill.getDealTime();
        this.dealStatus = userBill.getDealStatus();

        if (billType == BillType.Income.getType() || billType == BillType.Cost.getType()) {
            BookOrder bookOrder = DaoUtils.bookOrderDao.findByOrderNumber(userBill.getOrderNumber());
            UserInfo userInfo = DaoUtils.userInfoDao.findOne(bookOrder.getUserId() == userBill.getUserId() ? bookOrder.getOwnerId() : bookOrder.getUserId());
            this.userId = userInfo.getUserId();
            this.avatar = Utils.getUrl(userInfo.getAvatar());
            this.nickname = UserUtils.getSafeNickname(userInfo.getNickname());
            this.billDescription = UserAccUtils.billDes(userBill, bookOrder);
        } else {
            this.payType = userBill.getPayType();
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

    public String getBillDescription() {
        return billDescription;
    }

    public void setBillDescription(String billDescription) {
        this.billDescription = billDescription;
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

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }
}
