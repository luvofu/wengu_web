package com.wg.message.domain;

import com.wg.common.utils.TimeUtils;
import com.wg.user.domain.UserInfo;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Administrator on 2016/11/20 0020.
 */
@Cacheable
@Entity
@Table(name = "message_push",
        uniqueConstraints = {@UniqueConstraint(name = "ukey_uid_p", columnNames = {"userId", "platform"})},
        indexes = {@Index(name = "ukey_clientId", columnList = "clientId", unique = true)})
public class MessagePush {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private long pushId;
    private String clientId;//一个clientId只对应一个UserId
    private long userId;//一个userId可对应多个个clientId
    private String platform;
    @org.hibernate.annotations.UpdateTimestamp
    @Column(columnDefinition = "TIMESTAMP")
    private Date updatedTime;
    private Date createdTime = TimeUtils.getCurrentDate();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", insertable = false, updatable = false)
    private UserInfo userInfo;

    public long getPushId() {
        return pushId;
    }

    public void setPushId(long pushId) {
        this.pushId = pushId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
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
