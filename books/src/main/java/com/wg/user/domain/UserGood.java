package com.wg.user.domain;

import com.wg.common.utils.TimeUtils;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by wzhonggo on 8/4/2016.
 */
//用户点赞表
@Cacheable
@Entity
@Table(name = "user_good",
        uniqueConstraints = {@UniqueConstraint(name = "ukey_uid_goid_gt", columnNames = {"userId", "goodObjId", "goodType"})},
        indexes = {@Index(name = "key_goodObjId", columnList = "goodObjId"),
                @Index(name = "key_createdTime", columnList = "createdTime")})
public class UserGood {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private long goodId;
    private long userId;
    private int goodType;
    private long goodObjId;
    @org.hibernate.annotations.UpdateTimestamp
    @Column(columnDefinition = "TIMESTAMP")
    private Date updatedTime;
    private Date createdTime = TimeUtils.getCurrentDate();
    private String extendInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", insertable = false, updatable = false)
    private UserInfo userInfo;

    public long getGoodId() {
        return goodId;
    }

    public void setGoodId(long goodId) {
        this.goodId = goodId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getGoodType() {
        return goodType;
    }

    public void setGoodType(int goodType) {
        this.goodType = goodType;
    }

    public long getGoodObjId() {
        return goodObjId;
    }

    public void setGoodObjId(long goodObjId) {
        this.goodObjId = goodObjId;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }

    public String getExtendInfo() {
        return extendInfo;
    }

    public void setExtendInfo(String extendInfo) {
        this.extendInfo = extendInfo;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }
}
