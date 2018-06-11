package com.wg.useraccount.domain;

import com.alibaba.fastjson.annotation.JSONField;
import com.wg.common.utils.TimeUtils;
import com.wg.user.domain.UserInfo;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Administrator on 2017/3/10 0010.
 */
@Cacheable
@Entity
@Table(name = "user_account",
        indexes = {@Index(name = "ukey_userId", columnList = "userId", unique = true)})
public class UserAccount {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private long accountId;
    private long userId;
    private BigDecimal totalGold;//总金
    private BigDecimal frozenGold;//冻结金
    @org.hibernate.annotations.UpdateTimestamp
    @Column(columnDefinition = "TIMESTAMP")
    private Date updatedTime;
    private Date createdTime = TimeUtils.getCurrentDate();

    @JSONField(serialize = false)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", insertable = false, updatable = false)
    private UserInfo userInfo;

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public BigDecimal getTotalGold() {
        return totalGold;
    }

    public void setTotalGold(BigDecimal totalGold) {
        this.totalGold = totalGold;
    }

    public BigDecimal getFrozenGold() {
        return frozenGold;
    }

    public void setFrozenGold(BigDecimal frozenGold) {
        this.frozenGold = frozenGold;
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
}
