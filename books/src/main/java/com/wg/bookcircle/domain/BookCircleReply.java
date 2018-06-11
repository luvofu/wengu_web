package com.wg.bookcircle.domain;

import com.wg.common.utils.TimeUtils;
import com.wg.user.domain.UserInfo;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by wzhonggo on 8/4/2016.
 */
//书圈回复表
@Cacheable
@Entity
@Table(name = "bookcircle_reply", indexes = {
        @Index(name = "key_userId", columnList = "userId"),
        @Index(name = "key_dynamicId", columnList = "dynamicId"),
        @Index(name = "key_replyObjId", columnList = "replyObjId")})
public class BookCircleReply {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private long replyId;
    private long userId;
    private long dynamicId;
    //回复对象类型:0动态,1回复
    private int replyType;
    // 回复对象id:动态id、回复id
    private long replyObjId;
    @Column(length = 10000)
    private String content;
    //删除标志
    private boolean isDelete = false;
    @org.hibernate.annotations.UpdateTimestamp
    @Column(columnDefinition = "TIMESTAMP")
    private Date updatedTime;
    private Date createdTime = TimeUtils.getCurrentDate();
    private String extendInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", insertable = false, updatable = false)
    private UserInfo userInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dynamicId", insertable = false, updatable = false)
    private BookCircleDynamic bookCircleDynamic;

    public long getReplyId() {
        return replyId;
    }

    public void setReplyId(long replyId) {
        this.replyId = replyId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getDynamicId() {
        return dynamicId;
    }

    public void setDynamicId(long dynamicId) {
        this.dynamicId = dynamicId;
    }

    public int getReplyType() {
        return replyType;
    }

    public void setReplyType(int replyType) {
        this.replyType = replyType;
    }

    public long getReplyObjId() {
        return replyObjId;
    }

    public void setReplyObjId(long replyObjId) {
        this.replyObjId = replyObjId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public BookCircleDynamic getBookCircleDynamic() {
        return bookCircleDynamic;
    }

    public void setBookCircleDynamic(BookCircleDynamic bookCircleDynamic) {
        this.bookCircleDynamic = bookCircleDynamic;
    }

    public boolean isDelete() {
        return isDelete;
    }

    public void setDelete(boolean delete) {
        isDelete = delete;
    }
}
