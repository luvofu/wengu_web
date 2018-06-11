package com.wg.message.domain;

/**
 * Created by wzhonggo on 8/4/2016.
 */

import com.wg.common.utils.TimeUtils;
import com.wg.user.domain.UserInfo;

import javax.persistence.*;
import java.util.Date;

//用户消息表
@Cacheable
@Entity
@Table(name = "user_message",
        indexes = {@Index(name = "key_acceptUserId", columnList = "acceptUserId"),
                @Index(name = "key_sendUserId", columnList = "sendUserId"),
                @Index(name = "key_messageObjId", columnList = "messageObjId")})
public class UserMessage {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private long messageId;
    //接受消息用户id
    private long acceptUserId;
    //发送消息用户id
    private long sendUserId;
    //消息类型:0好友邀请、1书桌邀请、2书圈回复、3社区回复
    private int messageType;
    //消息对象id :0好友ID 、1书桌ID、 2书圈回复ID 、3社区回复ID
    private long messageObjId;
    @Column(length = 10000)
    private String content;
    //    0:未读 1:已读
    private int readStatus;
    //    0:未处理 1:已接受 2:拒绝
    private int dealStatus;
    @org.hibernate.annotations.UpdateTimestamp
    @Column(columnDefinition = "TIMESTAMP")
    private Date updatedTime;
    private Date createdTime = TimeUtils.getCurrentDate();
    private String extendInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "acceptUserId", insertable = false, updatable = false)
    private UserInfo acceptUserInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sendUserId", insertable = false, updatable = false)
    private UserInfo sendUserInfo;

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public long getAcceptUserId() {
        return acceptUserId;
    }

    public void setAcceptUserId(long acceptUserId) {
        this.acceptUserId = acceptUserId;
    }

    public long getSendUserId() {
        return sendUserId;
    }

    public void setSendUserId(long sendUserId) {
        this.sendUserId = sendUserId;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public long getMessageObjId() {
        return messageObjId;
    }

    public void setMessageObjId(long messageObjId) {
        this.messageObjId = messageObjId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getReadStatus() {
        return readStatus;
    }

    public void setReadStatus(int readStatus) {
        this.readStatus = readStatus;
    }

    public int getDealStatus() {
        return dealStatus;
    }

    public void setDealStatus(int dealStatus) {
        this.dealStatus = dealStatus;
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

    public UserInfo getAcceptUserInfo() {
        return acceptUserInfo;
    }

    public void setAcceptUserInfo(UserInfo acceptUserInfo) {
        this.acceptUserInfo = acceptUserInfo;
    }

    public UserInfo getSendUserInfo() {
        return sendUserInfo;
    }

    public void setSendUserInfo(UserInfo sendUserInfo) {
        this.sendUserInfo = sendUserInfo;
    }
}
