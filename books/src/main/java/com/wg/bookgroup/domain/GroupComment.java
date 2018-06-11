package com.wg.bookgroup.domain;

import com.wg.common.utils.TimeUtils;
import com.wg.user.domain.UserInfo;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by wzhonggo on 8/4/2016.
 */
//社区评论表
@Cacheable
@Entity
@Table(name = "community_comment", indexes = {@Index(name = "key_communityId", columnList = "communityId"),
        @Index(name = "key_userId", columnList = "userId"),
        @Index(name = "key_replyNum", columnList = "replyNum"),
        @Index(name = "key_createdTime", columnList = "createdTime")})
public class GroupComment {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private long commentId;
    private long userId;
    private long communityId;
    private long goodNum;
    private long replyNum;
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
    @JoinColumn(name = "communityId", insertable = false, updatable = false)
    private BookGroup bookGroup;

    public long getCommentId() {
        return commentId;
    }

    public void setCommentId(long commentId) {
        this.commentId = commentId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(long communityId) {
        this.communityId = communityId;
    }

    public long getGoodNum() {
        return goodNum;
    }

    public void setGoodNum(long goodNum) {
        this.goodNum = goodNum;
    }

    public long getReplyNum() {
        return replyNum;
    }

    public void setReplyNum(long replyNum) {
        this.replyNum = replyNum;
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

    public BookGroup getBookGroup() {
        return bookGroup;
    }

    public void setBookGroup(BookGroup bookGroup) {
        this.bookGroup = bookGroup;
    }

    public boolean isDelete() {
        return isDelete;
    }

    public void setDelete(boolean delete) {
        isDelete = delete;
    }
}
