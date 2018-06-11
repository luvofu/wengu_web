package com.wg.user.domain;

import com.wg.common.utils.TimeUtils;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by wzhonggo on 8/4/2016.
 */
//用户收藏表
@Cacheable
@Entity
@Table(name = "user_collection",
        uniqueConstraints = {@UniqueConstraint(name = "ukey_uid_coid_ct", columnNames = {"userId", "collectObjId", "collectType"})},
        indexes = {@Index(name = "key_collectObjId", columnList = "collectObjId")})
public class UserCollection {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private long collectionId;
    private long userId;
    // 收藏类型:0书、1书单
    private int collectType;
    //收藏对象id:书籍id、书单id
    private long collectObjId;
    @org.hibernate.annotations.UpdateTimestamp
    @Column(columnDefinition = "TIMESTAMP")
    private Date updatedTime;
    private Date createdTime = TimeUtils.getCurrentDate();
    private String extendInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", insertable = false, updatable = false)
    private UserInfo userInfo;

    public long getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(long collectionId) {
        this.collectionId = collectionId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getCollectType() {
        return collectType;
    }

    public void setCollectType(int collectType) {
        this.collectType = collectType;
    }

    public long getCollectObjId() {
        return collectObjId;
    }

    public void setCollectObjId(long collectObjId) {
        this.collectObjId = collectObjId;
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
